package com.example.extension.objects

import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

fun copyStream(inputStream: InputStream, outputStream: OutputStream): Long {
    var total: Long = 0
    var read: Int
    val buffer = ByteArray(1024)
    while (inputStream.read(buffer).also { read = it } >= 0) {
        outputStream.write(buffer, 0, read)
        total += read.toLong()
    }
    return total
}

fun InputStream.createFile(fileName: String): File? {
    try {
        val file = File(fileName)
        if (file.exists()) {
            val size = this.available()
            Log.d("createFile", size.toString())
            val buffer = ByteArray(size)
            this.read(buffer)
            this.safeClose()
        }
        return file
    } catch (e: IOException) {
        e.printStackTrace()
    }
    this.safeClose()
    return null
}

fun InputStream.getAsString(): String? {
    val reader: BufferedReader?
    val sb = StringBuilder()
    try {
        reader = BufferedReader(InputStreamReader(this))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            sb.append(line).append("\n")
        }
    } catch (e: Exception) {
        return null
    }
    reader.safeClose()
    return sb.toString()
}