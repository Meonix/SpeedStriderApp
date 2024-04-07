package com.example.basecomposeapplication.data.repository.error

import android.database.sqlite.SQLiteException
import com.example.basecomposeapplication.domain.error.ErrorEntity
import com.example.basecomposeapplication.domain.error.ErrorMapper
import com.example.basecomposeapplication.network.exception.ApiError
import com.example.basecomposeapplication.network.exception.BaseErrorResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

object ApiErrorMapper : ErrorMapper {
    private const val SERVER_ERROR_CODE = 500
    override fun map(throwable: Throwable): ErrorEntity {
        return when (throwable) {
            is HttpException -> {
                val errorBody =
                    throwable.response()?.errorBody() ?: return ApiError.UnexpectedError(throwable)
                //If it = null that should not be happened, in this case we leave it as an unexpected error
                val errorResponse = deserializeServerError(errorBody.string())
                return if (errorResponse != null && errorResponse.code >= SERVER_ERROR_CODE ||
                    throwable.code() >= SERVER_ERROR_CODE
                ) {
                    // Service downs
                    ApiError.ServerError(Throwable(errorResponse?.message, throwable))
                } else {
                    ApiError.HttpError(
                        Throwable(errorResponse?.message, throwable),
                        errorResponse
                    )
                }
            }

            is IOException -> ApiError.NetworkError(throwable)
            is CancellationException -> ApiError.CoroutineJobCanceledError(throwable)
            is SQLiteException -> ApiError.DataBaseError(throwable)
            else -> ApiError.UnexpectedError(throwable)
        }
    }

    @Suppress("unused")
    fun getErrorMessage(throwable: Throwable): String {
        if (throwable !is HttpException) return ""
        val response = throwable.response()
        if (response?.errorBody() != null) {
            try {
                val errorResponse = response.errorBody()!!.string()
                deserializeServerError(errorResponse) ?: return response.message()
            } catch (ignored: IOException) {
            }
        }
        return ""
    }

    @Suppress("unused")
    fun getErrorCode(throwable: Throwable?): Int {
        if (throwable !is HttpException) return 0
        val response = throwable.response()
        return response?.code() ?: 0
    }

    // convert JSON error string to BaseErrorResponse and return it
    private fun deserializeServerError(errorString: String): BaseErrorResponse? {
        val gson = GsonBuilder().create()
        return try {
            gson.fromJson(errorString, BaseErrorResponse::class.java)
        } catch (e: JsonSyntaxException) {
            null
        }
    }
}
