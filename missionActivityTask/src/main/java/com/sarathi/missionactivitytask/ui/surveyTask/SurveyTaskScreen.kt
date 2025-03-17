package com.sarathi.missionactivitytask.ui.surveyTask

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nudge.core.BLANK_STRING
import com.sarathi.missionactivitytask.ui.components.ShowDidiImageDialog
import com.sarathi.missionactivitytask.ui.grantTask.screen.TaskScreen
import com.sarathi.missionactivitytask.ui.grantTask.screen.TaskScreenContent
import com.sarathi.missionactivitytask.ui.grantTask.screen.TaskScreenContentForGroup
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.SurveyTaskScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent

@Composable
fun SurveyTaskScreen(
    navController: NavController,
    viewModel: SurveyTaskScreenViewModel = hiltViewModel(),
    missionId: Int,
    activityName: String,
    activityId: Int,
    programId: Int,
    onSettingClick: () -> Unit
) {

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(InitDataEvent.InitSurveyTaskScreenState(missionId, activityId))
    }

    if (viewModel.isDidiImageDialogVisible.value.first
        && viewModel.isDidiImageDialogVisible.value.third != null
        && viewModel.isDidiImageDialogVisible.value.third != Uri.EMPTY
    ) {
        ShowDidiImageDialog(
            didiName = viewModel.isDidiImageDialogVisible.value.second ?: BLANK_STRING,
            imagePath = viewModel.isDidiImageDialogVisible.value.third
        ) {
            viewModel.isDidiImageDialogVisible.value = Triple(false, BLANK_STRING, Uri.EMPTY)
        }
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
        taskScreenContent = { vm, mNavController ->
            TaskScreenContent(vm, mNavController, onImageClicked = { path ->
                viewModel.isDidiImageDialogVisible.value = path
            })
        },
        taskScreenContentForGroup = { groupKey, vm, navController ->
            TaskScreenContentForGroup(groupKey, vm, navController, onImageClicked = { path ->
                viewModel.isDidiImageDialogVisible.value = path
            })
        },
        programId = programId
    )


}