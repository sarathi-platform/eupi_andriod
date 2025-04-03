package com.sarathi.missionactivitytask.ui.activities.select

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.ANIMATE_COLOR
import com.nudge.core.BLANK_STRING
import com.nudge.core.EXPANSTION_TRANSITION_DURATION
import com.nudge.core.TRANSITION
import com.nudge.core.customGridHeight
import com.nudge.core.getFirstAndLastInitials
import com.nudge.core.helper.TranslationHelper
import com.nudge.core.showCustomToast
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.CardArrow
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.brownDark
import com.nudge.core.ui.theme.buttonTextStyle
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_27_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_3_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.languageItemInActiveBorderBg
import com.nudge.core.ui.theme.lightGrayColor
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.questionTextStyle
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.nudge.core.ui.theme.yellowBg
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
import com.sarathi.missionactivitytask.ui.components.ShowDidiImageDialog
import com.sarathi.missionactivitytask.ui.grantTask.screen.TaskScreen
import com.sarathi.missionactivitytask.utils.StatusEnum
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.surveymanager.constants.DELIMITER_MULTISELECT_OPTIONS
import com.sarathi.surveymanager.ui.component.GridTypeComponent
import com.sarathi.surveymanager.ui.component.OptionCard
import com.sarathi.surveymanager.ui.component.RadioOptionTypeComponent
import com.sarathi.surveymanager.ui.component.TypeMultiSelectedDropDownComponent
import com.sarathi.surveymanager.ui.screen.getOptionsValueDto

@Composable
fun ActivitySelectTaskScreen(
    navController: NavController = rememberNavController(),
    viewModel: ActivitySelectTaskViewModel = hiltViewModel(),
    missionId: Int,
    activityName: String,
    activityId: Int,
    programId: Int,
    onSettingClick: () -> Unit
) {

    BackHandler {
        navController.popBackStack()
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
        programId = programId,
        navController = navController,
        taskScreenContent = { _, _ ->
            selectActivityTaskScreenContent(viewModel = viewModel)
        },
        taskScreenContentForGroup = { groupKey, _, _ ->
            selectActivityTaskScreenContentForGroup(groupKey, viewModel)
        },

        )

    LaunchedEffect(key1 = viewModel.taskUiList.value) {
        viewModel.onEvent(InitDataEvent.InitActivitySelectTaskScreenState(missionId, activityId))
    }
}

