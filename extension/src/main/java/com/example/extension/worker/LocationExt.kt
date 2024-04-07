package com.example.extension.worker

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

fun Activity.initFusedLocationProviderClient(): FusedLocationProviderClient =
    LocationServices.getFusedLocationProviderClient(
        this
    )

@SuppressLint("MissingPermission")
fun getLastLocation(
    fusedLocationProviderClient: FusedLocationProviderClient,
    onGetLocationSuccess: ((Location?) -> Unit)? = null,
    onGetLocationFailed: ((Exception?) -> Unit)? = null
) {
    fusedLocationProviderClient.lastLocation.addOnCompleteListener {
        if (it.isSuccessful) {
            //if it. result is null, the location feature on device was turned off
            onGetLocationSuccess?.invoke(it.result)
        } else {
            onGetLocationFailed?.invoke(it.exception)
        }
    }
}