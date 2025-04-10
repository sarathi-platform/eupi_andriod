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
import com.patsurvey.nudge.ProfileScreen
import com.patsurvey.nudge.activities.SplashScreen
import com.patsurvey.nudge.activities.VillageScreen
import com.patsurvey.nudge.activities.settings.BugLogggingMechanismScreen
import com.patsurvey.nudge.activities.settings.SettingScreen
import com.patsurvey.nudge.activities.ui.login.LoginScreen
import com.patsurvey.nudge.activities.ui.login.OtpVerificationScreen
import com.patsurvey.nudge.activities.ui.selectlanguage.LanguageScreen
import com.patsurvey.nudge.activities.video.FullscreenView
import com.patsurvey.nudge.activities.video.VideoListScreen
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.ARG_FROM_HOME
import com.patsurvey.nudge.utils.ARG_MOBILE_NUMBER
import com.patsurvey.nudge.utils.ARG_VIDEO_ID

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.AUTHENTICATION,
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
            /*VillageSelectionScreen(navController = navController, viewModel = hiltViewModel()){
                navController.navigate(AuthScreen.AUTH_SETTING_SCREEN.route)
            }*/
        }

        composable(route = AuthScreen.AUTH_SETTING_SCREEN.route) {
            SettingScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize()
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
            ProfileScreen(profileScreenVideModel = hiltViewModel(), navController = navController)
        }


    }
//    settingNavGraph(navController)
//   logoutGraph(navController =navController)
}

sealed class AuthScreen(val route: String) {
    object START_SCREEN : AuthScreen(route = "start_screen")
    object LANGUAGE_SCREEN : AuthScreen(route = "language_screen")
    object BUG_LOGGING_SCREEN : AuthScreen(route = "Bug_Logging")
    object LOGIN : AuthScreen(route = "login_screen")
    object VILLAGE_SELECTION_SCREEN : AuthScreen(route = "village_selection_screen")
    object OTP_VERIFICATION : AuthScreen(route = "otp_verification_screen/{$ARG_MOBILE_NUMBER}")
    object AUTH_SETTING_SCREEN : AuthScreen(route = "setting_screen")
    object PROFILE_SCREEN : AuthScreen(route = "profile_screen")
    object VIDEO_LIST_SCREEN : AuthScreen(route = "video_list_screen")
    object VIDEO_PLAYER_SCREEN : AuthScreen(route = "video_player_screen/{$ARG_VIDEO_ID}")
}