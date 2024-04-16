package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.navigation.home.HomeScreens
import com.nrlm.baselinesurvey.navigation.navgraph.Graph
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.common_components.ShowCustomDialog
import com.nrlm.baselinesurvey.ui.common_components.common_events.DialogEvents
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.surveyee_screen.viewmodel.DataLoadingScreenViewModel

@Composable
fun DataLoadingScreenComponent(
    modifier: Modifier = Modifier,
    viewModel: DataLoadingScreenViewModel,
    navController: NavController
) {

    val loaderState = viewModel.loaderState.value
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        if (viewModel.isUserLoggedIn()) {
            viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
            viewModel.compareWithPreviousUser{ isDataLoadingAllowed ->
                if (isDataLoadingAllowed) {
                    if (!viewModel.isAllDataFetched()) {
                        viewModel.fetchAllData {
                            viewModel.setAllDataFetched()
                            navController.navigate(HomeScreens.Home_SCREEN.route)
                        }
                    } else {
                        navController.navigate(HomeScreens.Home_SCREEN.route)
                    }
                } else {
                    viewModel.onEvent(DialogEvents.ShowDialogEvent(true))
                }
            }
        } else {
            navController.navigate(HomeScreens.Home_SCREEN.route)
        }
    }


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoaderComponent(visible = loaderState.isLoaderVisible)

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
                    navController.navigate(Graph.LOGOUT_GRAPH)
                },
                onPositiveButtonClick = {
                    viewModel.onEvent(DialogEvents.ShowDialogEvent(false))
                    viewModel.clearLocalDB {
                        viewModel.fetchAllData {
                            viewModel.setAllDataFetched()
                            navController.navigate(HomeScreens.Home_SCREEN.route)
                        }
                    }
                }
            )
        }

    }

}