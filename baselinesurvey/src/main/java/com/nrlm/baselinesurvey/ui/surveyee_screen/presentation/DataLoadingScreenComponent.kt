package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponentWithText
import com.nrlm.baselinesurvey.ui.common_components.ShowCustomDialog
import com.nrlm.baselinesurvey.ui.common_components.common_events.DialogEvents
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.surveyee_screen.viewmodel.DataLoadingScreenViewModel
import com.nudge.navigationmanager.graphs.LogoutScreens
import com.nudge.navigationmanager.graphs.NudgeNavigationGraph
import com.nudge.navigationmanager.routes.MISSION_SUMMARY_SCREEN_ROUTE_NAME

@Composable
fun DataLoadingScreenComponent(
    modifier: Modifier = Modifier,
    viewModel: DataLoadingScreenViewModel,
    navController: NavController,
    missionId: Int,
    missionDescription: String
) {

    val loaderState = viewModel.loaderState.value

    LaunchedEffect(key1 = true) {
        if (viewModel.isUserLoggedIn()) {
            viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
            if (!viewModel.isAllDataFetched()) {
                viewModel.fetchAllData {
                    viewModel.setAllDataFetched()
                    viewModel.setGrantDbMissionDataLoaded()
                    navController.navigate("$MISSION_SUMMARY_SCREEN_ROUTE_NAME/${missionId}/${missionDescription}") {
                        popUpTo(route = LogoutScreens.LOG_DATA_LOADING_SCREEN.route) {
                            inclusive = true
                        }
                    }


                }
            } else {
                navController.navigate("$MISSION_SUMMARY_SCREEN_ROUTE_NAME/${missionId}/${missionDescription}") {
                    popUpTo(route = LogoutScreens.LOG_DATA_LOADING_SCREEN.route) {
                        inclusive = true
                    }
                }
            }
        } else {
            navController.navigate("$MISSION_SUMMARY_SCREEN_ROUTE_NAME/${missionId}/${missionDescription}") {
                popUpTo(route = LogoutScreens.LOG_DATA_LOADING_SCREEN.route) {
                    inclusive = true
                }
            }
        }
    }

    if (viewModel.errorNavigate.value) {
        viewModel.setAllDataFetched()
        navController.navigate("$MISSION_SUMMARY_SCREEN_ROUTE_NAME/${missionId}/${missionDescription}") {
            popUpTo(route = LogoutScreens.LOG_DATA_LOADING_SCREEN.route) {
                inclusive = true
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoaderComponentWithText(visible = loaderState.isLoaderVisible)

        if (viewModel.showUserChangedDialog.value.isDialogVisible) {
            ShowCustomDialog(
                title = stringResource(id = R.string.warning),
                message = stringResource(id = R.string.data_lost_message),
                positiveButtonTitle = stringResource(id = R.string.proceed),
                negativeButtonTitle = stringResource(id = R.string.cancel_text),
                dismissOnBackPress = false,
                onNegativeButtonClick = {
                    viewModel.onEvent(DialogEvents.ShowDialogEvent(false))
                    viewModel.logout()
                    navController.navigate(NudgeNavigationGraph.LOGOUT_GRAPH)
                },
                onPositiveButtonClick = {
                    viewModel.onEvent(DialogEvents.ShowDialogEvent(false))
                    viewModel.clearLocalDB {
                        viewModel.fetchAllData {
                            viewModel.setAllDataFetched()
                            navController.navigate("$MISSION_SUMMARY_SCREEN_ROUTE_NAME/${missionId}/${missionDescription}") {
                                popUpTo(route = LogoutScreens.LOG_DATA_LOADING_SCREEN.route) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
            )
        }

    }

}