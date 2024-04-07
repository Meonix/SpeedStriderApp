package com.example.resource

import android.content.Context
import com.example.extension.objects.parse
import com.example.extension.worker.safeClose
import com.google.gson.JsonObject
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

fun Context.getAssetAsInputStream(fileName: String): InputStream? {
    return try {
        return assets.open(fileName)
    } catch (e: FileNotFoundException) {
        null
    }
}

fun Context.getAssetAsBytes(fileName: String): ByteArray? {
    return try {
        val inputStream = assets.open(fileName)
        val bytes = ByteArray(inputStream.available())
        inputStream.read(bytes)
        inputStream.safeClose()
        return bytes
    } catch (e: FileNotFoundException) {
        null
    }
}

fun Context.getAssetAsString(filename: String): String? {
    return try {
        val sb = StringBuilder()
        BufferedReader(InputStreamReader(assets.open(filename))).useLines { lines ->
            lines.forEach {
                sb.append(it)
            }
        }
        return sb.toString()
    } catch (e: FileNotFoundException) {
        null
    }
}

fun Context.getAssetAsFile(filename: String): File? {
    try {
        val inputStream = assets.open(filename)
        val file = File("${cacheDir}/${filename.substringAfterLast("/")}")
        val outputStream: OutputStream = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var length = 0
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        outputStream.close()
        inputStream.close()
        return file
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun Context.getAssetAsJsonObject(fileName: String): JsonObject? {
    val s = getAssetAsString(fileName)
    return s.parse(JsonObject::class)
}

fun Context.getAssetAsJsonArray(fileName: String): JsonObject? {
    val s = getAssetAsString(fileName)
    return s.parse(JsonObject::class)
}

