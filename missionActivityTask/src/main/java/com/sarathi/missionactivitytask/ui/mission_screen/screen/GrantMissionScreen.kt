package com.sarathi.missionactivitytask.ui.mission_screen.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.basic_content.component.BasicMissionCard
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.mission_screen.viewmodel.MissionScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent

@Composable
fun GrantMissionScreen(
    navController: NavController = rememberNavController(),
    viewModel: MissionScreenViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitDataState)
    }
    DisposableEffect(key1 = true) {
        onDispose {
            viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
        }
    }
    ToolBarWithMenuComponent(
        title = "SARATHI",
        iconResId = R.drawable.ic_sarathi_logo,
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = true,
        isDataAvailable = viewModel.missionList.value.isEmpty(),
        onSearchValueChange = {},
        onBottomUI = {
        },
        onContentUI = {
            if (viewModel.missionList.value.isNotEmpty()) {
                LazyColumn {
                    items(viewModel.missionList.value) { mission ->
                        BasicMissionCard(
                            countStatusText = "Activities Completed",
                            topHeaderText = "Due by 22 March",
                            totalCount = 2,
                            title = "a",//mission.missionName,
                            needToShowProgressBar = true,
                            primaryButtonText = "Start",
                            onPrimaryClick = {
//                                navigateToActivityScreen(navController)
                            }

                        )
                    }
                }
            }
        }
    )


}