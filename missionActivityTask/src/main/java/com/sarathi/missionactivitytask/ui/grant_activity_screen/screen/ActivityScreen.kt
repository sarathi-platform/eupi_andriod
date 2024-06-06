package com.sarathi.missionactivitytask.ui.grant_activity_screen.screen

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sarathi.missionactivitytask.navigation.navigateToMediaPlayerScreen
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.grant_activity_screen.viewmodel.ActivityScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent

@Composable
fun ActivityScreen(
    navController: NavController = rememberNavController(),
    viewModel: ActivityScreenViewModel = hiltViewModel(),
    missionId: Int,
    missionName: String,
    onSettingClick: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitDataState)
        viewModel.setMissionDetail(missionId)
    }
    val context = LocalContext.current
    ToolBarWithMenuComponent(
        title = missionName,
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = false,
        isDataAvailable = !viewModel.loaderState.value.isLoaderVisible && viewModel.activityList.value.isEmpty(),
        onSearchValueChange = {

        },
        onBottomUI = {
        },
        onContentUI = { paddingValues, isSearch, onSearchValueChanged ->
            if (viewModel.activityList.value.isNotEmpty()) {
                ActivityRowCard(
                    activities = viewModel.activityList.value,
                    navController = navController
                ) { contentValue, contentKey, contentType ->
                    if (viewModel.isFilePathExists(contentValue)) {
                        navigateToMediaPlayerScreen(navController, contentKey, contentType)
                    } else {
                        Toast.makeText(
                            context,
                            "file not Exists ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        },
        onSettingClick = onSettingClick
    )
}
