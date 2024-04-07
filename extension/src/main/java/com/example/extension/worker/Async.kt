package com.example.extension.worker

import android.os.Handler
import android.os.Looper

val uiHandler: Handler get() = Handler(Looper.getMainLooper())

fun post(block: () -> Unit) {
    uiHandler.post { block() }
}

fun post(delay: Long, block: () -> Unit) {
    uiHandler.postDelayed({ block() }, delay)
}

