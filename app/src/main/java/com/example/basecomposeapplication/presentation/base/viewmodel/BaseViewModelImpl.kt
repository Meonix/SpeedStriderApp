package com.example.basecomposeapplication.presentation.base.viewmodel

import androidx.lifecycle.ViewModel
import com.example.basecomposeapplication.presentation.base.stateview.BaseViewState
import com.example.basecomposeapplication.shared.scheduler.IODispatcher
import com.example.basecomposeapplication.shared.scheduler.MainDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

interface MVIViewModelContract {

    /**
     * Pass ui intent from View to [ViewModel]
     * */
    fun processEvent(intent: BaseIntent)
}

interface BaseIntent


abstract class BaseViewModelImpl<VS : BaseViewState>(uiStateInitializer: VS) :
    ViewModel(),
    BaseViewModel<VS>, MVIViewModelContract {
    val _uiState: MutableStateFlow<VS> = MutableStateFlow(uiStateInitializer)

    protected fun updateUiState(reduce: VS.() -> VS) {
        val newState = uiState.value.reduce()
        _uiState.value = newState
    }

    @Inject
    open lateinit var ioDispatcherProvider: IODispatcher

    @Inject
    open lateinit var mainDispatcherProvider: MainDispatcher
}
