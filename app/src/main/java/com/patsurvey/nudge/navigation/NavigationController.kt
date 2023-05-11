package com.patsurvey.nudge.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.patsurvey.nudge.activities.ui.selectlanguage.LanguageScreen
import com.patsurvey.nudge.activities.ui.login.LoginScreen
import com.patsurvey.nudge.activities.SplashScreen
import com.patsurvey.nudge.activities.ui.login.OtpVerificationScreen
import com.patsurvey.nudge.activities.*
import com.patsurvey.nudge.activities.StepCompletionScreen
import com.patsurvey.nudge.activities.ui.digital_forms.DigitalFormAScreen
import com.patsurvey.nudge.activities.ui.socialmapping.SocialMappingScreen
import com.patsurvey.nudge.activities.ui.transect_walk.TransectWalkScreen
import com.patsurvey.nudge.utils.*

@Composable
fun StartFlowNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = ScreenRoutes.START_SCREEN.route) {
        composable(route = ScreenRoutes.START_SCREEN.route) {
            SplashScreen(
                navController = navController, modifier = Modifier.fillMaxSize(),
                hiltViewModel()
            )
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
        composable(
            route = ScreenRoutes.OTP_VERIFICATION_SCREEN.route,
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
        composable(route = ScreenRoutes.LOGIN_HOME_SCREEN.route) {
            HomeScreen(navController = navController)
        }
        composable(route = ScreenRoutes.DIGITAL_FORM_A_SCREEN.route) {
            DigitalFormAScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun VOHomeScreenFlowNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val sharedViewModel: AddDidiViewModel = hiltViewModel()
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
            DidiScreen(
                navController,
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier),
                didiViewModel = sharedViewModel
            )
        }
        composable(route = ScreenRoutes.ADD_DIDI_SCREEN.route) {
            AddDidiScreen(
                navController,
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier),
                didiViewModel = sharedViewModel
            )
        }
        composable(route = ScreenRoutes.MORE_SCREEN.route) {
            MoreScreen(navController)
        }
        composable(route = ScreenRoutes.HOME_SCREEN.route) {
            HomeScreen(navController = navController, modifier = Modifier.fillMaxWidth())
        }
        composable(
            route = ScreenRoutes.TRANSECT_WALK_SCREEN.route,
            arguments = listOf(navArgument(ARG_VILLAGE_ID) {
                type = NavType.IntType
            }, navArgument(ARG_STEP_ID) {
                type = NavType.IntArrayType
            })
        ) {
            TransectWalkScreen(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier),
                viewModel = hiltViewModel(),
                villageId = it.arguments?.getInt(ARG_VILLAGE_ID) ?: 0,
                stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1
            )
        }

        composable(
            route = ScreenRoutes.STEP_COMPLETION_SCREEN.route,
            arguments = listOf(navArgument(ARG_COMPLETION_MESSAGE) {
                type = NavType.StringType
            }, navArgument(ARG_FROM_SCREEN) {
                type = NavType.StringType
            }
            )
        ) {
            StepCompletionScreen(navController = navController,
                modifier = Modifier,
                message = it.arguments?.getString(ARG_COMPLETION_MESSAGE) ?: "",
                fromScreen = it.arguments?.getString(ARG_FROM_SCREEN) ?: ScreenRoutes.TRANSECT_WALK_SCREEN.route)
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
    val sharedViewModel: AddDidiViewModel = hiltViewModel()
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
            DidiScreen(
                homeScreenNavController,
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier),
                didiViewModel = sharedViewModel
            )
        }
        composable(route = ScreenRoutes.MORE_SCREEN.route) {
            MoreScreen(
                homeScreenNavController,
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier)
            )
        }

        composable(route = ScreenRoutes.ADD_DIDI_SCREEN.route) {
            AddDidiScreen(
                homeScreenNavController,
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier),
                didiViewModel = sharedViewModel
            )
        }

        composable(
            route = ScreenRoutes.TRANSECT_WALK_SCREEN.route,
            arguments = listOf(navArgument(ARG_VILLAGE_ID) {
                type = NavType.IntType
            }, navArgument(ARG_STEP_ID) {
                type = NavType.IntType
            })
        ) {
            TransectWalkScreen(
                navController = homeScreenNavController,
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier),
                viewModel = hiltViewModel(),
                villageId = it.arguments?.getInt(ARG_VILLAGE_ID) ?: 0,
                stepId = it.arguments?.getInt(ARG_STEP_ID) ?: -1
            )
        }

        composable(
            route = ScreenRoutes.STEP_COMPLETION_SCREEN.route,
            arguments = listOf(navArgument(ARG_COMPLETION_MESSAGE) {
                type = NavType.StringType
            }, navArgument(ARG_FROM_SCREEN) {
                type = NavType.StringType
            }
            )
        ) {
            StepCompletionScreen(navController = homeScreenNavController,
                modifier = Modifier,
                message = it.arguments?.getString(ARG_COMPLETION_MESSAGE) ?: "",
                fromScreen = it.arguments?.getString(ARG_FROM_SCREEN) ?: ScreenRoutes.TRANSECT_WALK_SCREEN.route)
        }

        composable(route = ScreenRoutes.VILLAGE_SELECTION_SCREEN.route) {
            VillageSelectionScreen(
                navController = homeScreenNavController,
                viewModel = hiltViewModel(),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}