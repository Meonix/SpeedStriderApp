package com.example.basecomposeapplication.presentation.screen.tab.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.basecomposeapplication.presentation.navigation.Destinations

data class TabBarItem(
    val title: String,
    val route: String = Destinations.HOME_ROUTE,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)