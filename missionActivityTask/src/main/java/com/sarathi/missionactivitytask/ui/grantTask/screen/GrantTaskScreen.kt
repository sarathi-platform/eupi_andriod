package com.sarathi.missionactivitytask.ui.grantTask.screen

import android.net.Uri
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.sarathi.missionactivitytask.navigation.navigateToDisbursmentSummaryScreen
import com.sarathi.missionactivitytask.ui.components.ShowDidiImageDialog
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.GrantTaskScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GrantTaskScreen(
    navController: NavController = rememberNavController(),
    viewModel: GrantTaskScreenViewModel = hiltViewModel(),
    missionId: Int,
    activityName: String,
    activityId: Int,
    programId: Int,
    onSettingClick: () -> Unit
) {
    LaunchedEffect(Unit) {

        viewModel.onEvent(InitDataEvent.InitGrantTaskScreenState(missionId, activityId))

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
            navigateToDisbursmentSummaryScreen(
                navController = navController,
                activityId = activityId,
                missionId = missionId,
                isFromSettingScreen = false,
                taskIdList = viewModel.getTaskListOfDisburesementAmountEqualSanctionedAmount()
            )
        },
        isSecondaryButtonEnable = viewModel.isGenerateFormButtonEnable.value,
        secondaryButtonText = viewModel.formEGenerateButtonText.value,
        isSecondaryButtonVisible = viewModel.isGenerateFormButtonVisible.value,
        taskList = emptyList(),//viewModel.taskUiList.value,
        navController = navController,
        programId = programId,
        taskScreenContent = { vm, mNavController ->
            TaskScreenContent(vm, mNavController, onImageClicked = { path ->
                viewModel.isDidiImageDialogVisible.value = path
            })
        },
        taskScreenContentForGroup = { groupKey, vm, navController ->
            TaskScreenContentForGroup(groupKey, vm, navController, { path ->
                viewModel.isDidiImageDialogVisible.value = path
            })
        }

    )
}

