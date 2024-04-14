package com.example.basecomposeapplication.presentation.screen.map

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

object LocationManager {
    private var fusedLocationClient: FusedLocationProviderClient? = null

    fun start(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }
}