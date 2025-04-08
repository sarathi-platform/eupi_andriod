package com.nudge.incomeexpensemodule.navigation

import android.text.TextUtils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.nudge.core.BLANK_STRING
import com.nudge.core.CLEAN_ROUTE_DELIMITER
import com.nudge.core.FORWARD_SLASH_DELIMITER
import com.nudge.core.value
import com.nudge.incomeexpensemodule.ui.add_event_screen.AddEventScreen
import com.nudge.incomeexpensemodule.ui.data_summary_screen.DataSummaryScreen
import com.nudge.incomeexpensemodule.ui.edit_history_screen.EditHistoryScreen
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.API_FAILED_SCREEN_SCREEN_ROUTE_NAME
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_MODULE_NAME
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_SCREEN_NAME
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_SHOW_DELETE_BUTTON
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_SUBJECT_ID
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_SUBJECT_NAME
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_TRANSACTION_ID
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph.INCOME_EXPENSE_GRAPH
import com.sarathi.dataloadingmangement.ui.component.api_failed_screen.ApiFailedListScreen

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
                ) ?: BLANK_STRING,
                onSettingClick = onSettingIconClick
            )
        }
        composable(
            route = IncomeExpenseScreens.AddEventScreen.route, arguments = listOf(
                navArgument(name = ARG_SUBJECT_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = ARG_SUBJECT_ID) {
                    type = NavType.IntType
                },
                navArgument(name = ARG_TRANSACTION_ID) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(name = ARG_SHOW_DELETE_BUTTON) {
                    type = NavType.BoolType
                }
            )) {
            AddEventScreen(
                navController = navController,
                subjectId = it.arguments?.getInt(
                    ARG_SUBJECT_ID
                ) ?: 0,
                subjectName = it.arguments?.getString(
                    ARG_SUBJECT_NAME
                ) ?: BLANK_STRING,
                transactionId = it.arguments?.getString(
                    ARG_TRANSACTION_ID
                ) ?: BLANK_STRING,
                showDeleteButton = it.arguments?.getBoolean(ARG_SHOW_DELETE_BUTTON).value(),
                onSettingClick = onSettingIconClick
            )
        }

        composable(route = IncomeExpenseScreens.EditHistoryScreen.route, arguments = listOf(
            navArgument(name = ARG_TRANSACTION_ID) {
                type = NavType.StringType
                nullable = true
            }
        )) {
            EditHistoryScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                transactionId = it.arguments?.getString(
                    ARG_TRANSACTION_ID
                ) ?: BLANK_STRING,
                onSettingClick = onSettingIconClick
            )
        }

        composable(
            route = IncomeExpenseScreens.ApiFailedScreen.route, arguments = listOf(
                navArgument(
                    name = ARG_SCREEN_NAME
                ) {
                    type = NavType.StringType
                },
                navArgument(
                    name = ARG_MODULE_NAME
                ) {
                    type = NavType.StringType
                },
            )
        ) {
            ApiFailedListScreen(
                onSettingClick = onSettingIconClick,
                navController = navController,
                viewModel = hiltViewModel(),
                screenName = it.arguments?.getString(ARG_SCREEN_NAME) ?: BLANK_STRING,
                moduleName = addSlashInString(
                    it.arguments?.getString(ARG_MODULE_NAME) ?: BLANK_STRING
                )
            )
        }

    }
}

fun navigateToApiFailedScreen(
    navController: NavController,
    screenName: String,
    moduleName: String,
) {
    navController.navigate(
        "$API_FAILED_SCREEN_SCREEN_ROUTE_NAME/$screenName/${
            removeSlashFromString(
                moduleName
            )
        }"
    )
}
fun navigateToDataSummaryScreen(navController: NavController, subjectId: Int, subjectName: String) {
    navController.navigate("${IncomeExpenseConstants.DATA_TAB_SUMMARY_SCREEN_ROUTE_NAME}/$subjectId/$subjectName")
}
fun navigateToEditHistoryScreen(navController: NavController, transactionID: String) {
    navController.navigate("${IncomeExpenseConstants.EDIT_HISTORY_SCREEN_ROUTE_NAME}/$transactionID")
}

fun navigateToAddEventScreen(
    navController: NavController,
    subjectId: Int,
    subjectName: String,
    transactionID: String,
    showDeleteButton: Boolean,
) {
    val mTransactionId = if (!TextUtils.equals(transactionID, BLANK_STRING)) transactionID else null
    navController.navigate("${IncomeExpenseConstants.ADD_EVENT_SCREEN_ROUTE_NAME}/$subjectId/$subjectName/$mTransactionId/$showDeleteButton")
}
private fun removeSlashFromString(name: String) =
    name.replace(FORWARD_SLASH_DELIMITER, CLEAN_ROUTE_DELIMITER)

private fun addSlashInString(name: String) =
    name.replace(CLEAN_ROUTE_DELIMITER, FORWARD_SLASH_DELIMITER)