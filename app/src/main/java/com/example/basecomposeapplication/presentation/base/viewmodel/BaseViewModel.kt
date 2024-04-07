package com.example.basecomposeapplication.presentation.base.viewmodel

import com.example.basecomposeapplication.presentation.base.stateview.BaseViewState
import kotlinx.coroutines.flow.StateFlow

interface BaseViewModel<VS : BaseViewState> {
    val uiState: StateFlow<VS>
}
