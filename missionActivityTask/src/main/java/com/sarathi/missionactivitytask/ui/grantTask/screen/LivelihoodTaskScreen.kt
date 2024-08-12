package com.sarathi.missionactivitytask.ui.grantTask.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.LivelihoodTaskScreenViewModel
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.TaskScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent

@Composable
fun LivelihoodTaskScreen(
    navController: NavController = rememberNavController(),
    viewModel: LivelihoodTaskScreenViewModel = hiltViewModel(),
    missionId: Int,
    activityName: String,
    activityId: Int,
    onSettingClick: () -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.onEvent(InitDataEvent.InitLivelihoodPlanningScreenState(missionId, activityId))
    }
    TaskScreen(
        missionId = missionId,
        activityId = activityId,
        activityName = activityName,
        onSettingClick = onSettingClick,
        viewModel = viewModel,
        onSecondaryButtonClick = {

        },
        isSecondaryButtonEnable = false,
        secondaryButtonText = BLANK_STRING,
        isSecondaryButtonVisible = false,
        taskList = emptyList(),//viewModel.taskUiList.value,
        navController = navController,
        taskScreenContent = { vm: TaskScreenViewModel, _: NavController ->

        }

    )
}

