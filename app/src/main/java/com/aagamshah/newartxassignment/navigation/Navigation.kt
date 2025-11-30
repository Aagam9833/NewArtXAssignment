package com.aagamshah.newartxassignment.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.aagamshah.newartxassignment.presentation.homescreen.HomeScreen
import com.aagamshah.newartxassignment.presentation.postscreen.UserProfileScreen
import com.aagamshah.newartxassignment.presentation.settingsscreen.SettingsScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainRoute
    ) {
        composable<MainRoute> {
            HomeScreen(navController = navController)
        }

        composable<Routes.ProfileRoute> { backStackEntry ->
            val route: Routes.ProfileRoute = backStackEntry.toRoute()
            UserProfileScreen(
                userId = route.id,
                navController = navController
            )
        }

        composable<Routes.SettingsRoute> {
            SettingsScreen(navController = navController)
        }
    }
}