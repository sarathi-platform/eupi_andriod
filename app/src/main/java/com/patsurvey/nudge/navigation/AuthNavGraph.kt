package com.patsurvey.nudge.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.nrlm.baselinesurvey.ARG_MISSION_ID
import com.nrlm.baselinesurvey.ARG_MISSION_NAME
import com.nrlm.baselinesurvey.ARG_SUB_MISSION_NAME
import com.nrlm.baselinesurvey.ui.profile.presentation.ProfileBSScreen
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.DataLoadingScreenComponent
import com.nudge.core.SYNC_DATA
import com.nudge.navigationmanager.graphs.AuthScreen
import com.nudge.navigationmanager.graphs.LogoutScreens
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph
import com.nudge.navigationmanager.graphs.SettingScreens
import com.nudge.navigationmanager.utils.NavigationParams
import com.patsurvey.nudge.activities.SplashScreen
import com.patsurvey.nudge.activities.VillageScreen
import com.patsurvey.nudge.activities.backup.presentation.ActivityReopeningScreen
import com.patsurvey.nudge.activities.backup.presentation.ExportBackupScreen
import com.patsurvey.nudge.activities.backup.presentation.ExportImportScreen
import com.patsurvey.nudge.activities.settings.BugLogggingMechanismScreen
import com.patsurvey.nudge.activities.settings.presentation.SettingBSScreen
import com.patsurvey.nudge.activities.sync.history.presentation.SyncHistoryScreen
import com.patsurvey.nudge.activities.sync.home.presentation.SyncHomeScreen
import com.patsurvey.nudge.activities.ui.login.LoginScreen
import com.patsurvey.nudge.activities.ui.login.OtpVerificationScreen
import com.patsurvey.nudge.activities.ui.selectlanguage.LanguageScreen
import com.patsurvey.nudge.activities.video.FullscreenView
import com.patsurvey.nudge.activities.video.VideoListScreen
import com.patsurvey.nudge.utils.ARG_FROM_HOME
import com.patsurvey.nudge.utils.ARG_MOBILE_NUMBER
import com.patsurvey.nudge.utils.ARG_VIDEO_ID
import com.patsurvey.nudge.utils.BLANK_STRING

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        route = NudgeNavigationGraph.AUTHENTICATION,
        startDestination = AuthScreen.START_SCREEN.route
    ) {

        composable(route = AuthScreen.START_SCREEN.route) {
            SplashScreen(
                navController = navController, modifier = Modifier.fillMaxSize(),
                hiltViewModel()
            )
        }
        composable(
            route = AuthScreen.LANGUAGE_SCREEN.route
        ) {
            LanguageScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                pageFrom = ARG_FROM_HOME
            )
        }
        composable(
            route = AuthScreen.BUG_LOGGING_SCREEN.route
        ) {
            BugLogggingMechanismScreen(
                navController = navController
            )
        }

        composable(route = AuthScreen.LOGIN.route) {
            LoginScreen(
                navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = AuthScreen.OTP_VERIFICATION.route,
            arguments = listOf(navArgument(ARG_MOBILE_NUMBER) {
                type = NavType.StringType
            })
        ) {
            OtpVerificationScreen(
                navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                it.arguments?.getString(ARG_MOBILE_NUMBER).toString()
            )
        }

        composable(route = AuthScreen.VILLAGE_SELECTION_SCREEN.route) {
            VillageScreen(navController = navController) {
                navController.navigate(AuthScreen.AUTH_SETTING_SCREEN.route)
            }
        }

        composable(route = AuthScreen.AUTH_SETTING_SCREEN.route) {
            SettingBSScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }

        composable(route = AuthScreen.VIDEO_LIST_SCREEN.route) {
            VideoListScreen(navController = navController, modifier = Modifier, viewModel = hiltViewModel())
        }

        composable(
            route = AuthScreen.VIDEO_PLAYER_SCREEN.route,
            arguments = listOf(navArgument(ARG_VIDEO_ID){
                type = NavType.IntType
            })
        ) {
            FullscreenView(navController = navController, viewModel =  hiltViewModel(), videoId = it.arguments?.getInt(ARG_VIDEO_ID) ?: -1)
        }

        composable(route = AuthScreen.PROFILE_SCREEN.route) {
            ProfileBSScreen(navController = navController, viewModel = hiltViewModel())
        }

        composable(route = LogoutScreens.LOG_DATA_LOADING_SCREEN.route,
            arguments = listOf(
                navArgument(ARG_MISSION_ID) {
                    type = NavType.IntType
                },
                navArgument(ARG_MISSION_NAME) {
                    type = NavType.StringType
                },
                navArgument(ARG_SUB_MISSION_NAME) {
                    type = NavType.StringType
                    nullable = true
                }
                )
        ) {
            DataLoadingScreenComponent(
                viewModel = hiltViewModel(),
                navController = navController,
                missionId = it.arguments?.getInt(
                    ARG_MISSION_ID
                ) ?: -1,
                missionDescription = it.arguments?.getString(ARG_MISSION_NAME) ?: BLANK_STRING,
                missionSubDescription = it.arguments?.getString(ARG_SUB_MISSION_NAME)
                    ?: BLANK_STRING
            )
        }

        composable(route = SettingScreens.BACKUP_RECOVERY_SCREEN.route) {
            ExportImportScreen(navController = navController, viewModel = hiltViewModel())
        }


        composable(route = SettingScreens.EXPORT_BACKUP_FILE_SCREEN.route) {
            ExportBackupScreen(navController = navController, viewModel = hiltViewModel())
        }


        composable(route = SettingScreens.SYNC_DATA_NOW_SCREEN.route){
            SyncHomeScreen(navController = navController, viewModel = hiltViewModel())
        }
        composable(SettingScreens.ACTIVITY_REOPENING_SCREEN.route) {
            ActivityReopeningScreen(navController = navController)
        }

        composable(route = SettingScreens.SYNC_HISTORY_SCREEN.route,
            arguments = listOf(
                navArgument(NavigationParams.ARG_SYNC_TYPE.value){
                    type=NavType.StringType
                }
            )
        ){
            SyncHistoryScreen(
                navController = navController,
                syncType = it.arguments?.getString(NavigationParams.ARG_SYNC_TYPE.value)
                    ?: SYNC_DATA,
                viewModel = hiltViewModel()
            )
        }
    }

//    settingNavGraph(navController)
//   logoutGraph(navController =navController)
}

