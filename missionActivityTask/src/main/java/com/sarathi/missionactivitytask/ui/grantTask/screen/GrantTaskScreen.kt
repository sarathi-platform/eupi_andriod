package com.sarathi.missionactivitytask.ui.grantTask.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.GrantTaskScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent

@Composable
fun GrantTaskScreen(
    navController: NavController = rememberNavController(),
    viewModel: GrantTaskScreenViewModel = hiltViewModel(),
    missionId: Int,
    activityName: String,
    activityId: Int
) {
    LaunchedEffect(key1 = true) {
        viewModel.setMissionActivityId(missionId, activityId)
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitDataState)
    }
    ToolBarWithMenuComponent(
        title = activityName,
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = true,
        isDataAvailable = viewModel.taskList.value.isEmpty(),
        onSearchValueChange = {

        },
        onBottomUI = {
        },
        onContentUI = {
            if (viewModel.taskList.value.isNotEmpty()) {
                GrantTaskList(
                    taskList = viewModel.taskList.value,
                    navController = navController
                )
            }
        }
    )
}

