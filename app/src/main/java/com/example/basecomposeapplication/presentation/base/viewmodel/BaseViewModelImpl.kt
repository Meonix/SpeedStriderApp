package com.example.basecomposeapplication.presentation.base.viewmodel

import androidx.lifecycle.ViewModel
import com.example.basecomposeapplication.presentation.base.stateview.BaseViewState
import com.example.basecomposeapplication.shared.scheduler.IODispatcher
import com.example.basecomposeapplication.shared.scheduler.MainDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


abstract class BaseViewModelImpl<VS : BaseViewState>(uiStateInitializer: VS) :
    ViewModel(),
    BaseViewModel<VS> {
    val _uiState: MutableStateFlow<VS> = MutableStateFlow(uiStateInitializer)

    @Inject
    open lateinit var ioDispatcherProvider: IODispatcher

    @Inject
    open lateinit var mainDispatcherProvider: MainDispatcher
}
