package com.example.extension.objects

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.view.View
import androidx.annotation.DrawableRes
import androidx.exifinterface.media.ExifInterface
import com.example.extension.worker.decodeExifOrientation
import com.example.extension.worker.logd
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToInt

fun Bitmap.toByteArray(): ByteArray {
    return try {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        stream.close()
        byteArray
    } catch (e: Exception) {
        byteArrayOf()
    }
}

fun Bitmap.getRectCrop(rect: Rect): Rect {
    val top = if (rect.top < 0) 0 else rect.top
    val left = if (rect.left < 0) 0 else rect.left
    val right = if (rect.right > this.width) this.width else rect.right
    val bottom = if (rect.bottom > this.height) this.height else rect.bottom
    return Rect(left, top, right, bottom)
}

fun Bitmap.scaleDown(
    realImage: Bitmap, maxImageSize: Float,
    filter: Boolean
): Bitmap? {
    return try {
        val ratio = (maxImageSize / realImage.width).coerceAtMost(maxImageSize / realImage.height)
        val width = (ratio * realImage.width).roundToInt()
        val height = (ratio * realImage.height).roundToInt()
        Bitmap.createScaledBitmap(
            realImage, width,
            height, filter
        )
    } catch (e: Exception) {
        null
    }
}

fun Bitmap?.crop(rect: Rect): Bitmap? {
    this ?: return null
    return try {
        val cropBitmap = Bitmap.createBitmap(
            this,
            rect.left, rect.top, rect.width(), rect.height()
        )
        cropBitmap
    } catch (e: Exception) {
        null
    }
}

fun Bitmap?.clone(): Bitmap? {
    this ?: return null
    return this.copy(this.config, true)
}

private fun ByteArray.rgb8ToArgb(width: Int, height: Int): IntArray? {
    try {
        val frameSize = width * height
        val rgb = IntArray(frameSize)
        var index = 0
        for (j in 0 until height) {
            for (i in 0 until width) {
                val B = this[3 * index].toInt()
                val G = this[3 * index + 1].toInt()
                val R = this[3 * index + 2].toInt()
                rgb[index] = (R and 0xff) or (G and 0xff shl 8) or (B and 0xff shl 16)
                index++
            }
        }
        return rgb
    } catch (e: Exception) {
        logd("rgb8ToArgb : ${e.message}")
        return null
    }
}

fun ByteArray?.getBitmapFromFrameNotFlip(width: Int, height: Int): Bitmap? {
    this ?: return null
    return try {
        val argb = this.rgb8ToArgb(width, height)
            ?: return null
        return Bitmap.createBitmap(argb, width, height, Bitmap.Config.RGB_565)
    } catch (e: OutOfMemoryError) {
        null
    } catch (e: Exception) {
        null
    }
}

/** Utility function used to read input file into a byte array */
fun loadInputBuffer(filePath: String): ByteArray {
    val inputFile = File(filePath)
    return BufferedInputStream(inputFile.inputStream()).let { stream ->
        ByteArray(stream.available()).also {
            stream.read(it)
            stream.close()
        }
    }
}

fun Bitmap.rotateBitMap(angle: Int): Bitmap? {
    val matrix = Matrix()
    matrix.postRotate(angle.toFloat())
    return Bitmap.createBitmap(
        this, 0, 0, this.width, this.height,
        matrix, true
    )
}

