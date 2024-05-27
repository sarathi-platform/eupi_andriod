package com.sarathi.missionactivitytask.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.nudge.core.BLANK_STRING
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MAT_GRAPH
import com.sarathi.missionactivitytask.ui.grantTask.screen.GrantTaskScreen
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
        composable(
            route = MATHomeScreens.ActivityScreen.route, arguments = listOf(
                navArgument(name = ARG_MISSION_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_MISSION_NAME) {
                    type = NavType.StringType
                })
        ) {
            ActivityScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                missionId = it.arguments?.getInt(
                    ARG_MISSION_ID
                ) ?: 0,
                missionName = it.arguments?.getString(
                    ARG_MISSION_NAME
                ) ?: BLANK_STRING
            )
        }
        composable(
            route = MATHomeScreens.GrantTaskScreen.route, arguments = listOf(
                navArgument(name = ARG_MISSION_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_ACTIVITY_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_ACTIVITY_NAME) {
                    type = NavType.StringType
                })
        ) {
            GrantTaskScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                missionId = it.arguments?.getInt(
                    ARG_MISSION_ID
                ) ?: 0,
                activityId = it.arguments?.getInt(
                    ARG_ACTIVITY_ID
                ) ?: 0,
                activityName = it.arguments?.getString(
                    ARG_ACTIVITY_NAME
                ) ?: BLANK_STRING
            )
        }
    }

}


sealed class MATHomeScreens(val route: String) {
    object MissionScreen : MATHomeScreens(route = MISSION_SCREEN_ROUTE_NAME)
    object ActivityScreen :
        MATHomeScreens(route = "$ACTIVITY_SCREEN_SCREEN_ROUTE_NAME/{$ARG_MISSION_ID}/{$ARG_MISSION_NAME}")

    object GrantTaskScreen :
        MATHomeScreens(route = "$GRANT_TASK_SCREEN_SCREEN_ROUTE_NAME/{$ARG_MISSION_ID}/{$ARG_ACTIVITY_ID}/{$ARG_ACTIVITY_NAME}")

}

fun navigateToActivityScreen(navController: NavController, missionId: Int, missionName: String) {
    navController.navigate("$ACTIVITY_SCREEN_SCREEN_ROUTE_NAME/$missionId/$missionName")
}

fun navigateToTaskScreen(
    navController: NavController,
    missionId: Int,
    activityId: Int,
    activityName: String
) {
    navController.navigate("$GRANT_TASK_SCREEN_SCREEN_ROUTE_NAME/$missionId/$activityId/$activityName")
}

const val MISSION_SCREEN_ROUTE_NAME = "mission_screen"
const val ACTIVITY_SCREEN_SCREEN_ROUTE_NAME = "activity_screen"
const val GRANT_TASK_SCREEN_SCREEN_ROUTE_NAME = "grant_task_screen"

const val ARG_ACTIVITY_ID = "activity_id"
const val ARG_MISSION_ID = "mission_id"
const val ARG_MISSION_NAME = "mission_name"
const val ARG_ACTIVITY_NAME = "activity_name"




