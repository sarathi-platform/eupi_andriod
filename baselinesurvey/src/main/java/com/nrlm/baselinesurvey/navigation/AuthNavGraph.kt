package com.nrlm.baselinesurvey.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.nrlm.baselinesurvey.ARG_FROM_HOME
import com.nrlm.baselinesurvey.ARG_MOBILE_NUMBER
import com.nrlm.baselinesurvey.navigation.navgraph.Graph
import com.nrlm.baselinesurvey.ui.splash.presentaion.SplashScreenComponent
import com.nrlm.baselinesurvey.ui.auth.presentation.LoginScreenComponent
import com.nrlm.baselinesurvey.ui.auth.presentation.OtpVerificationScreenComponent
import com.nrlm.baselinesurvey.ui.language.presentation.LanguageScreenComponent
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.SurveyeeListScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.START_SCREEN.route
    ) {

        composable(route = AuthScreen.START_SCREEN.route) {
            SplashScreenComponent(
                navController = navController, modifier = Modifier.fillMaxSize(),
                hiltViewModel()
            )
        }
        composable(route = AuthScreen.LANGUAGE_SCREEN.route
        ) {
            LanguageScreenComponent(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                pageFrom = ARG_FROM_HOME
            )

        }

        composable(route = AuthScreen.LOGIN.route) {
            LoginScreenComponent(
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
            OtpVerificationScreenComponent(
                navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize(),
                it.arguments?.getString(ARG_MOBILE_NUMBER).toString()
            )
        }

        composable(route = AuthScreen.SURVEYEE_LIST_SCREEN.route) {
            /*VillageSelectionScreen(navController = navController, viewModel = hiltViewModel()){
                navController.navigate(AuthScreen.AUTH_SETTING_SCREEN.route)
            }*/
//            VillageSelectionScreen()
            SurveyeeListScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                activityName = "",
                missionId = 0,
                activityDate = "",
                activityId = 0
            )
        }

        /*composable(route = AuthScreen.AUTH_SETTING_SCREEN.route) {
            SettingScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(route = AuthScreen.VIDEO_LIST_SCREEN.route) {
            VideoListScreen(navController = navController, modifier = Modifier, viewModel = hiltViewModel())
        }

        composable(route = AuthScreen.PROFILE_SCREEN.route) {
            ProfileScreen(profileScreenVideModel = hiltViewModel(), navController = navController)
        }*/


    }
//    settingNavGraph(navController)
//    logoutGraph(navController =navController)
}

sealed class AuthScreen(val route: String) {
    object START_SCREEN : AuthScreen(route = "start_screen")
    object LANGUAGE_SCREEN : AuthScreen(route = "language_screen")
    object LOGIN : AuthScreen(route = "login_screen")
    object SURVEYEE_LIST_SCREEN : AuthScreen(route = "surveyee_list_screen")
    object OTP_VERIFICATION : AuthScreen(route = "otp_verification_screen/{$ARG_MOBILE_NUMBER}")
    object AUTH_SETTING_SCREEN : AuthScreen(route = "setting_screen")
    object PROFILE_SCREEN : AuthScreen(route = "profile_screen")
    object VIDEO_LIST_SCREEN : AuthScreen(route = "video_list_screen")
}