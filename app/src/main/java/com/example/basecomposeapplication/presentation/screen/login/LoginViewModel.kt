package com.example.basecomposeapplication.presentation.screen.login

import androidx.lifecycle.viewModelScope
import com.example.basecomposeapplication.data.local.datastore.Pref
import com.example.basecomposeapplication.domain.usecase.test.TestUseCase
import com.example.basecomposeapplication.presentation.base.viewmodel.BaseViewModelImpl
import com.example.basecomposeapplication.presentation.error.AppErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val testUseCase: TestUseCase,
    private val pref: Pref,
    private val appErrorHandler: AppErrorHandler
) : BaseViewModelImpl<LoginUIState>(LoginUIState()) {

    override val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()

    fun clearError() {
        _uiState.update {
            it.copy(
                error = null
            )
        }
    }

    fun clearToken() {
        _uiState.update {
            it.copy(
                token = ""
            )
        }
    }

    fun login() {
        viewModelScope.launch {
            testUseCase(
                ioDispatcherProvider = ioDispatcherProvider,
                TestUseCase.Input(uiState.value.email)
            ) {
                onSubscribe {
                    _uiState.update {
                        it.copy(
                            isLoading = true,
                            error = null,
                            token = ""
                        )
                    }
                }
                onError { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = appErrorHandler.proceed(throwable)
                        )
                    }
                }
                onSuccess { result ->
                    viewModelScope.launch(ioDispatcherProvider.dispatcher()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                token = pref.getAccessToken(),
                                error = null,
                            )
                        }
                    }
                }
            }
        }
    }


    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(email = email)
        }
    }

    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(password = password)
        }
    }


}