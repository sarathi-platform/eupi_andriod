package com.sarathi.missionactivitytask.ui.activities.select

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.ANIMATE_COLOR
import com.nudge.core.BLANK_STRING
import com.nudge.core.EXPANSTION_TRANSITION_DURATION
import com.nudge.core.ROTATION_DEGREE_TRANSITION
import com.nudge.core.TRANSITION
import com.nudge.core.customGridHeight
import com.nudge.core.showCustomToast
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.CardArrow
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.buttonTextStyle
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_100_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_3_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.nudge.core.value
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardModel
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardSlots
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.basic_content.component.ImageViewer
import com.sarathi.missionactivitytask.ui.basic_content.component.SubContainerView
import com.sarathi.missionactivitytask.ui.components.CircularImageViewComponent
import com.sarathi.missionactivitytask.ui.components.OptionCard
import com.sarathi.missionactivitytask.ui.components.RadioOptionTypeComponent
import com.sarathi.missionactivitytask.ui.components.ShowDidiImageDialog
import com.sarathi.missionactivitytask.ui.grantTask.screen.TaskScreen
import com.sarathi.missionactivitytask.utils.StatusEnum
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.surveymanager.ui.component.GridTypeComponent

@Composable
fun ActivitySelectTaskScreen(
    navController: NavController = rememberNavController(),
    viewModel: ActivitySelectTaskViewModel = hiltViewModel(),
    missionId: Int,
    activityName: String,
    activityId: Int,
    onSettingClick: () -> Unit
) {
    LaunchedEffect(key1 = viewModel.taskUiList.value) {
        viewModel.onEvent(InitDataEvent.InitActivitySelectTaskScreenState(missionId, activityId))
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
        taskList = emptyList(),
        navController = navController,
        taskScreenContent = { _, _ ->
            selectActivityTaskScreenContent(viewModel = viewModel)
        },
        taskScreenContentForGroup = { groupKey, _, _ ->
            selectActivityTaskScreenContentForGroup(groupKey, viewModel)
        }
    )
}

fun LazyListScope.selectActivityTaskScreenContent(viewModel: ActivitySelectTaskViewModel) {
    itemsIndexed(
        items = viewModel.filterList.value.entries.toList()
    ) { _, task ->
        ExpandableTaskCardRow(
            viewModel = viewModel,
            task = task,
            questionUIModel = viewModel.questionUiModel.value[task.key],
        )
        CustomVerticalSpacer()
    }
    item {
        CustomVerticalSpacer(size = dimen_20_dp)
    }

}

fun LazyListScope.selectActivityTaskScreenContentForGroup(
    groupKey: String,
    viewModel: ActivitySelectTaskViewModel
) {

    itemsIndexed(
        items = viewModel.filterTaskMap[groupKey].value()
    ) { _, task ->
        ExpandableTaskCardRow(
            viewModel = viewModel,
            task = task,
            questionUIModel = viewModel.questionUiModel.value[task.key],
        )
        CustomVerticalSpacer()
    }
    item {
        CustomVerticalSpacer(size = dimen_20_dp)
    }

}

@Composable
fun CustomTextView(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen_20_dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = buttonTextStyle,
            color = blueDark
        )

    }
}


