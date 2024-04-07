package com.example.basecomposeapplication.network.exception

import com.google.gson.annotations.SerializedName

data class BaseErrorResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<Error>?
) {
    data class Error(
        @SerializedName("key") val key: String?,
        @SerializedName("errorCode") val errorCode: Int?,
        @SerializedName("message") val message: String?
    )
}
