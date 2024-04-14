package com.example.basecomposeapplication.presentation.screen.map

import androidx.lifecycle.viewModelScope
import com.example.basecomposeapplication.data.local.datastore.Pref
import com.example.basecomposeapplication.domain.usecase.test.TestUseCase
import com.example.basecomposeapplication.presentation.base.viewmodel.BaseIntent
import com.example.basecomposeapplication.presentation.base.viewmodel.BaseViewModelImpl
import com.example.basecomposeapplication.presentation.error.AppErrorHandler
import com.example.basecomposeapplication.presentation.screen.map.MapUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
) : BaseViewModelImpl<MapUIState>(MapUIState()) {

    override val uiState: StateFlow<MapUIState> = _uiState.asStateFlow()
    override fun processEvent(intent: BaseIntent) {
        TODO("Not yet implemented")
    }

    fun clearError() {
        _uiState.update {
            it.copy(
                error = null
            )
        }
    }

}