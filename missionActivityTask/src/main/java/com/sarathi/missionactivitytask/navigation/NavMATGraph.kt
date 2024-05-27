package com.sarathi.missionactivitytask.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.sarathi.contentmodule.media.MediaScreen
import com.sarathi.contentmodule.ui.content_detail_screen.screen.ContentDetailScreen
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_CONTENT_KEY
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_CONTENT_TYPE
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.BLANK_STRING
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

        composable(route = MATHomeScreens.MediaPlayerScreen.route, arguments = listOf(
            navArgument(
                name = ARG_CONTENT_KEY
            ) {
                type = NavType.StringType
            },
            navArgument(
                name = ARG_CONTENT_TYPE
            ) {
                type = NavType.StringType
            }
        )
        ) {
            MediaScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                fileType = it.arguments?.getString(
                    ARG_CONTENT_TYPE
                ) ?: BLANK_STRING,
                key = it.arguments?.getString(
                    ARG_CONTENT_KEY
                ) ?: BLANK_STRING
            )
        }


        composable(route = MATHomeScreens.ContentDetailScreen.route) {
            ContentDetailScreen(navController = navController, viewModel = hiltViewModel(),
                onNavigateToMediaScreen = { fileType, key ->
                    navigateToMediaPlayerScreen(
                        navController = navController,
                        contentKey = key,
                        contentType = fileType
                    )
                })
        }
    }

}


sealed class MATHomeScreens(val route: String) {
    object MissionScreen : MATHomeScreens(route = MISSION_SCREEN_ROUTE_NAME)
    object ActivityScreen : MATHomeScreens(route = ACTIVITY_SCREEN_ROUTE_NAME)
    object ContentDetailScreen : MATHomeScreens(route = CONTENT_DETAIL_SCREEN_ROUTE_NAME)
    object MediaPlayerScreen :
        MATHomeScreens(route = "$MEDIA_PLAYER_SCREEN_ROUTE_NAME/{$ARG_CONTENT_KEY}/{$ARG_CONTENT_TYPE}")

}

fun navigateToActivityScreen(navController: NavController) {
    navController.navigate(ACTIVITY_SCREEN_ROUTE_NAME)
}

fun navigateToContentDetailScreen(navController: NavController) {
    navController.navigate(CONTENT_DETAIL_SCREEN_ROUTE_NAME)
}

fun navigateToMediaPlayerScreen(
    navController: NavController,
    contentKey: String,
    contentType: String
) {
    navController.navigate("$MEDIA_PLAYER_SCREEN_ROUTE_NAME/${contentKey}/${contentType}")
}

const val MISSION_SCREEN_ROUTE_NAME = "mission_screen"
const val ACTIVITY_SCREEN_ROUTE_NAME = "activity_screen"
const val MEDIA_PLAYER_SCREEN_ROUTE_NAME = "media_player_screen"
const val CONTENT_DETAIL_SCREEN_ROUTE_NAME = "content_detail_screen"






