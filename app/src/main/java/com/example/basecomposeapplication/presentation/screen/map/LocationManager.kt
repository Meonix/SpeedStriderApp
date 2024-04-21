package com.example.basecomposeapplication.presentation.screen.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.mapbox.maps.logE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resumeWithException

object LocationManager {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun startTrackingLocation(context: Context, locationInterval: Long, onLocationUpdateCallback: (LocationResult) -> Unit) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                onLocationUpdateCallback(p0)
            }
        }

        locationCallback?.let {
            val locationRequest: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, locationInterval)
                .setWaitForAccurateLocation(false)
                .build()


            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location = suspendCancellableCoroutine { continuation ->
        if (fusedLocationClient == null)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient?.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
            override fun isCancellationRequested() = false
        })
            ?.addOnSuccessListener { location: Location? ->
                if (location == null) {
                    Timber.e("Cannot get location.")
                    continuation.resumeWithException(Throwable("Cannot get location."))
                } else {
                    continuation.resume(location) {}
                }
            }?.addOnFailureListener {
                Timber.e("Cannot get location.")
                continuation.resumeWithException(Throwable("Cannot get location."))
            }
    }

    fun stopTrackingLocation() {
        try {
            //Removes all location updates for the given callback.
            val removeTask = locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
            removeTask?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("Location Callback removed.")
                } else {
                    Timber.d("Failed to remove Location Callback.")
                }
            }
        } catch (se: SecurityException) {
            Timber.e("Failed to remove Location Callback.. $se")
        }
        fusedLocationClient = null
    }
}