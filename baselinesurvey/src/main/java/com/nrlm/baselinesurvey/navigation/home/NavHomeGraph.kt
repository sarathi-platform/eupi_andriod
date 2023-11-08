package com.nrlm.baselinesurvey.navigation.home

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nrlm.baselinesurvey.ARG_DIDI_ID
import com.nrlm.baselinesurvey.ARG_SECTION_ID
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.navigation.navgraph.Graph
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionScreen
import com.nrlm.baselinesurvey.ui.section_screen.presentation.SectionListScreen
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.DataLoadingScreenComponent
import com.nrlm.baselinesurvey.ui.surveyee_screen.presentation.SurveyeeListScreen
import com.patsurvey.nudge.navigation.AuthScreen

@Composable
fun NavHomeGraph(navController: NavHostController, prefRepo: PrefRepo) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = HomeScreens.DATA_LOADING_SCREEN.route
    ) {

        composable(route = HomeScreens.DATA_LOADING_SCREEN.route) {
            DataLoadingScreenComponent(viewModel = hiltViewModel(), navController = navController)
        }

        composable(route = HomeScreens.SURVEYEE_LIST_SCREEN.route) {
            SurveyeeListScreen(viewModel = hiltViewModel(), navController = navController)
        }

        composable(route = HomeScreens.SECTION_SCREEN.route, arguments = listOf(
            navArgument(
                name = ARG_DIDI_ID
            ) {
                type = NavType.IntType
            }
        )) {
            SectionListScreen(navController, viewModel = hiltViewModel(), didiId = it.arguments?.getInt(
                ARG_DIDI_ID) ?: 0)
        }

        composable(route = HomeScreens.QUESTION_SCREEN.route, arguments = listOf(
            navArgument(
                name = ARG_SECTION_ID
            ) {
                type = NavType.IntType
            },
            navArgument(
                name = ARG_DIDI_ID
            ) {
                type = NavType.IntType
            }
        )) {
            QuestionScreen(navController = navController, viewModel = hiltViewModel(), surveyeeId = it.arguments?.getInt(
                ARG_DIDI_ID) ?: 0, sectionId = it.arguments?.getInt(
                ARG_SECTION_ID) ?: 0)
        }

    }
}

sealed class HomeScreens(val route: String) {
    object DATA_LOADING_SCREEN : HomeScreens(route = "data_loading_screen")
    object SECTION_SCREEN : HomeScreens(route = "section_screen/{$ARG_DIDI_ID}")
    object QUESTION_SCREEN : HomeScreens(route = "question_screen/{$ARG_SECTION_ID}/{$ARG_DIDI_ID}")
    object SURVEYEE_LIST_SCREEN : HomeScreens(route = "surveyee_list_screen")
}

