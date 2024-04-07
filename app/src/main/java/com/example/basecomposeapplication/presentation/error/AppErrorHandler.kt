package com.example.basecomposeapplication.presentation.error

import com.example.basecomposeapplication.network.exception.ApiError

class AppErrorHandler : ErrorHandler {
    // Handle error and show it to UI
    override fun proceed(throwable: Throwable?): String {
        if (throwable is ApiError.CoroutineJobCanceledError) return ""
        return getErrorMessage(throwable)
    }

    private fun getErrorMessage(
        throwable: Throwable?,
    ): String {
        return when (throwable) {
            is ApiError -> when (throwable) {
                is ApiError.NetworkError -> {
                    "Network error, please check your connection."
                }

                is ApiError.HttpError -> {
                    throwable.errorResponse?.message.toString()
                }

                is ApiError.ServerError -> {
                    // Should not combine this error with another error (like else case) for debug purpose
                    throwable.originalThrowable.message.toString()
                }

                else -> throwable.originalThrowable.message.toString()
            }

            is PersistenceError -> when (throwable) {
                is PersistenceError.DataStorePrefError,
                is PersistenceError.FileError,
                is PersistenceError.DatabaseError -> {
                    // Should not combine this error with another error (like else case) for debug purpose
                    throwable.message.toString()
                }
            }

            else -> throwable?.message.toString()
        }
    }
}
