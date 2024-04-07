package com.example.extension.worker

import android.util.Log

fun logd(msg: String) {
    Log.d("Logcat-App", msg)
}

fun loge(msg: String) {
    Log.e(
        "Error-App", msg
    )
}