@SuppressLint("UnrememberedMutableState")
@Composable
fun ExpandableTaskCardRow(
    viewModel: ActivitySelectTaskViewModel,
    questionUIModel: QuestionUiModel?,
    task: MutableMap.MutableEntry<Int, HashMap<String, TaskCardModel>>
) {
    ExpandableTaskCard(
        title = task.value[TaskCardSlots.TASK_TITLE.name],
        subTitle1 = task.value[TaskCardSlots.TASK_SUBTITLE.name],
        status = task.value[TaskCardSlots.TASK_STATUS.name],
        imagePath = viewModel.getFilePathUri(
            task.value[TaskCardSlots.TASK_IMAGE.name]?.value ?: BLANK_STRING
        ) ?: Uri.EMPTY,
        modifier = Modifier,
        questionUiModel = questionUIModel,
        expanded = viewModel.expandedIds.contains(task.key),
        onExpendClick = { _, _ ->
            if (viewModel.expandedIds.contains(task.key)) {
                viewModel.expandedIds.remove(task.key)
            } else {
                viewModel.expandedIds.add(task.key)
            }
        },
        isNotAvailableButtonEnable = task.value[TaskCardSlots.TASK_NOT_AVAILABLE_ENABLE.name]?.value.equals(
            "true"
        ),
        onNotAvailableClick = {
            if (!viewModel.isActivityCompleted.value) {
                task.value[TaskCardSlots.TASK_STATUS.name] = TaskCardModel(
                    value = SurveyStatusEnum.NOT_AVAILABLE.name,
                    label = BLANK_STRING,
                    icon = null
                )

                questionUIModel?.let { question ->
                    question.options?.forEach {
                        it.isSelected = false
                        it.selectedValue = BLANK_STRING
                    }
                    viewModel.saveSingleAnswerIntoDb(
                        currentQuestionUiModel = questionUIModel,
                        subjectType = viewModel.activityConfigUiModelWithoutSurvey?.subject.value(),
                        taskId = task.key
                    )
                }

                viewModel.updateTaskAvailableStatus(
                    taskId = task.key,
                    status = SurveyStatusEnum.NOT_AVAILABLE.name
                )
                viewModel.isActivityCompleted()
            }
        },
        isActivityCompleted = viewModel.isActivityCompleted.value,
        viewModel = viewModel,
        onAnswerSelection = { _, _ ->
            if (questionUIModel != null) {
                viewModel.saveSingleAnswerIntoDb(
                    currentQuestionUiModel = questionUIModel,
                    subjectType = viewModel.activityConfigUiModelWithoutSurvey?.subject.value(),
                    taskId = task.key
                )
                viewModel.updateTasStatus(
                    taskId = task.key,
                    status = SurveyStatusEnum.COMPLETED.name
                )

            }
        }

    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ExpandableTaskCard(
    title: TaskCardModel?,
    subTitle1: TaskCardModel?,
    status: TaskCardModel?,
    imagePath: Uri,
    modifier: Modifier,
    viewModel: ActivitySelectTaskViewModel,
    questionUiModel: QuestionUiModel?,
    expanded: Boolean,
    onExpendClick: (Boolean, TaskCardModel) -> Unit,
    isNotAvailableButtonEnable: Boolean,
    isActivityCompleted: Boolean,
    onNotAvailableClick: () -> Unit,
    onAnswerSelection: (optionValue: String, optionId: Int) -> Unit
) {
    val taskStatus = remember(status?.value) {
        mutableStateOf(status?.value)
    }
    val taskMarkedNotAvailable = remember(status?.value) {
        mutableStateOf(status?.value == StatusEnum.NOT_AVAILABLE.name)
    }
    val transition = updateTransition(expanded, label = TRANSITION)
    val context = LocalContext.current

    val animateColor by transition.animateColor({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = ANIMATE_COLOR) {
        if (it) greenOnline else textColorDark
    }

    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = ROTATION_DEGREE_TRANSITION) {
        if (it) 180f else 0f
    }

    BasicCardView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen_16_dp)
            .border(width = dimen_1_dp, color = greenOnline, shape = RoundedCornerShape(dimen_6_dp))
            .background(Color.Transparent)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(white)
        ) {
            CardHeader(
                title = title,
                subTitle1 = subTitle1,
                imagePath = imagePath,
                expanded = expanded,
                onExpendClick = onExpendClick,
                arrowRotationDegree = arrowRotationDegree,
                animateColor = animateColor,
                viewModel = viewModel
            )

            CardContent(
                expanded = expanded,
                questionUiModel = questionUiModel,
                taskMarkedNotAvailable = taskMarkedNotAvailable,
                onAnswerSelection = onAnswerSelection,
                isActivityCompleted = isActivityCompleted,
                taskStatus = taskStatus,
                onNotAvailableClick = onNotAvailableClick,
                context = context,
                isNotAvailableButtonEnable = isNotAvailableButtonEnable
            )
        }
    }
}

