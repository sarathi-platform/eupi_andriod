package com.patsurvey.nudge.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.patsurvey.nudge.activities.ui.selectlanguage.LanguageScreen
import com.patsurvey.nudge.activities.ui.login.LoginScreen
import com.patsurvey.nudge.activities.SplashScreen
import com.patsurvey.nudge.activities.ui.login.OtpVerificationScreen
import com.patsurvey.nudge.activities.ui.selectlanguage.LanguageViewModel
import com.patsurvey.nudge.activities.*
import com.patsurvey.nudge.activities.ui.digital_forms.DigitalFormAScreen
import com.patsurvey.nudge.activities.ui.socialmapping.SocialMappingScreen

@Composable
fun StartFlowNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = ScreenRoutes.START_SCREEN.route) {
        composable(route = ScreenRoutes.START_SCREEN.route) {
            SplashScreen(navController = navController, modifier = Modifier.fillMaxSize())
        }
        composable(route = ScreenRoutes.LANGUAGE_SCREEN.route) {
            LanguageScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(route = ScreenRoutes.LOGIN_SCREEN.route) {
            LoginScreen(
                navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(route = ScreenRoutes.OTP_VERIFICATION_SCREEN.route) {
            OtpVerificationScreen(
                navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(route = ScreenRoutes.HOME_SCREEN.route) {
            HomeScreen(navController = navController, modifier = Modifier.fillMaxWidth())
        }
        composable(route = ScreenRoutes.RANKED_DIDI_LIST_SCREEN.route) {
            SocialMappingScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxWidth()
            )
        }
        composable(route = ScreenRoutes.VILLAGE_SELECTION_SCREEN.route) {
            VillageSelectionScreen(navController = navController, viewModel = hiltViewModel())
        }
        composable(route = ScreenRoutes.DIGITAL_FORM_A_SCREEN.route) {
            DigitalFormAScreen(navController = navController, viewModel = hiltViewModel(),modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun VOHomeScreenFlowNavigation(navController: NavHostController,
                               modifier: Modifier = Modifier){
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.VILLAGE_SELECTION_SCREEN.route
    ) {
        composable(route = ScreenRoutes.PROGRESS_SCREEN.route) {
            ProgressScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier), viewModel = hiltViewModel(), navController
            )
        }
        composable(route = ScreenRoutes.DIDI_SCREEN.route) {
            DidiScreen()
        }
        composable(route = ScreenRoutes.MORE_SCREEN.route) {
            MoreScreen()
        }
        composable(route = ScreenRoutes.HOME_SCREEN.route) {
            HomeScreen(navController = navController, modifier = Modifier.fillMaxWidth())
        }
        composable(route = ScreenRoutes.TRANSECT_WALK_SCREEN.route) {
            TransectWalkScreen(
                navController = navController, modifier = Modifier
                    .fillMaxSize()
                    .then(modifier)
            )
        }
        composable(route = ScreenRoutes.VILLAGE_SELECTION_SCREEN.route) {
            VillageSelectionScreen(navController = navController, viewModel = hiltViewModel())
        }
    }
}

@Composable
fun HomeScreenFlowNavigation(
    homeScreenNavController: NavHostController,
    stepsNavHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = homeScreenNavController,
        startDestination = ScreenRoutes.PROGRESS_SCREEN.route
    ) {
        composable(route = ScreenRoutes.PROGRESS_SCREEN.route) {
            ProgressScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier), viewModel = hiltViewModel(), homeScreenNavController
            )
        }
        composable(route = ScreenRoutes.DIDI_SCREEN.route) {
            DidiScreen(modifier = Modifier
                .fillMaxSize()
                .then(modifier))
        }
        composable(route = ScreenRoutes.MORE_SCREEN.route) {
            MoreScreen()
        }

        composable(route = ScreenRoutes.TRANSECT_WALK_SCREEN.route) {
            TransectWalkScreen(
                navController = homeScreenNavController, modifier = Modifier
                    .fillMaxSize()
                    .then(modifier)
            )
        }
        composable(route = ScreenRoutes.DIDI_SCREEN.route) {
            DidiScreen()
        }
        composable(route = ScreenRoutes.MORE_SCREEN.route) {
            MoreScreen()
        }
        composable(route = ScreenRoutes.VILLAGE_SELECTION_SCREEN.route) {
            VillageSelectionScreen(navController = homeScreenNavController, viewModel = hiltViewModel())
        }
    }
    /*NavHost(navController = stepsNavHostController, startDestination = ScreenRoutes.PROGRESS_SCREEN.route) {
        composable(route = ScreenRoutes.PROGRESS_SCREEN.route) {
            ProgressScreen(modifier = Modifier
                .fillMaxSize()
                .then(modifier), /*stepsNavHostController*/)
        }
        composable(route = ScreenRoutes.OTP_VERIFICATION_SCREEN.route) {
            OtpVerificationScreen(homeScreenNavController, viewModel = hiltViewModel(),modifier = Modifier.fillMaxSize())
        }

        composable(route = ScreenRoutes.TRANSECT_WALK_SCREEN.route) {
            TransectWalkScreen(navController = stepsNavHostController, modifier = Modifier
                .fillMaxSize()
                .then(modifier))
        }

        composable(route = ScreenRoutes.SOCIAL_MAPPING_SCREEN.route) {
            TransectWalkScreen(navController = stepsNavHostController, modifier = Modifier
                .fillMaxSize()
                .then(modifier))
        }
    }*/
}