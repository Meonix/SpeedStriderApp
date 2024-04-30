package com.example.basecomposeapplication.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.InsertChartOutlined
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Scaffold
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
import com.example.basecomposeapplication.presentation.screen.login.LoginScreen
import com.example.basecomposeapplication.presentation.screen.login.MapScreen
import com.example.basecomposeapplication.presentation.screen.tab.component.TabView
import com.example.basecomposeapplication.presentation.screen.tab.model.TabBarItem
import com.example.basecomposeapplication.presentation.screen.tab.achievement.AchievementScreen
import com.example.basecomposeapplication.presentation.screen.tab.chart.AnalysisScreen
import com.example.basecomposeapplication.presentation.screen.tab.home.HomeScreen
import com.example.basecomposeapplication.presentation.screen.tab.setting.SettingScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = Destinations.HOME_ROUTE,
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    }
) {
    val homeTab = TabBarItem(title = "Home", route = Destinations.HOME_ROUTE, selectedIcon = Icons.Filled.Home, unselectedIcon = Icons.Outlined.Home)
    val analysisTab = TabBarItem(title = "Analysis", route = Destinations.ANALYSIS_ROUTE, selectedIcon = Icons.Filled.InsertChart, unselectedIcon = Icons.Outlined.InsertChartOutlined)
    val achievementTab = TabBarItem(title = "Achievement", route = Destinations.ACHIEVEMENT_ROUTE, selectedIcon = Icons.AutoMirrored.Filled.TrendingUp, unselectedIcon = Icons.AutoMirrored.Outlined.TrendingUp)
    val settingTab = TabBarItem(title = "Setting", route = Destinations.SETTING_ROUTE, selectedIcon = Icons.Filled.Settings, unselectedIcon = Icons.Outlined.Settings)

    val tabBarItems = listOf(homeTab, analysisTab, achievementTab, settingTab)
    val bottomBarRoutes = tabBarItems.map { it.route }

    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute: String = currentNavBackStackEntry?.destination?.route ?: startDestination

    val shouldShowBottomBar: Boolean = currentRoute in bottomBarRoutes

    Scaffold(bottomBar = {
        if (shouldShowBottomBar)
            TabView(tabBarItems, navController)
    }) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.padding(it),
        ) {
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

            //TAB
            customComposable(
                route = Destinations.HOME_ROUTE,
            ) {
                HomeScreen()
            }
            customComposable(
                route = Destinations.ANALYSIS_ROUTE,
            ) {
                AnalysisScreen()
            }
            customComposable(
                route = Destinations.ACHIEVEMENT_ROUTE,
            ) {
                AchievementScreen()
            }
            customComposable(
                route = Destinations.SETTING_ROUTE,
            ) {
                SettingScreen()
            }
        }
    }
}