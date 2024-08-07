package com.sarathi.missionactivitytask.ui.surveyTask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nudge.core.BLANK_STRING
import com.sarathi.missionactivitytask.ui.grantTask.screen.TaskRowView
import com.sarathi.missionactivitytask.ui.grantTask.screen.TaskScreen
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.SurveyTaskScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent

@Composable
fun SurveyTaskScreen(
    navController: NavController,
    viewModel: SurveyTaskScreenViewModel = hiltViewModel(),
    missionId: Int,
    activityName: String,
    activityId: Int,
    onSettingClick: () -> Unit
) {

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(InitDataEvent.InitSurveyTaskScreenState(missionId, activityId))
    }

    TaskScreen(
        missionId = missionId,
        activityId = activityId,
        activityName = activityName,
        onSettingClick = onSettingClick,
        viewModel = viewModel,
        onSecondaryButtonClick = {
            // Implementation not required here
        },
        isSecondaryButtonEnable = false,
        secondaryButtonText = BLANK_STRING,
        isSecondaryButtonVisible = false,
        taskList = emptyList(),
        navController = navController,
        taskScreenContent = { vm, mNavController, task ->
            TaskRowView(vm, mNavController, task)
        }
    )


}