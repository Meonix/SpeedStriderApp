package com.example.basecomposeapplication.di

import android.app.Application
import com.example.basecomposeapplication.BuildConfig
import com.example.basecomposeapplication.data.local.datastore.Pref
import com.example.basecomposeapplication.network.api.Api
import com.example.basecomposeapplication.network.api.ServiceGenerator
import com.example.basecomposeapplication.network.moddleware.CommonInterceptor
import com.example.basecomposeapplication.network.util.Network
import com.example.basecomposeapplication.shared.scheduler.IODispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Singleton
    @Provides
    fun provideApiService(pref: Pref, ioDispatcher: IODispatcher): Api {
        return ServiceGenerator.generate(
            BuildConfig.BASE_URL,
            /*Api interface*/Api::class.java,
            null,
            CommonInterceptor(pref, ioDispatcher),
            buildHttpLog() // order is matter, if you want to see all http logs, leave it at last
        )
    }

    @Provides
    @Singleton
    fun provideNetworkHelper(application: Application): Network {
        return Network(application)
    }

    private fun buildHttpLog(): HttpLoggingInterceptor {
        val logLevel =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return HttpLoggingInterceptor().setLevel(logLevel)
    }
}