@Composable
fun CardHeader(
    title: TaskCardModel?,
    subTitle1: TaskCardModel?,
    imagePath: Uri,
    expanded: Boolean,
    onExpendClick: (Boolean, TaskCardModel) -> Unit,
    arrowRotationDegree: Float,
    animateColor: Color,
    viewModel: ActivitySelectTaskViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimen_16_dp,
                top = dimen_10_dp,
                bottom = dimen_5_dp,
                end = dimen_16_dp
            )
            .clickable {
                title?.let { onExpendClick(expanded, it) }
            },
        horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (imagePath != Uri.EMPTY) {
            CircularImageViewComponent(modifier = Modifier, imagePath = imagePath, onImageClick = {
                viewModel.isDidiImageDialogVisible.value = true
            })
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                title?.value?.let {
                    if (it.contains("vo", true)) {
                        title.icon?.let {
                            ImageViewer(it)
                            Spacer(modifier = Modifier.width(dimen_5_dp))
                        }
                        Spacer(modifier = Modifier.width(dimen_3_dp))
                    }
                    Text(text = title.value, style = buttonTextStyle, color = blueDark)
                }
            }
            SubContainerView(subTitle1)
        }

        CardArrow(
            modifier = Modifier,
            degrees = arrowRotationDegree,
            iconColor = animateColor,
            arrowIcon = R.drawable.ic_baseline_keyboard_arrow_down_24,
            onClick = { title?.let { onExpendClick(expanded, it) } }
        )
    }
    if (viewModel.isDidiImageDialogVisible.value) {
        ShowDidiImageDialog(didiName = title?.value ?: BLANK_STRING, imagePath = imagePath) {
            viewModel.isDidiImageDialogVisible.value = false
        }
    }
}

@Composable
fun CardContent(
    expanded: Boolean,
    questionUiModel: QuestionUiModel?,
    taskMarkedNotAvailable: MutableState<Boolean>,
    onAnswerSelection: (optionValue: String, optionId: Int) -> Unit,
    isActivityCompleted: Boolean,
    taskStatus: MutableState<String?>,
    onNotAvailableClick: () -> Unit,
    context: Context,
    isNotAvailableButtonEnable: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen_16_dp)
    ) {
        if (expanded) {
            RadioTypeOptionsUI(
                questionUiModel = questionUiModel,
                taskMarkedNotAvailable = taskMarkedNotAvailable,
                onAnswerSelection = onAnswerSelection,
                isActivityCompleted = isActivityCompleted,
                taskStatus = taskStatus,
                onNotAvailableClick = onNotAvailableClick,
                context = context,
                isNotAvailableButtonEnable = isNotAvailableButtonEnable
            )
        } else {
            DisplaySelectedOption(questionUiModel, taskStatus.value)
        }
    }
}

@Composable
fun DisplaySelectedOption(questionUiModel: QuestionUiModel?, taskStatus: String?) {
    var options = BLANK_STRING

    options = if (options.isNullOrEmpty() && taskStatus == StatusEnum.NOT_AVAILABLE.name) {
        stringResource(id = R.string.not_available)
    } else {
        questionUiModel?.options?.filter { it.isSelected == true }?.map { it.selectedValue }
            ?.joinToString(",").toString()
    }
    if (!options.isNullOrEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimen_5_dp)
        ) {
            Divider(
                modifier = Modifier
                    .height(dimen_1_dp)
                    .weight(1f)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimen_0_dp,
                    end = dimen_16_dp,
                    bottom = dimen_10_dp,
                    top = dimen_10_dp
                ),
            horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)
        ) {
            SubContainerView(
                TaskCardModel(
                    label = options.toString(),
                    value = BLANK_STRING,
                    icon = null
                )
            )
        }
    }
}



