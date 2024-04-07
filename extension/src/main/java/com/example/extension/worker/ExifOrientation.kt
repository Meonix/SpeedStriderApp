package com.example.extension.worker

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.exifinterface.media.ExifInterface

/** Transforms rotation and mirroring information into one of the [ExifInterface] constants
 * Class to rotation for image from back or front camera
 * Exif (Exchangeable Image File format) là dữ liệu thông tin của một bức ảnh,
 * trong đó bao gồm tất cả những yếu tố tạo nên một bức ảnh như khẩu độ,
 * ISO, tỉ lệ khung hình, mức cân bằng trắng, tiêu cự
 * */
// tính toán Exif theo rotationDegrees và state camera là front or back
fun computeExifOrientation(rotationDegrees: Int, mirrored: Boolean) = when {
    rotationDegrees == 0 && !mirrored -> ExifInterface.ORIENTATION_NORMAL
    rotationDegrees == 0 && mirrored -> ExifInterface.ORIENTATION_FLIP_HORIZONTAL
    rotationDegrees == 180 && !mirrored -> ExifInterface.ORIENTATION_ROTATE_180
    rotationDegrees == 180 && mirrored -> ExifInterface.ORIENTATION_FLIP_VERTICAL
    rotationDegrees == 270 && mirrored -> ExifInterface.ORIENTATION_TRANSVERSE
    rotationDegrees == 90 && !mirrored -> ExifInterface.ORIENTATION_ROTATE_90
    rotationDegrees == 90 && mirrored -> ExifInterface.ORIENTATION_TRANSPOSE
    rotationDegrees == 270 && mirrored -> ExifInterface.ORIENTATION_ROTATE_270
    rotationDegrees == 270 && !mirrored -> ExifInterface.ORIENTATION_TRANSVERSE
    else -> ExifInterface.ORIENTATION_UNDEFINED
}

/**
 * Helper function used to convert an EXIF orientation enum into a transformation matrix
 * that can be applied to a bitmap.
 *
 * @return matrix - Transformation required to properly display [Bitmap]
 */
fun decodeExifOrientation(exifOrientation: Int): Matrix {
    val matrix = Matrix()

    // Apply transformation corresponding to declared EXIF orientation
    when (exifOrientation) {
        ExifInterface.ORIENTATION_NORMAL -> Unit
        ExifInterface.ORIENTATION_UNDEFINED -> Unit
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1F, 1F)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1F, -1F)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.postScale(-1F, 1F)
            matrix.postRotate(270F)
        }

        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.postScale(-1F, 1F)
            matrix.postRotate(90F)
        }

        // Error out if the EXIF orientation is invalid
        else -> Log.e("EXIF", "Invalid orientation: $exifOrientation")
    }

    // Return the resulting matrix
    return matrix
}