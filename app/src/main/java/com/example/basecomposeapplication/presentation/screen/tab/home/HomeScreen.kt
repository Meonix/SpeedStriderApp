package com.example.basecomposeapplication.presentation.screen.tab.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.example.basecomposeapplication.presentation.screen.tab.home.component.CardTarget

@Composable
fun HomeScreen() {
    Scaffold {
        Column(
            modifier = Modifier
                .background(Color("#F6F6F6".toColorInt()))
                .padding(it)
                .fillMaxSize()
        ) {
            CardTarget()
        }
    }
}