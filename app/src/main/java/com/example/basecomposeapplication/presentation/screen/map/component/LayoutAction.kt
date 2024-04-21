package com.example.basecomposeapplication.presentation.screen.map.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LayoutAction(modifier: Modifier = Modifier, isRecording: Boolean = false) {
    Column(modifier = modifier.background(Color.White)) {
        Row {
            Text(text = if (isRecording) "Đang ghi" else "")
            ButtonAction(modifier = Modifier.size(48.dp))
        }
    }
}