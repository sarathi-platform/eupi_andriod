package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.navigation.home.HomeScreens
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.surveyee_screen.viewmodel.DataLoadingScreenViewModel

@Composable
fun DataLoadingScreenComponent(
    modifier: Modifier = Modifier,
    viewModel: DataLoadingScreenViewModel,
    navController: NavController
) {

    val loaderState = viewModel.loaderState.value

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.fetchAllData() {
            navController.navigate(HomeScreens.Home_SCREEN.route)
        }
    }


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoaderComponent(visible = loaderState.isLoaderVisible)
    }

}