package com.example.basecomposeapplication.di

import com.example.basecomposeapplication.presentation.error.AppErrorHandler
import com.example.basecomposeapplication.shared.scheduler.DefaultDispatcher
import com.example.basecomposeapplication.shared.scheduler.IODispatcher
import com.example.basecomposeapplication.shared.scheduler.MainDispatcher
import com.example.basecomposeapplication.shared.scheduler.UnconfinedDispatcher
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideIoDispatcher() = IODispatcher()

    @Singleton
    @Provides
    fun provideMainDispatcher() = MainDispatcher()

    @Singleton
    @Provides
    fun provideDefaultDispatcher() = DefaultDispatcher()

    @Singleton
    @Provides
    fun provideUnconfinedDispatcher() = UnconfinedDispatcher()

    @Singleton
    @Provides
    fun provideAppErrorHandler() = AppErrorHandler()

    @Singleton
    @Provides
    fun provideGson() = Gson()
}