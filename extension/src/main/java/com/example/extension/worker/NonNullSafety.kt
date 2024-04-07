package com.example.extension.worker

import android.util.Log


fun Any.name(): String = this::class.java.simpleName

fun tryExt(block: () -> Unit = {}) {
    try {
        block()
    } catch (e: Exception) {
        Log.d("ERROR ${block.name()}", "$e")
    }
}