package com.example.extension.worker

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.example.extension.objects.showToastShort
import java.io.*
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


const val KILOBYTE_UNIT = "Kb"
const val MEGABYTE_UNIT = "Mb"
const val GIGABYTE_UNIT = "Gb"
const val TERABYTE_UNIT = "Tb"
const val PHOTO_EXTENSION = "jpg"
const val TEMP_FOLDER_NAME = "temp"
const val FILE_NAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
val EXTENSION_WHITELIST = arrayOf("JPG")


fun createFile(folderPath: String, format: String, extension: String, locale: Locale): File {
    val sdf = SimpleDateFormat(format, locale)
    return when (extension) {
        PHOTO_EXTENSION -> File(folderPath, "IMG_${sdf.format(Date())}.$extension")
        else -> File(folderPath, "VID_${sdf.format(Date())}.$extension")
    }
}

/** Use external media if it is available, our app's file directory otherwise */
fun getOutputDirectory(app: Application, folderName: String): File {
    val appContext = app.applicationContext
    val mediaDir = app.externalMediaDirs.firstOrNull()?.let {
        File(
            it, folderName
//                appContext.resources.getString(R.string.app_name)
        ).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else appContext.filesDir
}

fun getCacheDir(context: Context): String {
    //get Cache folder path
    var folderPath = context.cacheDir.absolutePath
    //check if image folder in cache folder
    if (!File(folderPath + File.separator + TEMP_FOLDER_NAME).exists()) {
        //if not exits -> create image folder
        val newFolder = File(folderPath, TEMP_FOLDER_NAME)
        newFolder.mkdirs()
        if (newFolder.exists()) {
            //get path of image directory
            folderPath = newFolder.absolutePath
        }
    } else {//if exists -> get image folder
        folderPath = folderPath + File.separator + TEMP_FOLDER_NAME
    }
    return folderPath
}

fun getSizeOfFileUnit(unit: String, size: Double): Double {
    val newSize = when (unit) {
        KILOBYTE_UNIT -> size / 1024
        MEGABYTE_UNIT -> size / 1024 / 1024
        GIGABYTE_UNIT -> size / 1024 / 1024 / 1024
        TERABYTE_UNIT -> size / 1024 / 1024 / 1024
        else -> 0
    }
    return newSize.toDouble()
}

fun createFileWithExistsName(folderPath: String, fileName: String, extension: String?): File {
    return if (extension == null) {
        File(folderPath, fileName)
    } else {
        File(folderPath, "$fileName.$extension")
    }
}

fun trimFolderCache(folderPath: String) {
    try {
        val dir = File(folderPath)
        if (dir.isDirectory) {
            deleteDir(dir)
        }
    } catch (e: Exception) {
        e.message?.let { Log.d("TRIM-CACHE", it) }
    }
}

fun deleteDir(dir: File?): Boolean {
    if (dir != null && dir.isDirectory) {
        val children = dir.list()
        if (children != null) {
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
    }

    // The directory is now empty so delete it
    return dir!!.delete()
}


fun checkFolderIsExistsIfNoteCreateOne(filePath: String, folderName: String): String {
    if (!File("$filePath/$folderName").exists()) {
        //if not exits -> create image folder
        val newFolder = File(filePath, folderName)
        newFolder.mkdirs()
        if (newFolder.exists()) {
            //get path of image directory
            return newFolder.absolutePath
        }
    }//else
    return "$filePath/$folderName"
}

//@Throws(IOException::class)
//fun copyFile(source: File?, destination: File?) {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        FileUtils.copy(FileInputStream(source), FileOutputStream(destination))
//    }
//}

fun copyFileOrDirectory(srcDir: String, dstDir: String) {
    try {
        val src = File(srcDir)
        val dst = File(dstDir, src.name)
        if (src.isDirectory) {
            val files = src.list()
            files?.let {
                val filesLength = it.size
                for (i in 0 until filesLength) {
                    val src1 = File(src, it[i]).path
                    val dst1 = dst.path
                    copyFileOrDirectory(src1, dst1)
                }
            }
        } else {
            copyFile(src, dst)
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

@Throws(IOException::class)
fun copyFile(sourceFile: File?, destFile: File) {
    if (destFile.parentFile != null) {
        if (!destFile.parentFile!!.exists()) {
            destFile.parentFile?.mkdirs()
        }
    }
    if (!destFile.exists()) {
        destFile.createNewFile()
    }
    var source: FileChannel? = null
    var destination: FileChannel? = null
    try {
        source = FileInputStream(sourceFile).channel
        destination = FileOutputStream(destFile).channel
        destination.transferFrom(source, 0, source.size())
    } finally {
        source?.close()
        destination?.close()
    }
}

suspend fun getFileFromInputStream(tempFile: File, inputStream: InputStream): File =
    suspendCoroutine { result ->
        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        result.resume(tempFile)
    }

fun scanFileToMediaStorage(context: Context, file: File) {
    val mimeType = MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(file.extension)
    MediaScannerConnection.scanFile(
        context,
        arrayOf(file.absolutePath),
        arrayOf(mimeType)
    ) { _, uri ->
        logd("Image capture scanned into media store: $uri")
    }
}

fun saveImageToStorage(
    filePath: String?,
    context: Context?,
    mediaStoreFilePath: File? = null,
    bitmap: Bitmap?,
    folderNameMediaStore: String
) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            filePath ?: return
            val file = File(filePath)
            val resolver = context?.contentResolver
            val mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(file.extension)
            val contentValue = ContentValues()
            contentValue.put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                filePath.substringAfterLast("/")
            )
            contentValue.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            contentValue.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + File.separator
                        + folderNameMediaStore
            )
            val imageUri =
                resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValue)
            val fileOutputStream = imageUri?.let { uri ->
                resolver.openOutputStream(uri)
            }
            if (fileOutputStream != null) {
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            }
            context.showToastShort("Save image successful")
        } else {
            filePath ?: return
            mediaStoreFilePath ?: return
            copyFileOrDirectory(filePath, mediaStoreFilePath.absolutePath)
            // If the folder selected is an external media directory, this is
            // unnecessary but otherwise other apps will not be able to access our
            // images unless we scan them using [MediaScannerConnection]
            val newFile =
                File(mediaStoreFilePath.absolutePath + "/" + filePath.substringAfterLast("/"))
            context?.let { it1 -> scanFileToMediaStorage(it1, newFile) }
            context.showToastShort("Save image successful")
        }
    } catch (e: Exception) {
        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
    }
}

fun Closeable?.safeClose() {
    try {
        this?.close()
    } catch (ignored: Exception) {
    }
}
