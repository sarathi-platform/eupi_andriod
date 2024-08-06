package com.nudge.incomeexpensemodule.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.nudge.core.BLANK_STRING
import com.nudge.incomeexpensemodule.ui.add_event_screen.AddEventScreen
import com.nudge.incomeexpensemodule.ui.data_summary_screen.DataSummaryScreen
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_SUBJECT_ID
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_SUBJECT_NAME
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph.INCOME_EXPENSE_GRAPH

fun NavGraphBuilder.IncomeExpenseNavigation(
    navController: NavHostController,
    onSettingIconClick: () -> Unit,
    onBackPressed: () -> Unit
) {
    navigation(
        route = INCOME_EXPENSE_GRAPH,
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
            DataSummaryScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                subjectId = it.arguments?.getInt(
                    ARG_SUBJECT_ID
                ) ?: 0,
                subjectName = it.arguments?.getString(
                    ARG_SUBJECT_NAME
                ) ?: BLANK_STRING
            )
        }
        composable(
            route = IncomeExpenseScreens.AddEventScreen.route, arguments = listOf(
                navArgument(name = ARG_SUBJECT_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = ARG_SUBJECT_ID) {
                    type = NavType.IntType
                }
            )) {
            AddEventScreen(
                navController = navController,
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
    navController.navigate("${IncomeExpenseConstants.DATA_TAB_SUMMARY_SCREEN_ROUTE_NAME}/$subjectId/$subjectName")
}

fun navigateToAddEventScreen(navController: NavController, subjectId: Int, subjectName: String) {
    navController.navigate("${IncomeExpenseConstants.ADD_EVENT_SCREEN_ROUTE_NAME}/$subjectId/$subjectName")
}