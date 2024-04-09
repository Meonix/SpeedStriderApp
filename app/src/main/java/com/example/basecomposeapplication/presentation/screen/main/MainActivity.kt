package com.example.basecomposeapplication.presentation.screen.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.basecomposeapplication.presentation.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint

//TUFQQk9YX0RPV05MT0FEU19UT0tFTj1zay5leUoxSWpvaWEyRnBibTkyWVRFNU9Ua2lMQ0poSWpvaVkyeDFjamt4WTJkdU1EUjBhREpzY0dFMVlXb3dhemQ0YlNKOS5NRVd4LXpJaUhKdkl2emxGeEpIcUVR
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                NavGraph()
            }
        }
    }
}