fun LazyListScope.selectActivityTaskScreenContent(viewModel: ActivitySelectTaskViewModel) {
    itemsIndexed(
        items = viewModel.filterList.value.entries.toList()
    ) { index, task ->
        ExpandableTaskCardRow(
            viewModel = viewModel,
            task = task,
            index = index,
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
    ) { index, task ->
        ExpandableTaskCardRow(
            viewModel = viewModel,
            task = task,
            index = index,
            groupKey = groupKey,
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
            style = questionTextStyle,
            color = blueDark
        )

    }
}


@SuppressLint("UnrememberedMutableState")
@Composable
fun ExpandableTaskCardRow(
    viewModel: ActivitySelectTaskViewModel,
    questionUIModel: QuestionUiModel?,
    index: Int,
    groupKey: String? = null,
    task: MutableMap.MutableEntry<Int, HashMap<String, TaskCardModel>>
) {
    ExpandableTaskCard(
        title = task.value[TaskCardSlots.TASK_TITLE.name],
        subTitle1 = task.value[TaskCardSlots.TASK_SUBTITLE.name],
        status = task.value[TaskCardSlots.TASK_STATUS.name],
        subtitle2 = task.value[TaskCardSlots.TASK_SUBTITLE_2.name],
        subtitle3 = task.value[TaskCardSlots.TASK_SUBTITLE_3.name],
        subtitle4 = task.value[TaskCardSlots.TASK_SUBTITLE_4.name],
        imagePath = viewModel.getFilePathUri(
            task.value[TaskCardSlots.TASK_IMAGE.name]?.value ?: BLANK_STRING
        ),
        modifier = Modifier,
        questionUiModel = questionUIModel,
        expanded = viewModel.expandedIds.contains(task.key),
        onExpendClick = { _, _ ->
            viewModel.onExpandClicked(task)
        },
        secondaryButtonTitle = task.value[TaskCardSlots.TASK_NOT_AVAILABLE_ENABLE.name]?.label
            ?: BLANK_STRING,
        isNotAvailableButtonEnable = task.value[TaskCardSlots.TASK_NOT_AVAILABLE_ENABLE.name]?.value.equals(
            "true"
        ) && !TextUtils.isEmpty(task.value[TaskCardSlots.TASK_NOT_AVAILABLE_ENABLE.name]?.label),
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
                viewModel.updateMissionFilter()
                viewModel.updateTaskAvailableStatus(
                    taskId = task.key,
                    status = SurveyStatusEnum.NOT_AVAILABLE.name
                )

                viewModel.checkIsActivityCompleted()
            }
        },
        isActivityCompleted = viewModel.isActivityCompleted.value,
        viewModel = viewModel,
        onAnswerSelection = { _, _ ->
            if (questionUIModel != null) {
                task.value[TaskCardSlots.TASK_STATUS.name] = TaskCardModel(
                    value = SurveyStatusEnum.COMPLETED.name,
                    label = BLANK_STRING,
                    icon = null
                )
                viewModel.saveSingleAnswerIntoDb(
                    currentQuestionUiModel = questionUIModel,
                    subjectType = viewModel.activityConfigUiModelWithoutSurvey?.subject.value(),
                    taskId = task.key
                )
                viewModel.updateMissionFilter()
                viewModel.updateTasStatus(
                    taskId = task.key,
                    status = SurveyStatusEnum.COMPLETED.name
                )
                viewModel.onExpandClicked(task)
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
    subtitle2: TaskCardModel?,
    subtitle3: TaskCardModel?,
    subtitle4: TaskCardModel?,
    imagePath: Uri?,
    modifier: Modifier,
    viewModel: ActivitySelectTaskViewModel,
    questionUiModel: QuestionUiModel?,
    expanded: Boolean,
    onExpendClick: (Boolean, TaskCardModel) -> Unit,
    secondaryButtonTitle: String = BLANK_STRING,
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
        if (it) textColorDark else greenOnline
    }

    BasicCardView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen_16_dp)
            .border(
                width = dimen_1_dp,
                color = if (taskStatus.value == StatusEnum.COMPLETED.name ||
                    taskStatus.value == StatusEnum.NOT_AVAILABLE.name
                ) greenOnline else lightGrayColor,
                shape = RoundedCornerShape(dimen_6_dp)
            )
            .background(Color.Transparent)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(white)
        ) {
            SelectActivityCard(
                title = title,
                subTitle1 = subTitle1,
                subtitle2 = subtitle2,
                subtitle3 = subtitle3,
                subtitle4 = subtitle4,
                imagePath = imagePath,
                taskStatus = taskStatus,
                expanded = expanded,
                onExpendClick = onExpendClick,
                animateColor = animateColor,
                viewModel = viewModel,
                translationHelper = viewModel.translationHelper,
                questionUiModel = questionUiModel,
                taskMarkedNotAvailable = taskMarkedNotAvailable,
                onAnswerSelection = onAnswerSelection,
                isActivityCompleted = isActivityCompleted,
                secondaryButtonTitle = secondaryButtonTitle,
                onNotAvailableClick = onNotAvailableClick,
                context = context,
                isNotAvailableButtonEnable = isNotAvailableButtonEnable
            )
        }
    }
}

@Composable
fun SelectActivityCard(
    title: TaskCardModel?,
    subTitle1: TaskCardModel?,
    subtitle2: TaskCardModel?,
    subtitle3: TaskCardModel?,
    subtitle4: TaskCardModel?,
    imagePath: Uri?,
    expanded: Boolean,
    onExpendClick: (Boolean, TaskCardModel) -> Unit,
    animateColor: Color,
    viewModel: ActivitySelectTaskViewModel,
    translationHelper: TranslationHelper,
    questionUiModel: QuestionUiModel?,
    taskMarkedNotAvailable: MutableState<Boolean>,
    onAnswerSelection: (optionValue: String, optionId: Int) -> Unit,
    isActivityCompleted: Boolean,
    secondaryButtonTitle: String,
    taskStatus: MutableState<String?>,
    onNotAvailableClick: () -> Unit,
    context: Context,
    isNotAvailableButtonEnable: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimen_16_dp)
                .padding(top = dimen_8_dp, bottom = dimen_5_dp),
            horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imagePath != null) {
                CircularImageViewComponent(
                    modifier = Modifier,
                    imagePath = imagePath,
                    onImageClick = {
                        viewModel.isDidiImageDialogVisible.value =
                            Triple(true, title?.value.value(), imagePath)
                    })
            } else if (title?.value != BLANK_STRING) {
                Box(
                    modifier = Modifier
                        .border(width = dimen_2_dp, shape = CircleShape, color = brownDark)
                        .clip(CircleShape)
                        .width(dimen_56_dp)
                        .height(dimen_56_dp)
                        .background(color = yellowBg)
                ) {
                    Text(
                        getFirstAndLastInitials(title?.value),
                        modifier = Modifier.align(Alignment.Center),
                        style = mediumTextStyle.copy(color = brownDark)
                    )
                }
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

            if (taskStatus.value != StatusEnum.NOT_STARTED.name) {
                CardArrow(
                    modifier = Modifier.size(if (expanded) dimen_20_dp else dimen_27_dp),
                    degrees = 0F,
                    iconColor = animateColor,
                    arrowIcon = if (!expanded)
                        R.drawable.ic_check_circle else R.drawable.icon_close,
                    onClick = {
                        if (expanded) {
                            title?.let { onExpendClick(expanded, it) }
                        }
                    }
                )
            }
        }


        if (subtitle2?.value?.isNotBlank() == true) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = dimen_16_dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubContainerView(subtitle2)
            }
        }
        if (subtitle3?.value?.isNotBlank() == true || subtitle4?.value?.isNotBlank() == true) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SubContainerView(subtitle3)
                SubContainerView(subtitle4, isNumberFormattingRequired = true)
            }
        }
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (expanded) {
            OptionsUI(
                referenceId = viewModel.activityConfigUiModelWithoutSurvey?.referenceId,
                translationHelper = translationHelper,
                questionUiModel = questionUiModel,
                taskMarkedNotAvailable = taskMarkedNotAvailable,
                onAnswerSelection = onAnswerSelection,
                isActivityCompleted = isActivityCompleted,
                taskStatus = taskStatus,
                onNotAvailableClick = onNotAvailableClick,
                secondaryButtonTitle = secondaryButtonTitle,
                context = context,
                isNotAvailableButtonEnable = isNotAvailableButtonEnable
            )
        } else {
            DisplaySelectedOption(translationHelper, questionUiModel, taskStatus.value) {
                title?.let { onExpendClick(expanded, it) }
            }
        }
    }
}

