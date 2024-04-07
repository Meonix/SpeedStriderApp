package com.example.basecomposeapplication.presentation.error

interface ErrorHandler {

    fun proceed(throwable: Throwable?): String
}
