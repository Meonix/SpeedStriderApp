package com.example.extension.worker

import java.text.ParseException
import java.util.Calendar
import java.util.Date

fun Long.parseToDateTime(): Date? {
    return try {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this
        calendar.time
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }
}

fun <T> nonNull(block: (T) -> Unit): (T?) -> Unit {
    return {
        if (it != null) block(it)
    }
}

