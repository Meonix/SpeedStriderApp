package com.example.basecomposeapplication.di

import android.app.Application
import com.example.basecomposeapplication.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application that sets up Timber in the DEBUG BuildConfig.
 * Read Timber's documentation for production setups.
 */
@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}