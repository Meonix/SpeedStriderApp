package com.example.basecomposeapplication.network.api

import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceGenerator {

    private const val CONNECT_TIMEOUT = 30000L
    private const val READ_TIMEOUT = 30000L
    private const val WRITE_TIMEOUT = 30000L

    fun <T> generate(
        baseUrl: String,
        serviceClass: Class<T>,
        authenticator: Authenticator?,
        vararg interceptors: Interceptor
    ): T {
        val okHttpBuilder = OkHttpClient().newBuilder()
        if (authenticator != null) {
            okHttpBuilder.authenticator(authenticator)
        }
        for (itr in interceptors) {
            okHttpBuilder.addInterceptor(itr)
        }
        okHttpBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpBuilder.build())
            .build()
        return retrofit.create(serviceClass)
    }
}
