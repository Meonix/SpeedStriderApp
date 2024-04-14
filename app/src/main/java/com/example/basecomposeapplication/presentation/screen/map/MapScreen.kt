package com.example.basecomposeapplication.presentation.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.basecomposeapplication.presentation.base.widget.BaseScreen
import com.example.basecomposeapplication.presentation.screen.map.MapViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.locationcomponent.location

@OptIn(MapboxExperimental::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
    clearError: () -> Unit = { viewModel.clearError() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val mapViewportState = rememberMapViewportState {
        // Set the initial camera position
        setCameraOptions {
            center(Point.fromLngLat(0.0, 0.0))
            zoom(0.0)
            pitch(0.0)
        }
    }

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
                            pulsingEnabled = true
                        }
                    })
                }
            }
        }
    }
}
