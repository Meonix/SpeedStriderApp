package com.example.hardware.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.OrientationEventListener
import android.view.ScaleGestureDetector
import android.view.Surface
import android.view.View
import android.widget.SeekBar
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.WindowMetricsCalculator
import com.example.extension.objects.showToastLong
import com.example.extension.worker.FILE_NAME_FORMAT
import com.example.extension.worker.PHOTO_EXTENSION
import com.example.extension.worker.createFile
import com.example.extension.worker.getCacheDir
import com.example.hardware.camera.CameraHelper.enumerateCameras
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraController(
    private val app: Application,
    private val ioDispatcher: CoroutineDispatcher,
    private val mainDispatcher: CoroutineDispatcher
) {

    interface ViewInterface {
        fun cameraLifecycleOwner(): LifecycleOwner
        fun fragmentView(): Fragment? = null
        fun cameraPreviewView(): PreviewView
        fun seekBarZoomView(): SeekBar? = null
        fun switchButton(): View? = null
        fun cameraImageAnalysis(): Boolean = false
        fun cameraDisplayRotation(): Int = cameraPreviewView().display.rotation

        fun viewLaunch(
            mainDispatcher: CoroutineDispatcher,
            block: suspend CoroutineScope.() -> Unit
        ): Job {
            return cameraLifecycleOwner().lifecycleScope.launch(
                mainDispatcher,
                CoroutineStart.DEFAULT,
                block
            )
        }
    }

    interface CallBack {
        fun cameraOnImageAnalysis(bytes: ByteArray) = Unit
        fun cameraOnStateChanged(state: String) = Unit
        fun cameraOnError(error: String?) = Unit
        fun cameraOnPictureTaken(bitmap: Bitmap) = Unit
        fun cameraOnEstablished() = Unit
    }

    var view: ViewInterface? = null

    //    private val log = Logger("CameraController")
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraManager: CameraManager? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var cameraProvideJob: Job? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var flashModeCapture = ImageCapture.FLASH_MODE_OFF
    private var zoomListener: ScaleGestureDetector.SimpleOnScaleGestureListener? = null
    private val displayManager: DisplayManager get() = app.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    private var isChangeByPinch = false
    private var targetRotation: Int = 0
    private var currentRatioScreen = AspectRatio.RATIO_16_9
    private var flash = false
    var callback: CallBack? = null
    private var analysisExecutor: ExecutorService? = null
    var scaleGestureDetector: ScaleGestureDetector? = null

    // listener orientation
    private var orientationEventListener: OrientationEventListener? = null

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(id: Int) = Unit
        override fun onDisplayRemoved(id: Int) = Unit
        override fun onDisplayChanged(id: Int) {
            if (id == view?.cameraPreviewView()?.display?.displayId) {
                targetRotation = view?.cameraDisplayRotation() ?: 0
            }
        }
    }

    //listener to manual zoom on swipe seekbar
    val seekBarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (isChangeByPinch) {
                isChangeByPinch = false
            } else {
                camera?.cameraControl?.setLinearZoom(progress / 100.toFloat())
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    val isOpened: Boolean get() = camera != null
    var hadOpen: Boolean = false

    init {
        displayManager.registerDisplayListener(displayListener, null)
        currentRatioScreen = getRatio()
    }

    private fun getRatio(): Int {
        //return AspectRatio.RATIO_4_3
        val activity: Activity =
            view?.cameraPreviewView()?.context as? Activity ?: return AspectRatio.RATIO_4_3
        val wmc = WindowMetricsCalculator.getOrCreate()

        val metrics = wmc.computeCurrentWindowMetrics(activity).bounds
        //val size = Size(640, 480)
        val previewRatio = max(metrics.width(), metrics.height()).toDouble() / min(
            metrics.width(),
            metrics.height()
        )
        if (abs(previewRatio - 4.0 / 3) <= abs(previewRatio - 16.0 / 9)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    fun initCamera() {
        view ?: return
        cameraManager =
            view?.fragmentView()?.activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraProvideJob?.cancel()
        cameraProvideJob = view?.viewLaunch(mainDispatcher) {
            try {
                // find camera
                cameraProvider =
                    withContext(ioDispatcher) { ProcessCameraProvider.getInstance(app).get() }
                lensFacing = when {
                    hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                    hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                    else -> throw IllegalStateException("Camera not found")
                }
                updateCameraSwitchButton()
                orientationEventListener =
                    object : OrientationEventListener(view?.cameraPreviewView()?.context) {
                        override fun onOrientationChanged(orientation: Int) {
                            if (imageCapture != null) {
                                // Monitors orientation values to determine the target rotation value
                                // vì target rotation chỉ nhận 4 giá trị -> các giá trị exif sẽ được tính toán
                                // tự động trong trường hợp dùng front camera cũng sẽ được tính toán giá trị
                                // exif nên sẽ không dùng đến hàm computeExifOrientation() của file ExifOrientation
                                val rotation: Int = when (orientation) {
                                    in 45..134 -> Surface.ROTATION_270
                                    in 135..224 -> Surface.ROTATION_180
                                    in 225..314 -> Surface.ROTATION_90
                                    else -> Surface.ROTATION_0
                                }
                                imageCapture?.targetRotation = rotation
                                imageAnalysis?.targetRotation = rotation
                                this@CameraController.targetRotation = rotation
                            }
                        }
                    }
                orientationEventListener?.enable()


                // init camera use case
                bindCameraUseCases()
                setupZoomListener()
            } catch (e: Exception) {
                app.showToastLong(e.message)
            }
        }
    }

    private fun bindCameraUseCases() {
        val v = view ?: return
        v.cameraPreviewView().post {
            camera = initUseCaseAndStart()
        }
        camera?.cameraInfo?.cameraState?.observe(v.cameraLifecycleOwner()) { state ->
            view?.viewLaunch(mainDispatcher) {
                if (state.error != null) {
                    callback?.cameraOnError(state.error.toString())
                } else {
                    callback?.cameraOnStateChanged(state.type.toString())
                }
            }
        }
        callback?.cameraOnEstablished()
    }

    fun getCameras(): List<CameraHelper.CameraItem> {
        cameraManager?.let {
            return enumerateCameras(it)
        }
        return listOf()
    }

    fun focusAtPoint(axisX: Float, axisY: Float) {
        // Get the MeteringPointFactory from PreviewView
        view ?: return
        val factory = view!!.cameraPreviewView().meteringPointFactory

        // Create a MeteringPoint from the tap coordinates
        val point = factory.createPoint(axisX, axisY)

        // Create a MeteringAction from the MeteringPoint, you can configure it to specify the metering mode
        val action = FocusMeteringAction.Builder(point).build()

        // Trigger the focus and metering. The method returns a ListenableFuture since the operation
        // is asynchronous. You can use it get notified when the focus is successful or if it fails.
        camera?.cameraControl?.startFocusAndMetering(action)
    }

    private fun setupZoomListener() {
        //setup zoom listener
        zoomListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val zoomRatio = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1F
                val maxRatio = camera?.cameraInfo?.zoomState?.value?.maxZoomRatio ?: 1F
                val scale = detector.scaleFactor * zoomRatio
                view?.seekBarZoomView()
                    ?.setProgress(((scale - 1) * 100 / (maxRatio - 1)).toInt(), true)
                camera?.cameraControl?.setZoomRatio(scale)
                isChangeByPinch = true
                return true
            }
        }
        view?.cameraPreviewView()?.context?.let {
            scaleGestureDetector =
                ScaleGestureDetector(
                    it,
                    zoomListener as ScaleGestureDetector.SimpleOnScaleGestureListener
                )
        }

    }

    /** Returns true if the device has an available back camera. False otherwise */
    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) == true
    }

    /** Returns true if the device has an available front camera. False otherwise */
    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) == true
    }

    /** Enabled or disabled a button to switch cameras depending on the available cameras */
    private fun updateCameraSwitchButton() {
        try {
            view?.switchButton()?.isEnabled = hasBackCamera() && hasFrontCamera()
        } catch (exception: CameraInfoUnavailableException) {
            view?.switchButton()?.isEnabled = false
        }
    }

    fun changeRatioCamera(newRatio: Int) {
        currentRatioScreen = newRatio
        bindCameraUseCases()
    }

    fun changeFlashMode() {
        flash = !flash
        flashModeCapture = if (flash) {
            ImageCapture.FLASH_MODE_OFF
        } else {
            ImageCapture.FLASH_MODE_ON
        }
        camera?.cameraControl?.enableTorch(flash)
    }

    fun changeCamera() {
        lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }
        // Re-bind use cases to update selected camera
        bindCameraUseCases()
        view?.seekBarZoomView()?.setProgress(0, true)
    }

    /** Enabled or disabled flash function when capture image*/
    private fun updateFlashButton() {
        flashModeCapture = if (flashModeCapture == ImageCapture.FLASH_MODE_ON) {
            ImageCapture.FLASH_MODE_OFF
        } else {
            ImageCapture.FLASH_MODE_ON
        }
    }

    fun stop() {
        cameraProvideJob?.cancel()
        cameraProvider?.unbindAll()
        analysisExecutor?.shutdown()
        displayManager.unregisterDisplayListener(displayListener)
        orientationEventListener?.disable()
        camera = null
    }

    fun takePicture(
        onSuccess: ((Bitmap) -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        val executor = Executors.newSingleThreadExecutor()
        imageCapture?.takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(e: ImageCaptureException) {
                    executor.shutdown()
                    view?.viewLaunch(mainDispatcher) {
                        onError(e)
                        callback?.cameraOnError(e.message)
                    }

                }

                @SuppressLint("UnsafeOptInUsageError")
                override fun onCaptureSuccess(image: ImageProxy) {
                    try {
                        executor.shutdown()
                        val rotation = image.imageInfo.rotationDegrees
                        val bitmap = image.image?.toBitmap().rotate(rotation)
                            ?: throw NullPointerException("bitmap is null")
                        view?.viewLaunch(mainDispatcher) {
                            delay(300)
                            onSuccess?.invoke(bitmap)
                            callback?.cameraOnPictureTaken(bitmap)
                        }
                    } catch (e: Exception) {
                        view?.viewLaunch(mainDispatcher) {
                            onError?.invoke(e)
                            callback?.cameraOnError(e.message)
                        }
                    }
                    image.close()
                }
            })

    }

    fun takePictureAndSave(
        onSuccess: ((Bitmap, String?) -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        val v = view ?: return
        val folderPath = getCacheDir(context = app.applicationContext)
        val imageFile: File = createFile(
            folderPath,
            FILE_NAME_FORMAT,
            PHOTO_EXTENSION,
            app.applicationContext.resources.configuration.locales[0] ?: Locale.US
        )
        // Setup image capture metadata
        val metadata = ImageCapture.Metadata().apply {
            // Mirror image when using the front camera
            isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
        }
        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(imageFile)
            .setMetadata(metadata)
            .build()
        val executor = Executors.newSingleThreadExecutor()
        v.cameraLifecycleOwner().lifecycleScope.launch(ioDispatcher) {
            imageCapture?.takePicture(
                outputOptions,
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(e: ImageCaptureException) {
                        executor.shutdown()
                        view?.viewLaunch(mainDispatcher) {
                            onError(e)
                            callback?.cameraOnError(e.message)
                        }
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        executor.shutdown()
                        flash = false
                        camera?.cameraControl?.enableTorch(flash)
                        try {
                            val savedUri = output.savedUri ?: Uri.fromFile(imageFile)
                            val fileOutput = savedUri.path
                            // Bitmap type hardware
                            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                val source =
                                    ImageDecoder.createSource(app.contentResolver, savedUri)
                                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                                    decoder.isMutableRequired = true
                                }
                            } else {
                                @Suppress("DEPRECATION")
                                MediaStore.Images.Media.getBitmap(app.contentResolver, savedUri)
                            }
                            view?.viewLaunch(mainDispatcher) {
                                onSuccess?.invoke(bitmap, fileOutput)
                                callback?.cameraOnPictureTaken(bitmap)
                            }

                        } catch (e: Exception) {
                            view?.viewLaunch(mainDispatcher) {
                                onError?.invoke(e)
                                callback?.cameraOnError(e.message)
                            }

                        }
                    }
                }
            )
        }
    }

    fun zoomOut() {
        val minRatio = camera?.cameraInfo?.zoomState?.value?.minZoomRatio ?: 1F
        camera?.cameraControl?.setZoomRatio(minRatio)
    }

    @SuppressLint("RestrictedApi")
    private fun initUseCaseAndStart(): Camera? {
        val v = view ?: return null
        val aspectRatio: Int = currentRatioScreen
        val rotation = v.cameraDisplayRotation()

        val useCases = mutableListOf<UseCase>()

        val preview = Preview.Builder()
            .setTargetAspectRatio(aspectRatio)
            .setTargetRotation(rotation)
            .build()
        useCases.add(preview)

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(aspectRatio)
            .setFlashMode(flashModeCapture)
            .build()
        useCases.add(imageCapture!!)


        /**
         * [androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888]
         * [androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888]
         */
        if (v.cameraImageAnalysis()) {
            imageAnalysis = ImageAnalysis.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(rotation)
                //.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build()

            analysisExecutor = Executors.newSingleThreadExecutor()
            imageAnalysis?.setAnalyzer(analysisExecutor!!) { imageProxy: ImageProxy ->
                callback?.also {
                    val buffer = imageProxy.planes[0].buffer
                    val data = buffer.toByteArray()
                    it.cameraOnImageAnalysis(data)
                }
                imageProxy.close()
            }
            useCases.add(imageAnalysis!!)
        }

        cameraProvider?.unbindAll()
        preview.setSurfaceProvider(v.cameraPreviewView().surfaceProvider)
        return cameraProvider?.safeBindUseCase(*useCases.toTypedArray())
    }

    private fun ProcessCameraProvider?.safeUnbind(vararg arr: UseCase?) {
        arr.filterNotNull().forEach {
            try {
                this?.unbind(it)
            } catch (e: Exception) {
                callback?.cameraOnError(e.message)
            }
        }
    }

    private fun ProcessCameraProvider?.safeBindUseCase(vararg arr: UseCase?): Camera? {
        val v = view ?: return null
        return try {
            // CameraSelector
            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
            val useCases = arr.filterNotNull().toTypedArray()
            if (useCases.isEmpty()) return null
            this?.bindToLifecycle(
                v.cameraLifecycleOwner(),
                cameraSelector,
                *useCases
            )
        } catch (e: Exception) {
            callback?.cameraOnError(e.message)
            null
        }
    }

    companion object {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }
    }

}