package com.example.basecomposeapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.basecomposeapplication.presentation.navigation.DestinationsArgs.USER_INFO_ARG
import com.example.basecomposeapplication.presentation.screen.home.HomeScreen
import com.example.basecomposeapplication.presentation.screen.login.LoginScreen
import com.example.basecomposeapplication.presentation.screen.login.MapScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = Destinations.MAP_ROUTE,
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute: String = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        customComposable(
            route = Destinations.HOME_ROUTE,
            arguments = listOf(
                navArgument(USER_INFO_ARG) { type = NavType.StringType; defaultValue = "" }
            )
        ) { entry ->
            HomeScreen(
                userInfo = entry.arguments?.getString(USER_INFO_ARG)
            )
        }
        customComposable(
            route = Destinations.LOGIN_ROUTE,
        ) {
            LoginScreen(
                onLoginResult = { result ->
                    navActions.navigateToHome(userInfo = result)
                }
            )
        }
        customComposable(
            route = Destinations.MAP_ROUTE,
        ) {
            MapScreen()
        }
    }
}