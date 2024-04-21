package com.example.basecomposeapplication.presentation.screen.map.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonAction(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Pause,
    shape: Shape = CircleShape,
    paddingItem: Dp = 8.dp,
    contentDescription: String = "",
    onClick: () -> Unit = {}
) {
    Card(modifier = modifier, shape = shape, onClick = onClick) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(modifier = Modifier.padding(paddingItem), imageVector = icon, contentDescription = contentDescription)
        }
    }
}

@Preview
@Composable
private fun ButtonActionPreview() {
    MaterialTheme {
        ButtonAction(modifier = Modifier.size(48.dp))
    }
}