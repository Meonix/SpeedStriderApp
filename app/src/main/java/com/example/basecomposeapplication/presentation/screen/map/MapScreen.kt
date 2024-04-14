package com.example.basecomposeapplication.presentation.screen.login

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.basecomposeapplication.presentation.base.widget.BaseScreen
import com.example.basecomposeapplication.presentation.screen.map.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.locationcomponent.location
import timber.log.Timber

@OptIn(MapboxExperimental::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
    clearError: () -> Unit = { viewModel.clearError() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(0.0, 0.0))
            zoom(14.0)
            pitch(0.0)
        }
    }

    LaunchedEffect(key1 = true, block = {
        Timber.d("launchMultiplePermissionRequest")
        locationPermissionState.launchMultiplePermissionRequest()
    })

    BaseScreen(
        uiState = uiState,
        onDialogDismiss = clearError,
        onConfirmDialogConfirm = clearError
    ) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
        ) { paddingValues ->

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (locationPermissionState.allPermissionsGranted) {
                    MapboxMap(
                        modifier = Modifier.fillMaxSize(),
                        logo = {},
                        compass = { },
                        scaleBar = { },
                        mapViewportState = mapViewportState
                    ) {
                        MapEffect(key1 = Unit, block = { mapView ->
                            mapView.location.updateSettings {
                                enabled = true
                                pulsingEnabled = false
                            }
                        })
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Screen need have permission location for tracking location",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", context.packageName, null)
                            i.data = uri
                            context.startActivity(i)
                        }) {
                            Text(text = "Setting")
                        }
                    }
                }
            }
        }
    }
}
