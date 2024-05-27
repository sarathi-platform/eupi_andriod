package com.sarathi.missionactivitytask.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MAT_GRAPH
import com.sarathi.missionactivitytask.ui.grant_activity_screen.screen.ActivityScreen
import com.sarathi.missionactivitytask.ui.mission_screen.screen.GrantMissionScreen


fun NavGraphBuilder.MatNavigation(navController: NavHostController) {
    navigation(
        route = MAT_GRAPH,
        startDestination = MATHomeScreens.MissionScreen.route
    ) {

        composable(route = MATHomeScreens.MissionScreen.route) {
            GrantMissionScreen(navController = navController, viewModel = hiltViewModel())
        }
        composable(route = MATHomeScreens.ActivityScreen.route) {
            ActivityScreen(navController = navController, viewModel = hiltViewModel())
        }
    }

}


sealed class MATHomeScreens(val route: String) {
    object MissionScreen : MATHomeScreens(route = MISSION_SCREEN_ROUTE_NAME)
    object ActivityScreen : MATHomeScreens(route = ACTIVITY_SCREEN_SCREEN_ROUTE_NAME)

}

fun navigateToActivityScreen(navController: NavController) {
    navController.navigate(ACTIVITY_SCREEN_SCREEN_ROUTE_NAME)
}

const val MISSION_SCREEN_ROUTE_NAME = "mission_screen"
const val ACTIVITY_SCREEN_SCREEN_ROUTE_NAME = "activity_screen"






