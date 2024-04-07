package com.example.basecomposeapplication.presentation.screen.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.basecomposeapplication.presentation.base.widget.BaseScreen

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onEmailChange: (String) -> Unit = { viewModel.updateEmail(it) },
    onPasswordChange: (String) -> Unit = { viewModel.updatePassword(it) },
    onLoginResult: (String) -> Unit,
    clearError: () -> Unit = { viewModel.clearError() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager: FocusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    BaseScreen(
        uiState = uiState,
        onDialogDismiss = clearError,
        onConfirmDialogConfirm = clearError
    ) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .clickable(
                    interactionSource,
                    indication = null,
                    onClick = { focusManager.clearFocus() }),
            snackbarHost = { SnackbarHost(scaffoldState.snackbarHostState) }
        ) { paddingValues ->
            if (uiState.token.isNotBlank()) {
                LaunchedEffect(uiState.token) {
                    onLoginResult(uiState.token)
                    viewModel.clearToken()
                }
            }
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                LoginTextFields(
                    loginUIState = uiState,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.login()
                        focusManager.clearFocus()
                    }
                ) {
                    Text("Login")
                }
            }
        }
    }
}

@Composable
private fun LoginTextFields(
    loginUIState: LoginUIState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = loginUIState.email,
        onValueChange = onEmailChange,
        label = { Text("Email") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = loginUIState.password,
        onValueChange = onPasswordChange,
        label = { Text("Password") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    )
}