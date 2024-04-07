package com.example.basecomposeapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.customComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = {
            this.enterComplexTransition()
        },
        exitTransition = {
            this.exitComplexTransition()
        },
        popEnterTransition = {
            this.popEnterComplexTransition()
        },
        popExitTransition = {
            this.popExitComplexTransition()
        },
    ) { navBackStackEntry ->
        content(navBackStackEntry)
    }
}
