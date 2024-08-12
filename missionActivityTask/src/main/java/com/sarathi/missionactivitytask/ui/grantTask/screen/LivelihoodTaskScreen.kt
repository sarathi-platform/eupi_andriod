package com.sarathi.missionactivitytask.ui.grantTask.screen

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ID
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.value
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardModel
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardSlots
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.missionactivitytask.navigation.navigateToGrantSurveySummaryScreen
import com.sarathi.missionactivitytask.navigation.navigateToLivelihoodDropDownScreen
import com.sarathi.missionactivitytask.ui.basic_content.component.LivelihoodTaskCard
import com.sarathi.missionactivitytask.ui.basic_content.component.TaskCard
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
        taskScreenContent = { vm: TaskScreenViewModel, nvController: NavController ->
            livelihoodTaskScreenContent((vm as LivelihoodTaskScreenViewModel),nvController)
        }

    )
}
fun LazyListScope.livelihoodTaskScreenContent(viewModel: LivelihoodTaskScreenViewModel, navController: NavController) {

    itemsIndexed(
        items = viewModel.filterList.value.entries.toList()
    ) { _, task ->

        LivelihoodTaskRowView(viewModel, navController, task)

        CustomVerticalSpacer()
    }
    item {
        CustomVerticalSpacer(size = dimen_20_dp)
    }

}

@Composable
fun LivelihoodTaskRowView(
    viewModel: LivelihoodTaskScreenViewModel,
    navController: NavController,

    task: MutableMap.MutableEntry<Int, HashMap<String, TaskCardModel>>,
) {
    LivelihoodTaskCard(
        onPrimaryButtonClick = { subjectName ->
            viewModel.activityConfigUiModelWithoutSurvey?.let {
                when (ActivityTypeEnum.getActivityTypeFromId(it.activityTypeId)) {
                    ActivityTypeEnum.LIVELIHOOD -> {
                        navigateToLivelihoodDropDownScreen(
                            navController,
                            taskId = task.key,
                            activityId = viewModel.activityId,
                            missionId = viewModel.missionId,
                            subjectName = subjectName
                        )
                    }

                    else -> {}
                }
            }


        },
        onNotAvailable = {
            if (!viewModel.isActivityCompleted.value) {
                task.value[TaskCardSlots.TASK_STATUS.name] = TaskCardModel(
                    value = SurveyStatusEnum.NOT_AVAILABLE.name,
                    label = BLANK_STRING,
                    icon = null
                )
                viewModel.updateTaskAvailableStatus(
                    taskId = task.key,
                    status = SurveyStatusEnum.NOT_AVAILABLE.name
                )
                viewModel.isActivityCompleted()
            }
        },
        imagePath = viewModel.getFilePathUri(
            task.value[TaskCardSlots.TASK_IMAGE.name]?.value ?: BLANK_STRING
        ),
        title = task.value[TaskCardSlots.TASK_TITLE.name],
        subTitle1 = task.value[TaskCardSlots.TASK_SUBTITLE.name],
        primaryButtonText = task.value[TaskCardSlots.TASK_PRIMARY_BUTTON.name],
        secondaryButtonText = task.value[TaskCardSlots.TASK_SECONDARY_BUTTON.name],
        status = task.value[TaskCardSlots.TASK_STATUS.name],
        subtitle2 = task.value[TaskCardSlots.TASK_SUBTITLE_2.name],
        subtitle3 = task.value[TaskCardSlots.TASK_SUBTITLE_3.name],
        subtitle4 = task.value[TaskCardSlots.TASK_SUBTITLE_4.name],
        subtitle5 = task.value[TaskCardSlots.TASK_SUBTITLE_5.name]?.copy(value = viewModel.livelihoodsEntityList.find { it.livelihoodId==viewModel.subjectLivelihoodMapiingMap.get(viewModel.taskUiModel?.find { it.taskId==task.key }?.subjectId)?.primaryLivelihoodId.value() }?.name.value()),
        subtitle7 = task.value[TaskCardSlots.TASK_SUBTITLE_6.name]?.copy(value = viewModel.livelihoodsEntityList.find { it.livelihoodId==viewModel.subjectLivelihoodMapiingMap.get(viewModel.taskUiModel?.find { it.taskId==task.key }?.subjectId)?.secondaryLivelihoodId.value() }?.name.value()) ,
        subtitle6 = task.value[TaskCardSlots.TASK_SUBTITLE_8.name],
        isActivityCompleted = viewModel.isActivityCompleted.value,
        isNotAvailableButtonEnable = task.value[TaskCardSlots.TASK_NOT_AVAILABLE_ENABLE.name]?.value.equals(
            "true"
        ),
        isShowSecondaryStatusIcon = task.value[TaskCardSlots.TASK_SECOND_STATUS_AVAILABLE.name]?.value.equals(
            "true"
        ),
    )
}


