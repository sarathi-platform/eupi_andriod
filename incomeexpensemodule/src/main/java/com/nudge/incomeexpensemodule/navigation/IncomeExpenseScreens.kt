package com.nudge.incomeexpensemodule.navigation

import com.nudge.incomeexpensemodule.utils.IncomeExpenseConstants


sealed class IncomeExpenseScreens(val route: String) {
    object DataTabSummaryScreen :
        IncomeExpenseScreens(route = IncomeExpenseConstants.DATA_TAB_SUMMARY_SCREEN_ROUTE_NAME)

}
