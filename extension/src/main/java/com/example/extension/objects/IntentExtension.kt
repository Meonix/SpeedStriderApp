package com.example.extension.objects

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.extension.views.realPathFromURI
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class ImagePickerJob(val lifecycleOwner: LifecycleOwner, val activity: Activity) {

    init {
        observer(lifecycleOwner)
    }

    private val imagePickerIntent: Intent
        get() {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
            }
            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
                    it.type = "image/*"
                }
            return Intent.createChooser(getIntent, "Select Image").also {
                it.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
            }
        }

    private var launcher: ActivityResultLauncher<Intent>? = null

    private var callBack: (Bitmap) -> Unit = {}

    private fun observer(lifecycleOwner: LifecycleOwner) {

        val forResult = ActivityResultContracts.StartActivityForResult()

        val callback = ActivityResultCallback<ActivityResult> {
            getImageFromIntent(it)?.also { image ->
                callBack(image)
            }
        }

        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                launcher = when (lifecycleOwner) {
                    is ComponentActivity -> lifecycleOwner.registerForActivityResult(
                        forResult,
                        callback
                    )

                    is Fragment -> lifecycleOwner.registerForActivityResult(forResult, callback)
                    else -> null
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                launcher?.unregister()
            }
        })
    }

    private fun getImageFromIntent(result: ActivityResult?): Bitmap? {
        val data: Intent = result?.data ?: return null
        val uri: Uri = data.data ?: return null
        val outputStream = ByteArrayOutputStream()
        return try {
            val path: String = activity.realPathFromURI(uri) ?: return null
            val file = File(path)
            val inputStream = FileInputStream(file)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            bitmap
        } catch (ignore: IOException) {
            null
        } finally {
            outputStream.safeClose()
        }
    }

    fun startForResult(callBack: (Bitmap) -> Unit) {
        this.callBack = callBack
        launcher?.launch(imagePickerIntent)
    }
}