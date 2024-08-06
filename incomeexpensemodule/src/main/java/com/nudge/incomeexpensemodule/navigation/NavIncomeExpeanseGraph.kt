package com.nudge.incomeexpensemodule.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.nudge.core.BLANK_STRING
import com.nudge.incomeexpensemodule.ui.LivelihoodScreen
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_SUBJECT_ID
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_SUBJECT_NAME
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph.MAT_GRAPH

fun NavGraphBuilder.IncomeExpenseNavigation(
    navController: NavHostController,
    onSettingIconClick: () -> Unit,
    onBackPressed: () -> Unit
) {
    navigation(
        route = MAT_GRAPH,
        startDestination = IncomeExpenseScreens.DataTabSummaryScreen.route
    ) {
        composable(
            route = IncomeExpenseScreens.DataTabSummaryScreen.route, arguments = listOf(
                navArgument(name = ARG_SUBJECT_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = ARG_SUBJECT_ID) {
                    type = NavType.IntType
                }
            )) {
            LivelihoodScreen(
                navHostController = navController,
                subjectId = it.arguments?.getInt(
                    ARG_SUBJECT_ID
                ) ?: 0,
                subjectName = it.arguments?.getString(
                    ARG_SUBJECT_NAME
                ) ?: BLANK_STRING
            )
        }
    }
}

fun navigateToDataSummaryScreen(navController: NavController, subjectId: Int, subjectName: String) {
    navController.navigate("${IncomeExpenseScreens.DataTabSummaryScreen}/$subjectId/$subjectName")
}