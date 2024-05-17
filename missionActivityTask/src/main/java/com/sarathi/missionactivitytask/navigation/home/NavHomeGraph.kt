package com.sarathi.missionactivitytask.navigation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sarathi.missionactivitytask.navigation.Graph
import com.sarathi.missionactivitytask.ui.grant_activity_screen.screen.ActivityScreen
import com.sarathi.missionactivitytask.ui.mission_screen.screen.GrantMissionScreen
import com.sarathi.missionactivitytask.ui.theme.white

@Composable
fun NavHomeGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .background(color = white)
            .then(modifier),
        navController = navController,
        route = Graph.HOME,
        startDestination = HomeScreens.MissionScreen.route
    ) {

        composable(route = HomeScreens.MissionScreen.route) {
            GrantMissionScreen(navController = navController, viewModel = hiltViewModel())
        }
        composable(route = HomeScreens.ActivityScreen.route) {
            ActivityScreen(navController = navController, viewModel = hiltViewModel())
        }
    }

}


sealed class HomeScreens(val route: String) {
    object DATA_LOADING_SCREEN : HomeScreens(route = DATA_LOADING_SCREEN_ROUTE_NAME)
    object MissionScreen : HomeScreens(route = MISSION_SCREEN_ROUTE_NAME)
    object ActivityScreen : HomeScreens(route = ACTIVITY_SCREEN_SCREEN_ROUTE_NAME)

}

fun navigateToActivityScreen(navController: NavController) {
    navController.navigate(ACTIVITY_SCREEN_SCREEN_ROUTE_NAME)
}


const val DATA_LOADING_SCREEN_ROUTE_NAME = "data_loading_screen"
const val MISSION_SCREEN_ROUTE_NAME = "mission_screen"
const val ACTIVITY_SCREEN_SCREEN_ROUTE_NAME = "activity_screen"






