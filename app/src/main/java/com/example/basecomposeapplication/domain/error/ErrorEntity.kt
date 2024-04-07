package com.example.basecomposeapplication.domain.error

abstract class ErrorEntity : Throwable() {
    abstract val originalThrowable: Throwable
}
