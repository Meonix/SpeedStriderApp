package com.example.basecomposeapplication.presentation.screen.map

import com.example.basecomposeapplication.presentation.base.stateview.BaseViewState

data class MapUIState(
    override val isLoading: Boolean = false,
    override val error: String? = null,
) : BaseViewState