package com.example.extension.objects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.os.Environment
import android.provider.MediaStore
import android.util.Size
import androidx.annotation.RequiresPermission
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun Closeable?.safeClose() {
    try {
        this?.close()
    } catch (ignored: Exception) {
    }
}

val File.thumbnail: Bitmap?
    get() {
        try {
            val file = File(path)
            if (file.absoluteFile.exists()) {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ThumbnailUtils.createVideoThumbnail(file, Size(96, 96), CancellationSignal())
                } else {
                    ThumbnailUtils.createVideoThumbnail(
                        path,
                        MediaStore.Video.Thumbnails.MICRO_KIND
                    )
                }
            }
        } catch (e: Exception) {

        }
        return null
    }


val Context.packageDir: File
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) dataDir else filesDir
    }

val Context.externalDir: File
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getExternalFilesDir(null) ?: File("")
        } else {
            @Suppress("DEPRECATION")
            Environment.getExternalStorageDirectory()
        }
    }

val Context.externalPath: String get() = externalDir.absolutePath

val downloadDir: File get() = publicDir(Environment.DIRECTORY_DOWNLOADS)

fun packageDir(context: Context, dir: String, fileName: String? = null): File {
    val parent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.getExternalFilesDir(dir) ?: File("")
    } else {
        @Suppress("DEPRECATION")
        Environment.getExternalStoragePublicDirectory(dir)
    }
    if (fileName != null) {
        return File(parent, fileName)
    }
    return parent
}

fun publicDir(dir: String, fileName: String? = null): File {
    val parent = Environment.getExternalStoragePublicDirectory(dir)
    if (fileName != null) {
        return File(parent, fileName)
    }
    return parent
}

fun copyFolder(sourceFile: File, destinationFile: File) {
    println("copy files from '$sourceFile' to '$destinationFile'")
    destinationFile.mkdirs()
    copyFiles(sourceFile, destinationFile)
}

fun copyFiles(sourceFile: File, destinationFile: File) {
    try {
        val folderFiles = sourceFile.list() ?: return
        sourceFile.mkdirs()
        for (file in folderFiles) {
            val childSourceFile = File(sourceFile.path, file)
            val childDestinationFile = File(destinationFile.path, file)
            if (file.contains(".")) {
                println("copying '$file''")
                copyFile(childSourceFile, childDestinationFile)
            } else {
                copyFiles(childSourceFile, childDestinationFile)
            }
        }
    } catch (e: Exception) {
        println(e.message)
    }
}

fun copyFile(sourceFile: File, destinationFile: File) {
    var inputStream: InputStream? = null
    var fos: FileOutputStream? = null
    try {
        // create destination file
        if (destinationFile.absoluteFile.exists()) {
            destinationFile.delete()
        }
        destinationFile.createNewFile()

        // write buffer
        inputStream = sourceFile.inputStream()
        fos = FileOutputStream(destinationFile)
        val buffer = ByteArray(1024)
        var read: Int = inputStream.read(buffer)
        while (read >= 0) {
            fos.write(buffer, 0, read)
            read = inputStream.read(buffer)
        }
    } catch (e: IOException) {
        println("copy file error: ${e.message}")
    }
    inputStream?.safeClose()
    fos?.flush()
    fos.safeClose()
}

fun CoroutineScope.unzip(
    zipFile: File,
    targetDirectory: File,
    onCompleted: suspend CoroutineScope.() -> Unit
) {
    launch(Dispatchers.IO) {
        try {
            ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { zipInputStream ->
                try {
                    var ze: ZipEntry? = null
                    var count: Int
                    val buffer = ByteArray(8192)
                    while (zipInputStream.nextEntry.also { ze = it } != null) {
                        val zipEntry = ze ?: break
                        val file = File(targetDirectory, zipEntry.name)
                        val dir = if (zipEntry.isDirectory) file else file.parentFile
                        if (!dir.isDirectory && !dir.mkdirs()) throw FileNotFoundException(
                            "Failed to ensure directory: " +
                                    dir.absolutePath
                        )
                        if (zipEntry.isDirectory) continue
                        val fileOutputStream = FileOutputStream(file)
                        fileOutputStream.use { fileOut ->
                            while (zipInputStream.read(buffer)
                                    .also { count = it } != -1
                            ) fileOut.write(
                                buffer,
                                0,
                                count
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
        this.launch(Dispatchers.IO, CoroutineStart.DEFAULT, onCompleted)
    }
}


@RequiresPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
fun writeFile(context: Context, fileName: String, bytes: ByteArray): File? {
    return try {
        val file = File(context.externalDir, fileName)
        val outputStream = FileOutputStream(file)
        outputStream.write(bytes)
        outputStream.flush()
        outputStream.safeClose()
        file
    } catch (e: Exception) {
        null
    }
}

@RequiresPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
fun readFile(context: Context, fileName: String): String {
    val file = File(context.externalDir, fileName)
    val text = java.lang.StringBuilder()
    try {
        val br = BufferedReader(FileReader(file))
        var line: String?
        while (br.readLine().also { line = it } != null) {
            text.append(line)
            text.append('\n')
        }
        br.safeClose()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return text.toString()
}

fun File.getUri(context: Context): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(context, context.packageName + ".provider", this)
    } else {
        Uri.fromFile(this)
    }
}

val File.size: Long get() = length() / 1024

val File.getWebpImage: ByteArray
    get() {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeStream(FileInputStream(this), null, options)
        val stream = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            bitmap?.compress(Bitmap.CompressFormat.WEBP_LOSSY, 100, stream)
        } else {
            bitmap?.compress(Bitmap.CompressFormat.WEBP, 100, stream)
        }
        return stream.toByteArray()
    }

private fun saveBitmapToCacheFolder(
    context: Context,
    bitmap: Bitmap,
    imageName: String,
    imageFolderName: String
): File? {
    val cacheDir: File? = context.cacheDir
    val imageFolder = File(cacheDir, imageFolderName)

    try {
        if (!imageFolder.exists()) {
            imageFolder.mkdirs()
        }
        val file = File(imageFolder, imageName)
        val out = FileOutputStream(file)
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            100, out
        )
        out.flush()
        out.close()
        return file
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}