@Composable
private fun RadioTypeOptionsUI(
    questionUiModel: QuestionUiModel?,
    taskMarkedNotAvailable: MutableState<Boolean>,
    onAnswerSelection: (optionValue: String, optionId: Int) -> Unit,
    isActivityCompleted: Boolean,
    taskStatus: MutableState<String?>,
    onNotAvailableClick: () -> Unit,
    context: Context,
    isNotAvailableButtonEnable: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(dimen_100_dp, customGridHeight(questionUiModel?.options?.size ?: 0)),
    ) {
        questionUiModel?.options?.sortedBy { it.order }?.let {
            when (questionUiModel.type) {
                QuestionType.RadioButton.name,
                QuestionType.Toggle.name -> {
                    val selectedValue =
                        it.find { it.isSelected == true }?.selectedValue ?: BLANK_STRING
                        RadioOptionTypeComponent(
                            optionItemEntityState = it,
                            isTaskMarkedNotAvailable = taskMarkedNotAvailable,
                            selectedValue = selectedValue,
                            isActivityCompleted = isActivityCompleted
                        ) { selectedIndex, optionValue, optionId ->
                            questionUiModel.options?.let { options ->
                                options.forEach {
                                    it.isSelected = false
                                    it.selectedValue = BLANK_STRING
                                }
                                options[selectedIndex].isSelected = true
                                options[selectedIndex].selectedValue = optionValue
                            }
                            onAnswerSelection(optionValue, optionId)
                        }
                    }

                    QuestionType.MultiSelect.name -> {
                        GridTypeComponent(
                            questionDisplay = questionUiModel.questionDisplay,
                            optionUiModelList = it,
                            questionIndex = 0,
                            areOptionsEnabled = !isActivityCompleted,
                            maxCustomHeight = customGridHeight(it.size),
                            isQuestionDisplay = false,
                            showCardView = false,
                            isTaskMarkedNotAvailable = taskMarkedNotAvailable,
                            onAnswerSelection = { selectedOptionIndex, isSelected ->
                                if (!isActivityCompleted) {
                                    questionUiModel.options?.get(selectedOptionIndex)?.isSelected =
                                        isSelected
                                    taskMarkedNotAvailable.value = false
                                    onAnswerSelection(BLANK_STRING, selectedOptionIndex)
                                }

                            }, questionDetailExpanded = {}
                        )
                    }
                }

            }
    }
    CustomVerticalSpacer()
    Divider(
        Modifier
            .fillMaxWidth()
            .height(dimen_2_dp)
    )
    CustomVerticalSpacer()
    NotAvailableUI(
        isNotAvailableButtonEnable = isNotAvailableButtonEnable,
        taskMarkedNotAvailable = taskMarkedNotAvailable,
        isActivityCompleted = isActivityCompleted,
        taskStatus = taskStatus,
        onNotAvailableClick = onNotAvailableClick,
        context = context
    )

    CustomVerticalSpacer()
}

@Composable
private fun NotAvailableUI(
    isNotAvailableButtonEnable: Boolean,
    taskMarkedNotAvailable: MutableState<Boolean>,
    isActivityCompleted: Boolean,
    taskStatus: MutableState<String?>,
    onNotAvailableClick: () -> Unit,
    context: Context
) {
    if (isNotAvailableButtonEnable) {
        OptionCard(
            modifier = Modifier
                .fillMaxWidth(),
            backgroundColor = if (!taskMarkedNotAvailable.value
            ) Color.Transparent else blueDark, textColor = if (taskMarkedNotAvailable.value
            ) white else blueDark,
            optionText = stringResource(id = R.string.not_available)
        ) {
            if (!isActivityCompleted) {
                taskMarkedNotAvailable.value = true
                taskStatus.value = SurveyStatusEnum.NOT_AVAILABLE.name
                onNotAvailableClick()
            } else {
                showCustomToast(
                    context,
                    context.getString(R.string.activity_completed_unable_to_edit)
                )
            }
        }
    }
}

