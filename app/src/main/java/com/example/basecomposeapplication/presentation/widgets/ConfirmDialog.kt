package com.example.basecomposeapplication.presentation.widgets

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDialog(
    showDialog: Boolean,
    onDialogDismissRequest: () -> Unit,
    title: String,
    content: String,
    confirmButtonText: String,
    onConfirmButtonClicked: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDialogDismissRequest,
            title = { Text(text = title) },
            text = { Text(text = content) },
            confirmButton = {
                Button(onClick = onConfirmButtonClicked) {
                    Text(text = confirmButtonText)
                }
            }
        )
    }
}