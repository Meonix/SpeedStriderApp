package com.example.basecomposeapplication.presentation.error

import com.example.basecomposeapplication.domain.error.ErrorEntity

sealed class PersistenceError : ErrorEntity() {
    data class DatabaseError(override val originalThrowable: Throwable) : PersistenceError()
    data class FileError(override val originalThrowable: Throwable) : PersistenceError()
    data class DataStorePrefError(override val originalThrowable: Throwable) : PersistenceError()
}
