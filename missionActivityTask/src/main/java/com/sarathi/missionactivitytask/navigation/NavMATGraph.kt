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
import com.sarathi.contentmodule.media.MediaScreen
import com.sarathi.contentmodule.ui.content_detail_screen.screen.ContentDetailScreen
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ACTIVITY_COMPLETION_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ACTIVITY_SCREEN_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_ACTIVITY_ID
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_ACTIVITY_MASSAGE
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_ACTIVITY_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_CONTENT_KEY
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_CONTENT_TYPE
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_MISSION_ID
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.ARG_MISSION_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.CONTENT_DETAIL_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.GRANT_TASK_SCREEN_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MAT_GRAPH
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MEDIA_PLAYER_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MISSION_FINAL_STEP_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.MISSION_SCREEN_ROUTE_NAME
import com.sarathi.missionactivitytask.ui.grantTask.screen.GrantTaskScreen
import com.sarathi.missionactivitytask.ui.grant_activity_screen.screen.ActivityScreen
import com.sarathi.missionactivitytask.ui.mission_screen.screen.GrantMissionScreen
import com.sarathi.missionactivitytask.ui.step_completion_screen.ActivitySuccessScreen
import com.sarathi.missionactivitytask.ui.step_completion_screen.FinalStepCompletionScreen


fun NavGraphBuilder.MatNavigation(
    navController: NavHostController,
    onSettingIconClick: () -> Unit
) {
    navigation(
        route = MAT_GRAPH,
        startDestination = MATHomeScreens.MissionScreen.route
    ) {

        composable(route = MATHomeScreens.MissionScreen.route) {
            GrantMissionScreen(
                navController = navController, viewModel = hiltViewModel(),
                onSettingClick = onSettingIconClick
            )
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
                ) ?: BLANK_STRING,
                onSettingClick = onSettingIconClick
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
                ) ?: BLANK_STRING,
                onSettingClick = onSettingIconClick
            )
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

        composable(route = MATHomeScreens.ActivityCompletionScreen.route, arguments = listOf(
            navArgument(
                name = ARG_ACTIVITY_MASSAGE
            ) {
                type = NavType.StringType
            }
        )) {
            ActivitySuccessScreen(
                navController = navController, messages = it.arguments?.getString(
                    ARG_ACTIVITY_MASSAGE
                ) ?: BLANK_STRING
            )
        }

        composable(route = MATHomeScreens.FinalStepCompletionScreen.route) {
            FinalStepCompletionScreen(navController = navController) {

            }
        }
    }

}


sealed class MATHomeScreens(val route: String) {
    object MissionScreen : MATHomeScreens(route = MISSION_SCREEN_ROUTE_NAME)
    object ActivityScreen :
        MATHomeScreens(route = "$ACTIVITY_SCREEN_SCREEN_ROUTE_NAME/{$ARG_MISSION_ID}/{$ARG_MISSION_NAME}")

    object GrantTaskScreen :
        MATHomeScreens(route = "$GRANT_TASK_SCREEN_SCREEN_ROUTE_NAME/{$ARG_MISSION_ID}/{$ARG_ACTIVITY_ID}/{$ARG_ACTIVITY_NAME}")

    object ContentDetailScreen : MATHomeScreens(route = CONTENT_DETAIL_SCREEN_ROUTE_NAME)
    object MediaPlayerScreen :
        MATHomeScreens(route = "$MEDIA_PLAYER_SCREEN_ROUTE_NAME/{$ARG_CONTENT_KEY}/{$ARG_CONTENT_TYPE}")

    object ActivityCompletionScreen :
        MATHomeScreens(route = "$ACTIVITY_COMPLETION_SCREEN_ROUTE_NAME/{$ARG_ACTIVITY_MASSAGE}")

    object FinalStepCompletionScreen : MATHomeScreens(route = MISSION_FINAL_STEP_SCREEN_ROUTE_NAME)

}

fun navigateToContentDetailScreen(navController: NavController) {
    navController.navigate(CONTENT_DETAIL_SCREEN_ROUTE_NAME)
}

fun navigateToActivityCompletionScreen(navController: NavController, activityMsg: String) {
    navController.navigate("$ACTIVITY_COMPLETION_SCREEN_ROUTE_NAME/$activityMsg")
}

fun navigateToFinalStepCompletionScreen(navController: NavController) {
    navController.navigate(MISSION_FINAL_STEP_SCREEN_ROUTE_NAME)
}

fun navigateToMediaPlayerScreen(
    navController: NavController,
    contentKey: String,
    contentType: String
) {
    navController.navigate("$MEDIA_PLAYER_SCREEN_ROUTE_NAME/${contentKey}/${contentType}")
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







