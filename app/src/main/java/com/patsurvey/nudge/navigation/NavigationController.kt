package com.patsurvey.nudge.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
fun HomeScreenFlowNavigation(homeScreenNavController: NavHostController, stepsNavHostController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController =homeScreenNavController, startDestination = ScreenRoutes.PROGRESS_SCREEN.route) {
        composable(route = ScreenRoutes.PROGRESS_SCREEN.route) {
            ProgressScreen(modifier = Modifier
                .fillMaxSize()
                .then(modifier), stepsNavHostController)
        }
//        composable(route = ScreenRoutes.DIDI_SCREEN.route) {
//            W
//        }
    }
    NavHost(navController = stepsNavHostController, startDestination = ScreenRoutes.PROGRESS_SCREEN.route) {
        composable(route = ScreenRoutes.PROGRESS_SCREEN.route) {
            ProgressScreen(modifier = Modifier
                .fillMaxSize()
                .then(modifier), stepsNavHostController)
        }
        composable(route = ScreenRoutes.TRANSECT_WALK_SCREEN.route) {
            TransectWalkScreen(navController = stepsNavHostController, modifier = Modifier.fillMaxSize().then(modifier))
        }
    }
}