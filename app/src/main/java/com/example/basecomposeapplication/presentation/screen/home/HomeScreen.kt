package com.example.basecomposeapplication.presentation.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.basecomposeapplication.presentation.base.widget.BaseScreen

@Composable
fun HomeScreen(
    userInfo: String? = null,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userInfo) {
        viewModel.updateToken(userInfo ?: "")
    }
    BaseScreen(uiState = uiState, onDialogDismiss = { }, onConfirmDialogConfirm = { }) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.Cyan
        ) { paddingValues ->
            HomeText(homeUIState = uiState, modifier = Modifier.padding(paddingValues))
        }
    }

}

@Composable
private fun HomeText(
    homeUIState: HomeUIState,
    modifier: Modifier
) {
    Spacer(modifier = Modifier.height(36.dp))
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = "Info${homeUIState.token}",
            modifier = modifier.fillMaxWidth()
        )
    }
}