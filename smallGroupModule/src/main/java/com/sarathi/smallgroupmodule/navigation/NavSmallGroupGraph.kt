package com.sarathi.smallgroupmodule.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.sarathi.smallgroupmodule.constatns.SmallGroupConstants.ARG_SMALL_GROUP_ID
import com.sarathi.smallgroupmodule.constatns.SmallGroupConstants.SMALL_GROUP_GRAPH
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation.SmallGroupAttendanceHistoryScreen

fun NavGraphBuilder.SmallGroupNavigation(navController: NavHostController) {
    navigation(
        route = SMALL_GROUP_GRAPH,
        startDestination = SmallGroupScreens.SmallGroupAttendanceHistoryScreen.route
    ) {
        //TODO Temp code remove after fixing navigation
        composable(route = SmallGroupScreens.SmallGroupAttendanceHistoryScreen.route,
            arguments = listOf(
                navArgument(ARG_SMALL_GROUP_ID) {
                    type = NavType.IntType
                }
            )) {
            SmallGroupAttendanceHistoryScreen(
                smallGroupAttendanceHistoryViewMode = hiltViewModel(),
                smallGroupId = it.arguments?.getInt(
                    ARG_SMALL_GROUP_ID
                ) ?: 0
            )
        }

    }


}

sealed class SmallGroupScreens(val route: String) {

    object SmallGroupAttendanceHistoryScreen :
        SmallGroupScreens(route = SMALL_GROUP_ATTENDANCE_HISTORY_SCREEN)

}

fun NavHostController.navigateToAttendanceHistoryScreen(smallGroupId: Int) {
    this.navigate("$SMALL_GROUP_ATTENDANCE_HISTORY_SCREEN_ROUTE/$smallGroupId")
}


//TODO Temp code remove after fixing navigation
const val SMALL_GROUP_ATTENDANCE_HISTORY_SCREEN =
    "small_group_attendance_history_screen/{$ARG_SMALL_GROUP_ID}"
const val SMALL_GROUP_ATTENDANCE_HISTORY_SCREEN_ROUTE = "small_group_attendance_history_screen"