suspend fun getBitmapFromFilePath(
    ioDispatcher: CoroutineDispatcher,
    path: String,
    coroutineScope: CoroutineScope
): Bitmap? =
    suspendCoroutine { result ->
        try {
            coroutineScope.launch(ioDispatcher) {
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap = BitmapFactory.decodeFile(path, options)
                result.resume(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            result.resume(null)
        }
    }

fun Bitmap.scaleBitmap(context: Context, ratioScale: Int): Bitmap {
    //get height and width of device
    val displayMetrics = context.resources.displayMetrics
    //get new width following ratio of screen
    var width = displayMetrics.widthPixels
    width *= ratioScale   // 7/10 ratio of screen
    var height = width  // height = width => we have square
    // => we have width of final bitmap
    // we take height multiplied ratio of original bitmap
    // rotatedBitmap.height / rotatedBitmap.width is ratio of height in original bitmap size
    //Ex : ratio of height = height/width
    height = height * this.height / this.width
    return Bitmap.createScaledBitmap(this, width, height, true)
}

fun getBitmapWithExif(inputStream: ByteArrayInputStream): Bitmap {
    //this code to save inputStream to sourceStream
    val sourceStream = ByteArrayOutputStream()
    val buffer = ByteArray(1024)
    var len: Int
    while (inputStream.read(buffer).also { len = it } > -1) {
        sourceStream.write(buffer, 0, len)
    }
    sourceStream.flush()
    // Open new InputStreams using recorded bytes
    // Can be repeated as many times as you wish
    val is1 = ByteArrayInputStream(sourceStream.toByteArray())
    val is2 = ByteArrayInputStream(sourceStream.toByteArray())
    val bm = BitmapFactory.decodeStream(is1)
    val ei = ExifInterface(is2) // get Exif Info
    val orientation = ei.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    ) // get orientation info via attribute tag
    val bitmapTransformation = decodeExifOrientation(orientation)
    return Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, bitmapTransformation, true)
}

/**
 * Decode and sample down a bitmap from resources to the requested width and height.
 *
 * @param res       The resources object containing the image data
 * @param resId     The resource id of the image data
 * @param width  The requested width of the resulting bitmap
 * @param height The requested height of the resulting bitmap
 * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
 * that are equal to or greater than the requested width and height
 */
fun bitmapFromResources(
    context: Context,
    @DrawableRes resId: Int,
    width: Int,
    height: Int
): Bitmap {
    // First decode with inJustDecodeBounds=true to check dimensions
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeResource(context.resources, resId, options)

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, width, height)
    // END_INCLUDE (read_bitmap_dimensions)

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false
    return BitmapFactory.decodeResource(context.resources, resId, options)
}

/**
 * Calculate an inSampleSize for use in a [BitmapFactory.Options] object when decoding
 * bitmaps using the decode* methods from [BitmapFactory]. This implementation calculates
 * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
 * having a width and height equal to or larger than the requested width and height.
 *
 * @param options   An options object with out* params already populated (run through a decode*
 * method with inJustDecodeBounds==true
 * @param width  The requested width of the resulting bitmap
 * @param height The requested height of the resulting bitmap
 * @return The value to be used for inSampleSize
 */
fun calculateInSampleSize(options: BitmapFactory.Options, width: Int, height: Int): Int {
    // BEGIN_INCLUDE (calculate_sample_size)
    // Raw height and width of image
    val h = options.outHeight
    val w = options.outWidth
    var inSampleSize = 1

    if (h > height || w > width) {

        val halfHeight = h / 2
        val halfWidth = w / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize > height && halfWidth / inSampleSize > width) {
            inSampleSize *= 2
        }

        // This offers some additional logic in case the image has a strange
        // aspect ratio. For example, a panorama may have a much larger
        // width than height. In these cases the total pixels might still
        // end up being too large to fit comfortably in memory, so we should
        // be more aggressive with sample down the image (=larger inSampleSize).

        var totalPixels = (width * height / inSampleSize).toLong()

        // Anything more than 2x the requested pixels we'll sample down further
        val totalReqPixelsCap = width * height * 2L

        while (totalPixels > totalReqPixelsCap) {
            inSampleSize *= 2
            totalPixels /= 2
        }
    }
    return inSampleSize
    // END_INCLUDE (calculate_sample_size)
}

val Uri.thumbnail: Bitmap?
    get() {
        val path = this.path ?: return null
        val file = File(path)
        return file.thumbnail
    }

private fun takeScreenShotOfView(view: View, height: Int, width: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val bgDrawable = view.background
    if (bgDrawable != null) {
        bgDrawable.draw(canvas)
    } else {
        canvas.drawColor(Color.WHITE)
    }
    view.draw(canvas)
    return bitmap
}