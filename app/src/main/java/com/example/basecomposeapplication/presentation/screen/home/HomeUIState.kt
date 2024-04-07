package com.example.basecomposeapplication.presentation.screen.home

import com.example.basecomposeapplication.presentation.base.stateview.BaseViewState

data class HomeUIState(
    val token: String = "",
    override val isLoading: Boolean = false,
    override val error: String? = null,
) : BaseViewState