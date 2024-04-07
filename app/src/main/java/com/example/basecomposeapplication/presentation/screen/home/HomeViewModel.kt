package com.example.basecomposeapplication.presentation.screen.home

import com.example.basecomposeapplication.presentation.base.viewmodel.BaseViewModelImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

) : BaseViewModelImpl<HomeUIState>(HomeUIState()) {
    override val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    fun updateToken(token: String) {
        _uiState.value = _uiState.value.copy(
            token = token
        )
    }
}