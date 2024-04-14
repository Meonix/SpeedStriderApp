package com.example.basecomposeapplication.presentation.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.basecomposeapplication.presentation.navigation.DestinationsArgs.USER_INFO_ARG
import com.example.basecomposeapplication.presentation.navigation.Screens.HOME_SCREEN
import com.example.basecomposeapplication.presentation.navigation.Screens.LOGIN_SCREEN
import com.example.basecomposeapplication.presentation.navigation.Screens.MAP_SCREEN

/**
 * Screens used in [Destinations]
 */
private object Screens {
    const val LOGIN_SCREEN = "login"
    const val HOME_SCREEN = "main"
    const val REGISTER_SCREEN = "register"
    const val MAP_SCREEN = "map"
}

/**
 * Arguments used in [Destinations] routes
 */
object DestinationsArgs {
    const val USER_INFO_ARG = "userLoginInfo"
}

/**
 * Destinations used in the [Activity]
 */
object Destinations {
    const val LOGIN_ROUTE = LOGIN_SCREEN

    //    const val HOME_ROUTE = "$HOME_SCREEN/{$USER_INFO_ARG}"
    const val HOME_ROUTE = "$HOME_SCREEN?$USER_INFO_ARG={$USER_INFO_ARG}"
    const val MAP_ROUTE = MAP_SCREEN
}


/**
 * Models the navigation actions in the app.
 */

class NavigationActions(private val navController: NavHostController) {

    fun navigateToHome(userInfo: String = "") {
        navController.navigate(
//            "$HOME_SCREEN/$userInfo"
            HOME_SCREEN.let {
                if (userInfo != "") "$it?$USER_INFO_ARG=$userInfo" else it
            }
        ) {
            launchSingleTop = true
        }
    }

    fun navigateToLogin() {
        navController.navigate(Destinations.LOGIN_ROUTE) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

//    fun navigateToTaskDetail(taskId: String) {
//        navController.navigate("$TASK_DETAIL_SCREEN/$taskId")
//    }
//
//    fun navigateToAddEditTask(title: Int, taskId: String?) {
//        navController.navigate(
//            "$ADD_EDIT_TASK_SCREEN/$title".let {
//                if (taskId != null) "$it?$TASK_ID_ARG=$taskId" else it
//            }
//        )
//    }
}


