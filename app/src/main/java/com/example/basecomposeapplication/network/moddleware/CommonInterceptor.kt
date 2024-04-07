package com.example.basecomposeapplication.network.moddleware


import com.example.basecomposeapplication.data.local.datastore.Pref
import com.example.basecomposeapplication.shared.scheduler.IODispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.CountDownLatch

class CommonInterceptor(private val pref: Pref, private val ioDispatcher: IODispatcher) :
    Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
//        Headers headers = new Headers.Builder()
//            .add("Authorization", "auth-value")
//            .add("User-Agent", "you-app-name")
//            .build();
        val requestBuilder = original.newBuilder()//add more header with name and value.
//            .addHeader("X-AtsShinsotsu-nen", "2022")
//            .cacheControl(CacheControl.FORCE_CACHE) //  Đặt kiểm soát header là của request này, replace lên mọi header đã có.
//            .headers(headers) //Removes all headers on this builder and adds headers.
//            .method(originalRequest.method) // Adds request method and request body
//            .removeHeader("Authorization") // Removes all the headers with this name
        val latch = CountDownLatch(1)

        CoroutineScope(ioDispatcher.dispatcher()).launch {
            requestBuilder.addHeader("Authorization", pref.getAccessToken())
            latch.countDown()
        }

        try {
            latch.await()
        } catch (e: InterruptedException) {
            // handle exception
        }

        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }
}