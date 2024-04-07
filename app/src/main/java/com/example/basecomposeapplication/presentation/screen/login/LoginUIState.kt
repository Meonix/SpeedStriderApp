package com.example.basecomposeapplication.presentation.screen.login

import com.example.basecomposeapplication.presentation.base.stateview.BaseViewState

data class LoginUIState(
    val email: String = "",
    val password: String = "",
    val token: String = "",
    override val isLoading: Boolean = false,
    override val error: String? = null,
) : BaseViewState