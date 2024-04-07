package com.example.basecomposeapplication.network.exception

import com.example.basecomposeapplication.domain.error.ErrorEntity

sealed class ApiError : ErrorEntity() {

    data class HttpError(
        override val originalThrowable: Throwable,
        val errorResponse: BaseErrorResponse?
    ) : ApiError()

    data class ServerError(override val originalThrowable: Throwable) : ApiError()
    data class NetworkError(override val originalThrowable: Throwable) : ApiError()
    data class CoroutineJobCanceledError(override val originalThrowable: Throwable) : ApiError()
    data class DataBaseError(override val originalThrowable: Throwable) : ApiError()
    data class UnexpectedError(override val originalThrowable: Throwable) : ApiError()
}