@Composable
fun DisplaySelectedOption(
    translationHelper: TranslationHelper,
    questionUiModel: QuestionUiModel?,
    taskStatus: String?,
    onViewClick: () -> Unit
) {
    var options = BLANK_STRING
    options = if (options.isNullOrEmpty() && taskStatus == StatusEnum.NOT_AVAILABLE.name) {
        translationHelper.stringResource(R.string.not_available)
    } else {
        questionUiModel?.options?.filter { it.isSelected == true }?.map { it.description }
            ?.joinToString(", ").toString()
    }
    if (!options.isNullOrEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimen_0_dp,
                    end = dimen_16_dp,
                    top = dimen_10_dp
                )
                .padding(horizontal = dimen_16_dp),
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
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (taskStatus == StatusEnum.COMPLETED.name) {
                Row(modifier = Modifier.clickable {
                    onViewClick()
                }) {
                    Text(
                        text = translationHelper.stringResource(
                            R.string.task_view
                        ),
                        modifier = Modifier
                            .padding(horizontal = dimen_5_dp)
                            .absolutePadding(bottom = 3.dp),
                        color = blueDark,
                        style = newMediumTextStyle,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "",
                        tint = blueDark,
                    )
                }
            } else {
                CustomVerticalSpacer()
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun OptionsUI(
    referenceId: Int?,
    translationHelper: TranslationHelper,
    questionUiModel: QuestionUiModel?,
    taskMarkedNotAvailable: MutableState<Boolean>,
    onAnswerSelection: (optionValue: String, optionId: Int) -> Unit,
    isActivityCompleted: Boolean,
    secondaryButtonTitle: String,
    taskStatus: MutableState<String?>,
    onNotAvailableClick: () -> Unit,
    context: Context,
    isNotAvailableButtonEnable: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        questionUiModel?.options?.sortedBy { it.order }?.let {
            when (questionUiModel.type.toLowerCase()) {
                QuestionType.RadioButton.name.toLowerCase(),
                QuestionType.Toggle.name.toLowerCase() -> {
                    val selectedValue =
                        it.find { it.isSelected == true }?.description ?: BLANK_STRING
                    RadioOptionTypeComponent(
                        optionItemEntityState = it,
                        isTaskMarkedNotAvailable = taskMarkedNotAvailable,
                        selectedValue = selectedValue,
                        isIconRequired = !it.firstOrNull()?.optionImage.isNullOrEmpty(),
                        isActivityCompleted = isActivityCompleted,
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

                QuestionType.SingleSelectGrid.name.toLowerCase() -> {
                    GridTypeComponent(
                        questionDisplay = questionUiModel.questionDisplay,
                        optionUiModelList = it,
                        questionIndex = 0,
                        areOptionsEnabled = !isActivityCompleted,
                        maxCustomHeight = customGridHeight(it.size),
                        isQuestionDisplay = false,
                        showCardView = false,
                        isTaskMarkedNotAvailable = taskMarkedNotAvailable,
                        isEditAllowed = !isActivityCompleted,
                        onAnswerSelection = { selectedOptionIndex, isSelected ->
                            if (!isActivityCompleted) {
                                questionUiModel.options?.let { options ->
                                    options.forEach {
                                        it.isSelected = false
                                        it.selectedValue = BLANK_STRING
                                    }
                                    options[selectedOptionIndex].isSelected = true
                                    taskMarkedNotAvailable.value = false
                                    onAnswerSelection(BLANK_STRING, selectedOptionIndex)
                                }
                            } else {
                                showCustomToast(
                                    context,
                                    translationHelper.getString(
                                        com.sarathi.surveymanager.R.string.activity_completed_unable_to_edit
                                    )
                                )
                            }
                        }, questionDetailExpanded = {}
                    )
                }

                QuestionType.MultiSelect.name.toLowerCase() -> {
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
                            } else {
                                showCustomToast(
                                    context,
                                    translationHelper.getString(
                                        com.sarathi.surveymanager.R.string.activity_completed_unable_to_edit
                                    )
                                )
                            }
                        }, questionDetailExpanded = {}
                    )
                }

                QuestionType.MultiSelectDropDown.name.lowercase() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimen_10_dp, vertical = dimen_10_dp)
                    ) {
                        TypeMultiSelectedDropDownComponent(
                            content = questionUiModel.contentEntities,
                            questionIndex = 0,
                            title = BLANK_STRING,
                            isMandatory = questionUiModel.isMandatory,
                            sources = getOptionsValueDto(questionUiModel.options ?: listOf()),
                            isEditAllowed = !isActivityCompleted,
                            maxCustomHeight = dimen_60_dp,
                            showCardView = false,
                            isQuestionNumberVisible = false,
                            optionStateMap = questionUiModel.options?.map {
                                Pair(
                                    it.optionId!!,
                                    true
                                )
                            }?.toMap() ?: emptyMap(),
                            showSearchBar = true,
                            navigateToMediaPlayerScreen = {},
                            onAnswerSelection = { selectedItems ->
                                if (!isActivityCompleted) {
                                    val selectedOptions =
                                        selectedItems.split(DELIMITER_MULTISELECT_OPTIONS)
                                    questionUiModel.options?.forEach { options ->
                                        if (selectedOptions.find { it == options.description.toString() } != null) {
                                            options.isSelected = true
                                        } else {
                                            options.isSelected = false
                                        }
                                    }
                                    onAnswerSelection(
                                        selectedItems,
                                        questionUiModel.options?.firstOrNull()?.optionId.value()
                                    )
                                } else {
                                    showCustomToast(
                                        context,
                                        translationHelper.getString(
                                            com.sarathi.surveymanager.R.string.activity_completed_unable_to_edit
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
            }

        }
    }
    CustomVerticalSpacer()
    if (isNotAvailableButtonEnable && !TextUtils.isEmpty(secondaryButtonTitle)) {
        Divider(
            Modifier
                .fillMaxWidth()
                .height(dimen_1_dp),
            color = languageItemInActiveBorderBg.copy(alpha = 0.30f)
        )
        CustomVerticalSpacer()
        NotAvailableUI(
            translationHelper = translationHelper,
            isNotAvailableButtonEnable = isNotAvailableButtonEnable,
            taskMarkedNotAvailable = taskMarkedNotAvailable,
            isActivityCompleted = isActivityCompleted,
            secondaryButtonTitle = secondaryButtonTitle,
            taskStatus = taskStatus,
            onNotAvailableClick = onNotAvailableClick,
            context = context
        )

        CustomVerticalSpacer()
    }
}

@Composable
private fun NotAvailableUI(
    translationHelper: TranslationHelper,
    isNotAvailableButtonEnable: Boolean,
    taskMarkedNotAvailable: MutableState<Boolean>,
    isActivityCompleted: Boolean,
    secondaryButtonTitle: String,
    taskStatus: MutableState<String?>,
    onNotAvailableClick: () -> Unit,
    context: Context
) {
    val context = LocalContext.current
    if (isNotAvailableButtonEnable) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimen_16_dp)
        ) {
            OptionCard(
                modifier = Modifier
                    .fillMaxWidth(),
                backgroundColor = if (!taskMarkedNotAvailable.value
                ) Color.Transparent else blueDark, textColor = if (taskMarkedNotAvailable.value
                ) white else blueDark,
                borderColor = if (!taskMarkedNotAvailable.value
                ) languageItemInActiveBorderBg else blueDark,
                optionText = secondaryButtonTitle,
                isNotAvailableOption = true
            ) {
                if (!isActivityCompleted) {
                    taskMarkedNotAvailable.value = true
                    taskStatus.value = SurveyStatusEnum.NOT_AVAILABLE.name
                    onNotAvailableClick()
                } else {
                    showCustomToast(
                        context,
                        translationHelper.getString(
                            R.string.activity_completed_unable_to_edit
                        )
                    )
                }
            }
        }
    }
}

