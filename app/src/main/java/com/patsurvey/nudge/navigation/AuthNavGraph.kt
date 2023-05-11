package com.patsurvey.nudge.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.patsurvey.nudge.activities.SplashScreen
import com.patsurvey.nudge.activities.VillageSelectionScreen
import com.patsurvey.nudge.activities.ui.login.LoginScreen
import com.patsurvey.nudge.activities.ui.login.OtpVerificationScreen
import com.patsurvey.nudge.activities.ui.selectlanguage.LanguageScreen
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.ARG_MOBILE_NUMBER

fun NavGraphBuilder.authNavGraph(navController: NavHostController, prefRepo: PrefRepo) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = if(prefRepo.getLoginStatus()) AuthScreen.VILLAGE_SELECTION_SCREEN.route else AuthScreen.START_SCREEN.route
    ) {

        composable(route = AuthScreen.START_SCREEN.route) {
            SplashScreen(
                navController = navController, modifier = Modifier.fillMaxSize(),
                hiltViewModel()
            )
        }
        composable(route = AuthScreen.LANGUAGE_SCREEN.route) {
            LanguageScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize()
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
            VillageSelectionScreen(navController = navController, viewModel = hiltViewModel())
        }

    }
}

sealed class AuthScreen(val route: String) {
    object START_SCREEN : AuthScreen(route = "start_screen")
    object LANGUAGE_SCREEN : AuthScreen(route = "language_screen")
    object LOGIN : AuthScreen(route = "login_screen")
    object VILLAGE_SELECTION_SCREEN : AuthScreen(route = "village_selection_screen")
    object OTP_VERIFICATION : AuthScreen(route = "otp_verification_screen/{$ARG_MOBILE_NUMBER}")
}