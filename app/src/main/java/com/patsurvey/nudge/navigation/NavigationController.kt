package com.patsurvey.nudge.navigation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.patsurvey.nudge.activities.*

@Composable
fun StartFlowNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = ScreenRoutes.START_SCREEN.route) {
        composable(route = ScreenRoutes.START_SCREEN.route) {
            SplashScreen(navController = navController, modifier = Modifier.fillMaxSize())
        }
        composable(route = ScreenRoutes.LANGUAGE_SCREEN.route) {
            LanguageScreen(navController = navController, modifier = Modifier.fillMaxSize())
        }
        composable(route = ScreenRoutes.HOME_SCREEN.route) {
            HomeScreen(navController = navController, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun HomeScreenFlowNavigation(homeScreenNavController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController =homeScreenNavController, startDestination = ScreenRoutes.PROFILE_SCREEN.route) {
        composable(route = ScreenRoutes.PROFILE_SCREEN.route) {
            ProgressScreen(modifier = Modifier.fillMaxSize().then(modifier))
        }
        composable(route = ScreenRoutes.DIDI_SCREEN.route) {
            TolaScreen(navController = homeScreenNavController, modifier = Modifier.fillMaxSize().then(modifier))
        }
    }
}