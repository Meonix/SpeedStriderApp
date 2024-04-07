package com.example.basecomposeapplication.presentation.base.widget

import androidx.compose.runtime.Composable
import com.example.basecomposeapplication.presentation.base.stateview.BaseViewState
import com.example.basecomposeapplication.presentation.widgets.ConfirmDialog
import com.example.basecomposeapplication.presentation.widgets.FullScreenLoadingDialog

@Composable
fun BaseScreen(
    uiState: BaseViewState,
    onDialogDismiss: () -> Unit,
    onConfirmDialogConfirm: () -> Unit,
    content: @Composable () -> Unit
) {

    content()
    ConfirmDialog(
        showDialog = uiState.error != null,
        onDialogDismissRequest = onDialogDismiss,
        title = "Error alert",
        content = uiState.error.toString(),
        confirmButtonText = "Yes",
        onConfirmButtonClicked = onConfirmDialogConfirm
    )
    FullScreenLoadingDialog(isLoading = uiState.isLoading)
}