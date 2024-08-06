package com.nudge.incomeexpensemodule.navigation

import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_SUBJECT_ID
import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants.ARG_SUBJECT_NAME


sealed class IncomeExpenseScreens(val route: String) {
    object DataTabSummaryScreen :
        IncomeExpenseScreens(route = "${IncomeExpenseConstants.DATA_TAB_SUMMARY_SCREEN_ROUTE_NAME}/{${ARG_SUBJECT_ID}}/{${ARG_SUBJECT_NAME}}")

    object AddEventScreen :
        IncomeExpenseScreens(route = "${IncomeExpenseConstants.ADD_EVENT_SCREEN_ROUTE_NAME}/{${ARG_SUBJECT_ID}}/{${ARG_SUBJECT_NAME}}")
}
