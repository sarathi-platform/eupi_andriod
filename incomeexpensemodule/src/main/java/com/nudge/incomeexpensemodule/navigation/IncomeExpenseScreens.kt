package com.nudge.incomeexpensemodule.navigation

import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_SHOW_DELETE_BUTTON
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_SUBJECT_ID
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_SUBJECT_NAME
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_TRANSACTION_ID


sealed class IncomeExpenseScreens(val route: String) {
    object DataTabSummaryScreen :
        IncomeExpenseScreens(route = "${IncomeExpenseConstants.DATA_TAB_SUMMARY_SCREEN_ROUTE_NAME}/{${ARG_SUBJECT_ID}}/{${ARG_SUBJECT_NAME}}")

    object AddEventScreen :
        IncomeExpenseScreens(route = "${IncomeExpenseConstants.ADD_EVENT_SCREEN_ROUTE_NAME}/{${ARG_SUBJECT_ID}}/{${ARG_SUBJECT_NAME}}/{$ARG_TRANSACTION_ID}/{$ARG_SHOW_DELETE_BUTTON}")
    object EditHistoryScreen :
        IncomeExpenseScreens(route = "${IncomeExpenseConstants.EDIT_HISTORY_SCREEN_ROUTE_NAME}/{${ARG_TRANSACTION_ID}}")
}
