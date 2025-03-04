package com.sarathi.smallgroupmodule.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.nudge.core.value
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph.SMALL_GROUP_GRAPH
import com.sarathi.smallgroupmodule.constatns.SmallGroupConstants.ARG_SELECTED_DATE
import com.sarathi.smallgroupmodule.constatns.SmallGroupConstants.ARG_SMALL_GROUP_ID
import com.sarathi.smallgroupmodule.constatns.SmallGroupConstants.ARG_SUBJECT_ID
import com.sarathi.smallgroupmodule.constatns.SmallGroupConstants.ARG_SUBJECT_NAME
import com.sarathi.smallgroupmodule.constatns.SmallGroupConstants.ARG_VILLAGE_NAME
import com.sarathi.smallgroupmodule.ui.didiTab.presentation.DidiShgVerificationScreen
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation.SmallGroupAttendanceEditScreen
import com.sarathi.smallgroupmodule.ui.smallGroupAttendance.presentation.SmallGroupAttendanceScreen
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.ui.SmallGroupAttendanceHistoryScreen

fun NavGraphBuilder.SmallGroupNavigation(
    navController: NavHostController,
    onSettingIconClick: () -> Unit
) {
    navigation(
        route = SMALL_GROUP_GRAPH,
        startDestination = SmallGroupScreens.SmallGroupAttendanceHistoryScreen.route
    ) {
        //TODO Temp code remove after fixing navigation
        composable(
            route = SmallGroupScreens.SmallGroupAttendanceHistoryScreen.route,
            arguments = listOf(
                navArgument(ARG_SMALL_GROUP_ID) {
                    type = NavType.IntType
                }
            )) {
            SmallGroupAttendanceHistoryScreen(
                navController = navController,
                smallGroupAttendanceHistoryViewModel = hiltViewModel(),
                smallGroupId = it.arguments?.getInt(
                    ARG_SMALL_GROUP_ID
                ) ?: 0
            ) {
                onSettingIconClick()
            }
        }

        composable(
            route = SmallGroupScreens.SmallGroupAttendanceScreen.route,
            arguments =
            listOf(
                navArgument(ARG_SMALL_GROUP_ID) {
                    type = NavType.IntType
                }
            )
        ) {
            SmallGroupAttendanceScreen(
                smallGroupId = it.arguments?.getInt(ARG_SMALL_GROUP_ID) ?: 0,
                smallGroupAttendanceScreenViewModel = hiltViewModel(),
                navHostController = navController,
                onSettingIconClicked = { onSettingIconClick() }
            )
        }

        composable(
            route = SmallGroupScreens.SmallGroupAttendanceEditScreen.route,
            arguments =
            listOf(
                navArgument(ARG_SMALL_GROUP_ID) {
                    type = NavType.IntType
                },
                navArgument(ARG_SELECTED_DATE) {
                    type = NavType.LongType
                }
            )
        ) {
            SmallGroupAttendanceEditScreen(
                smallGroupId = it.arguments?.getInt(ARG_SMALL_GROUP_ID).value(),
                selectedDate = it.arguments?.getLong(ARG_SELECTED_DATE).value(),
                navHostController = navController,
                smallGroupAttendanceEditScreenViewModel = hiltViewModel(),
                onSettingIconClicked = {
                    onSettingIconClick()
                }
            )
        }

        composable(
            route = SmallGroupScreens.ShgVerificationScreen.route,
            arguments =
            listOf(
                navArgument(ARG_SUBJECT_ID) {
                    type = NavType.IntType
                },
                navArgument(ARG_SUBJECT_NAME) {
                    type = NavType.StringType
                },
                navArgument(ARG_VILLAGE_NAME) {
                    type = NavType.StringType
                }
            )
        ) {

            DidiShgVerificationScreen(
                subjectId = it.arguments?.getInt(ARG_SUBJECT_ID).value(),
                didiName = it.arguments?.getString(ARG_SUBJECT_NAME).value(),
                villageName = it.arguments?.getString(ARG_VILLAGE_NAME).value(),
                navController = navController,
                viewModel = hiltViewModel(),
                onSettingClick = onSettingIconClick
            )


        }

    }


}

sealed class SmallGroupScreens(val route: String) {

    object SmallGroupAttendanceHistoryScreen :
        SmallGroupScreens(route = SMALL_GROUP_ATTENDANCE_HISTORY_SCREEN)

    object SmallGroupAttendanceScreen :
        SmallGroupScreens(route = SMALL_GROUP_ATTENDANCE_SCREEN)

    object SmallGroupAttendanceEditScreen :
        SmallGroupScreens(route = SMALL_GROUP_ATTENDANCE_EDIT_SCREEN)
    object ShgVerificationScreen :
        SmallGroupScreens(route = SHG_VERIFICATION_SCREEN)

}

fun NavHostController.navigateToAttendanceHistoryScreen(smallGroupId: Int) {
    this.navigate("$SMALL_GROUP_ATTENDANCE_HISTORY_SCREEN_ROUTE/$smallGroupId")
}

fun NavHostController.navigateToAttendanceEditScreen(smallGroupId: Int, selectedDate: Long) {
    this.navigate("$SMALL_GROUP_ATTENDANCE_EDIT_SCREEN_ROUTE/$smallGroupId/$selectedDate")
}

//TODO Find correct way to navigate back and reload screen with updated state.
fun NavHostController.navigateToHistoryScreenFromAttendance(smallGroupId: Int) {
    this.apply {
        navigateUp()
        navigateUp()
        navigate("$SMALL_GROUP_ATTENDANCE_HISTORY_SCREEN_ROUTE/$smallGroupId")
    }

}
fun NavHostController.navigateToShgVerificationScreen(
    subjectId: Int,
    subjectName: String,
    villageName: String
) {
    this.navigate("$SHG_VERIFICATION_SCREEN_ROUTE/$subjectId/$subjectName/$villageName")
}

//TODO Temp code remove after fixing navigation
const val SMALL_GROUP_ATTENDANCE_HISTORY_SCREEN =
    "small_group_attendance_history_screen/{$ARG_SMALL_GROUP_ID}"
const val SMALL_GROUP_ATTENDANCE_HISTORY_SCREEN_ROUTE = "small_group_attendance_history_screen"

const val SMALL_GROUP_ATTENDANCE_SCREEN_ROUTE = "small_group_attendance_screen"
const val SMALL_GROUP_ATTENDANCE_SCREEN =
    "$SMALL_GROUP_ATTENDANCE_SCREEN_ROUTE/{$ARG_SMALL_GROUP_ID}"

const val SMALL_GROUP_ATTENDANCE_EDIT_SCREEN_ROUTE = "small_group_attendance_edit_screen"
const val SHG_VERIFICATION_SCREEN_ROUTE = "shg_verification_screen"


const val SMALL_GROUP_ATTENDANCE_EDIT_SCREEN =
    "$SMALL_GROUP_ATTENDANCE_EDIT_SCREEN_ROUTE/{$ARG_SMALL_GROUP_ID}/{$ARG_SELECTED_DATE}"

const val SHG_VERIFICATION_SCREEN =
    "$SHG_VERIFICATION_SCREEN_ROUTE/{$ARG_SUBJECT_ID}/{$ARG_SUBJECT_NAME}/{${ARG_VILLAGE_NAME}}"

