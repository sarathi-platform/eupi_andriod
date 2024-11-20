package com.sarathi.missionactivitytask.ui.grantTask.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ID
import com.nudge.core.FILTER_BY_SMALL_GROUP_LABEL
import com.nudge.core.FILTER_BY_VILLAGE_NAME_LABEL
import com.nudge.core.FilterCore
import com.nudge.core.NO_FILTER_VALUE
import com.nudge.core.NO_SG_FILTER_LABEL
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.enums.SurveyFlow
import com.nudge.core.isOnline
import com.nudge.core.ui.commonUi.BottomSheetScaffoldComponent
import com.nudge.core.ui.commonUi.CustomIconButton
import com.nudge.core.ui.commonUi.CustomLinearProgressIndicator
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.SimpleSearchComponent
import com.nudge.core.ui.commonUi.customVerticalSpacer
import com.nudge.core.ui.commonUi.rememberCustomBottomSheetScaffoldProperties
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_16_sp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_50_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dimen_72_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.unmatchedOrangeColor
import com.nudge.core.ui.theme.white
import com.nudge.core.utils.CoreLogger
import com.nudge.core.value
import com.sarathi.contentmodule.ui.content_screen.screen.BaseContentScreen
import com.sarathi.dataloadingmangement.R
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardModel
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardSlots
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.missionactivitytask.navigation.navigateToActivityCompletionScreen
import com.sarathi.missionactivitytask.navigation.navigateToContentDetailScreen
import com.sarathi.missionactivitytask.navigation.navigateToGrantSurveySummaryScreen
import com.sarathi.missionactivitytask.navigation.navigateToLivelihoodDropDownScreen
import com.sarathi.missionactivitytask.navigation.navigateToMediaPlayerScreen
import com.sarathi.missionactivitytask.navigation.navigateToSectionScreen
import com.sarathi.missionactivitytask.ui.activities.select.CustomTextView
import com.sarathi.missionactivitytask.ui.basic_content.component.TaskCard
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.TaskScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.SearchEvent
import com.sarathi.missionactivitytask.utils.event.TaskScreenEvent
import com.sarathi.surveymanager.ui.component.ButtonPositive
import com.sarathi.surveymanager.ui.component.ShowCustomDialog
import com.sarathi.surveymanager.ui.description_component.presentation.ModelBottomSheetDescriptionContentComponent
import com.sarathi.surveymanager.ui.htmltext.HtmlText
import kotlinx.coroutines.launch
import com.nudge.core.R as CoreRes

const val TAG = "TaskScreen"

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun TaskScreen(
    navController: NavController,
    viewModel: TaskScreenViewModel,
    programId: Int,
    missionId: Int,
    activityName: String,
    activityId: Int,
    secondaryButtonText: String,
    isSecondaryButtonEnable: Boolean = false,
    onSecondaryButtonClick: () -> Unit,
    isSecondaryButtonVisible: Boolean = false,
    taskList: List<TaskUiModel>? = null,
    onSettingClick: () -> Unit,
    taskScreenContent: LazyListScope.(viewModel: TaskScreenViewModel, navController: NavController) -> Unit,
    taskScreenContentForGroup: LazyListScope.(groupKey: String, viewModel: TaskScreenViewModel, navController: NavController) -> Unit
) {
    val context = LocalContext.current
    val scaffoldState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = false)
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        viewModel.loaderState.value.isLoaderVisible,
        {
            if (isOnline(context)) {
                viewModel.refreshData()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.refresh_failed_please_try_again),
                    Toast.LENGTH_LONG
                ).show()
            }

        })

    val coroutineScope = rememberCoroutineScope()

    val customBottomSheetScaffoldProperties = rememberCustomBottomSheetScaffoldProperties()

    val focusManager = LocalFocusManager.current

    LaunchedEffect(taskList?.size) {
        viewModel.setMissionActivityId(missionId, activityId, programId)
        viewModel.onEvent(InitDataEvent.InitTaskScreenState(taskList))
    }

    LaunchedEffect(viewModel.isButtonEnable.value) {
        if (viewModel.isButtonEnable.value) {
            scaffoldState.show()
        }
    }


    BottomSheetScaffoldComponent(
        bottomSheetScaffoldProperties = customBottomSheetScaffoldProperties,
        defaultValue = getDefaultValueFor(context, viewModel.filterLabel),
        headerTitle = getFilterLabel(context, viewModel.filterLabel),
        bottomSheetContentItemList = viewModel.filterByList,
        selectedIndex = FilterCore.getFilterValueForActivity(activityId),
        onBottomSheetItemSelected = {
            viewModel.onEvent(TaskScreenEvent.OnFilterSelected(it))
        }
    ) {
        ModelBottomSheetDescriptionContentComponent(
            modifier = Modifier
                .fillMaxSize(),
            sheetContent = {
                Column(
                    modifier = Modifier.padding(dimen_10_dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1f),
                            text = stringResource(R.string.since_you_have_completed_all_the_tasks_please_complete_the_activity),
                            style = newMediumTextStyle.copy(color = blueDark)
                        )
                        IconButton(onClick = {
                            scope.launch {
                                scaffoldState.hide()
                            }
                        }, modifier = Modifier.size(48.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_close),
                                contentDescription = "Close",
                                tint = blueDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = stringResource(R.string.on_completing_the_activity_you_will_not_be_able_to_edit_the_details),
                        style = newMediumTextStyle.copy(color = unmatchedOrangeColor)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimen_10_dp),
                    ) {
                        ButtonPositive(
                            modifier = Modifier.weight(0.5f),
                            buttonTitle = stringResource(R.string.complete_activity),
                            isActive = viewModel.isButtonEnable.value,
                            isArrowRequired = false,
                            onClick = {
                                scope.launch {
                                    scaffoldState.hide()
                                }
                                viewModel.markActivityCompleteStatus()
                                navigateToActivityCompletionScreen(
                                    isFromActivity = true,
                                    navController = navController,
                                    activityMsg = context.getString(
                                        R.string.activity_completion_message,
                                        activityName
                                    ),
                                    activityRoutePath = activityName
                                )
                            }
                        )
                    }
                }
            },
            sheetState = scaffoldState,
            sheetElevation = 20.dp,
            sheetBackgroundColor = Color.White,
            sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        ) {
            ToolBarWithMenuComponent(
                title = activityName,
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                onBackIconClick = { navController.popBackStack() },
                isSearch = true,
                onSearchValueChange = { queryTerm ->

                },
                onRetry = {},
                onBottomUI = {
                    BottomAppBar(
                        modifier = Modifier.height(dimen_72_dp),
                        backgroundColor = white
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = dimen_10_dp),
                        ) {
                            ButtonPositive(
                                modifier = Modifier.weight(0.5f),
                                buttonTitle = stringResource(R.string.complete_activity),
                                isActive = viewModel.isButtonEnable.value,
                                isArrowRequired = false,
                                onClick = {
                                    viewModel.showDialog.value = true
                                })

                            if (isSecondaryButtonVisible) {
                                Spacer(modifier = Modifier.width(10.dp))
                                ButtonPositive(
                                    modifier = Modifier.weight(0.5f),
                                    buttonTitle = secondaryButtonText,
                                    isActive = isSecondaryButtonEnable,
                                    isArrowRequired = false,
                                    onClick = onSecondaryButtonClick
                                )
                            }
                        }
                    }
                },
                onContentUI = { paddingValues, isSearch, onSearchValueChanged ->
                    Column {
                        BaseContentScreen(
                            matId = viewModel.matId.value,
                            contentScreenCategory = viewModel.contentCategory.value
                        ) { contentValue, contentKey, contentType, isLimitContentData, contentTitle ->
                            if (!isLimitContentData) {
                                navigateToMediaPlayerScreen(
                                    navController = navController,
                                    contentKey = contentKey,
                                    contentType = contentType,
                                    contentTitle = contentTitle,
                                )
                            } else {
                                navigateToContentDetailScreen(
                                    navController,
                                    matId = viewModel.matId.value,
                                    contentScreenCategory = viewModel.contentCategory.value
                                )
                            }
                        }
                        if (isSearch) {

                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = dimen_8_dp,
                                        end = dimen_8_dp,
                                        bottom = dimen_10_dp
                                    )
                            ) {

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(dimen_8_dp)
                                ) {

                                    SimpleSearchComponent(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        placeholderString = viewModel.searchLabel.value,
                                        searchFieldHeight = dimen_50_dp,
                                        onSearchValueChange = { queryTerm ->
                                            viewModel.onEvent(
                                                SearchEvent.PerformSearch(
                                                    queryTerm,
                                                    viewModel.isGroupingApplied.value,
                                                    viewModel.isFilterApplied.value
                                                )
                                            )
                                        }
                                    )

                                    if (viewModel.isFilterEnabled.value) {
                                        CustomIconButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    customBottomSheetScaffoldProperties.sheetState.show()
                                                }
                                            },
                                            icon = painterResource(id = if (viewModel.isFilterApplied.value) com.nudge.core.R.drawable.filter_active_icon else com.nudge.core.R.drawable.filter_icon),
                                            iconTintColor = if (viewModel.isFilterApplied.value) white else blueDark,
                                            contentDescription = "filter_list",
                                            buttonContainerColor = if (viewModel.isFilterApplied.value) blueDark else Color.Transparent,
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = if (viewModel.isFilterApplied.value) blueDark else Color.Transparent,
                                                contentColor = if (viewModel.isFilterApplied.value) white else blueDark
                                            )
                                        )
                                    }

                                    if (viewModel.isGroupByEnable.value) {
                                        CustomIconButton(
                                            onClick = {
                                                if (viewModel.filterList.value.isNotEmpty()) {
                                                    viewModel.onEvent(TaskScreenEvent.OnGroupBySelected)
                                                }
                                            },
                                            icon = painterResource(id = if (viewModel.isGroupingApplied.value) com.nudge.core.R.drawable.ic_group_by_active_icon else com.nudge.core.R.drawable.ic_group_by_icon),
                                            iconTintColor = if (viewModel.isGroupingApplied.value) white else blueDark,
                                            contentDescription = "filter_list",
                                            buttonContainerColor = if (viewModel.isGroupingApplied.value) blueDark else Color.Transparent,
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = if (viewModel.isGroupingApplied.value) blueDark else Color.Transparent,
                                                contentColor = if (viewModel.isGroupingApplied.value) white else blueDark
                                            )
                                        )
                                    }
                                }
                            }
                        }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(pullRefreshState)
                    ) {
                        PullRefreshIndicator(
                            refreshing = viewModel.loaderState.value.isLoaderVisible,
                            state = pullRefreshState,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .zIndex(1f),
                            contentColor = blueDark,
                        )
                        Spacer(modifier = Modifier.height(dimen_10_dp))
                        LazyColumn(modifier = Modifier.padding(bottom = dimen_50_dp)) {

                            stickyHeader {
                                if (viewModel.isProgressEnable.value) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(white)
                                    ) {
                                        CustomLinearProgressIndicator(
                                            modifier = Modifier
                                                .padding(dimen_10_dp)
                                                .padding(horizontal = dimen_6_dp),
                                            progressState = viewModel.progressState,
                                            color = greenOnline
                                        )
                                    }
                                }

                                if (ActivityTypeEnum.showSurveyQuestionOnTaskScreen(viewModel.activityType)) {
                                    if (viewModel.filterList.value.isNotEmpty() && viewModel.questionUiModel.value.isNotEmpty()) {
                                        viewModel.filterList.value.keys.let {
                                            val questionTitle =
                                                viewModel.questionUiModel.value[it.first()]?.questionDisplay.value()
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(white)
                                            ) {
                                                CustomTextView(title = questionTitle)
                                            }
                                        }
                                    }
                                }
                            }

                            if (viewModel.isFilterApplied.value) {
                                customVerticalSpacer()
                                item {
                                    HtmlText(
                                        text = getFilterAppliedText(context, viewModel),
                                        modifier = Modifier.padding(horizontal = dimen_16_dp),
                                        style = defaultTextStyle,
                                        fontSize = dimen_16_sp
                                    )
                                }
                                customVerticalSpacer()
                            }

                                if (viewModel.isGroupingApplied.value && viewModel.isGroupByEnable.value) {
                                    viewModel.filterTaskMap.forEach { (category, itemsInCategory) ->
                                        item {
                                            Row(
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = dimen_6_dp)
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.ic_vo_name_icon),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .padding(horizontal = dimen_10_dp)
                                                        .size(25.dp),
                                                    colorFilter = ColorFilter.tint(blueDark)
                                                )

                                                Text(
                                                    text = category ?: BLANK_STRING,
                                                    style = defaultTextStyle.copy(color = blueDark)
                                                )
                                            }
                                        }
                                        item {
                                            CustomVerticalSpacer()
                                        }

                                        if (category != null) {
                                            taskScreenContentForGroup(
                                                category,
                                                viewModel,
                                                navController
                                            )
                                        }
                                    }

                                } else {
                                    if (viewModel.filterList.value.isNotEmpty() && !viewModel.loaderState.value.isLoaderVisible) {
                                        taskScreenContent(viewModel, navController)

                                    }
                                }
                            }
                        }
                    }
                    if (viewModel.showDialog.value) {
                        ShowCustomDialog(
                            message = stringResource(R.string.not_be_able_to_make_changes_after_completing_this_activity),
                            negativeButtonTitle = stringResource(com.sarathi.surveymanager.R.string.cancel),
                            positiveButtonTitle = stringResource(com.sarathi.surveymanager.R.string.ok),
                            onNegativeButtonClick = {
                                viewModel.showDialog.value = false
                            },
                            onPositiveButtonClick = {
                                viewModel.markActivityCompleteStatus()

                        navigateToActivityCompletionScreen(
                            isFromActivity = true,
                            navController = navController,
                            activityMsg = context.getString(
                                R.string.activity_completion_message,
                                activityName
                            ),
                            activityRoutePath = viewModel.activityConfigUiModelWithoutSurvey?.activityType.value()
                        )
                        viewModel.showDialog.value = false
                    }
                )
            }
        },
        onSettingClick = onSettingClick
    )
}


    }


}

fun getDefaultValueFor(context: Context, filterLabel: String): String {
    var result = BLANK_STRING
    result = when (filterLabel) {
        FILTER_BY_SMALL_GROUP_LABEL -> context?.getString(R.string.no_small_group_assgned_label)
            .value()

        else -> BLANK_STRING
    }
    return result

}

@Composable
private fun getFilterAppliedText(context: Context?, viewModel: TaskScreenViewModel): String {

    val count = if (viewModel.isGroupingApplied.value) {
        var size = 0
        viewModel.filterTaskMap.forEach {
            size += it.value.size
        }
        size.toString()
    } else {
        viewModel.filterList.value.size.toString()
    }
    val filterByKey = viewModel.getFilterByValueKeyWithoutLabel(context, viewModel.filterLabel)
    val filterValue = if (filterByKey.equals(
            NO_FILTER_VALUE,
            true
        )
    ) NO_SG_FILTER_LABEL else filterByKey


    return stringResource(id = R.string.filter_item_count_label, count, filterValue)
}

fun LazyListScope.TaskScreenContent(
    viewModel: TaskScreenViewModel,
    navController: NavController
) {

    itemsIndexed(
        items = viewModel.filterList.value.entries.toList()
    ) { _, task ->

        TaskRowView(viewModel, navController, task)

        CustomVerticalSpacer()
    }
    item {
        CustomVerticalSpacer(size = dimen_20_dp)
    }

}

fun LazyListScope.TaskScreenContentForGroup(
    groupKey: String,
    viewModel: TaskScreenViewModel,
    navController: NavController
) {
    itemsIndexed(
        items = viewModel.filterTaskMap[groupKey].value()
    ) { _, task ->

        TaskRowView(viewModel, navController, task)

        CustomVerticalSpacer()
    }
    item {
        CustomVerticalSpacer(size = dimen_20_dp)
    }
}

@Composable
fun TaskRowView(
    viewModel: TaskScreenViewModel,
    navController: NavController,
    task: MutableMap.MutableEntry<Int, HashMap<String, TaskCardModel>>,
) {
    TaskCard(
        onPrimaryButtonClick = { subjectName ->
            viewModel.activityConfigUiModelWithoutSurvey?.let {

                when (SurveyFlow.getSurveyFlowFromTaskScreenForActivityType(it.activityTypeId)) {

                    SurveyFlow.GrantSurveySummaryScreen -> {
                        viewModel.activityConfigUiModel?.let {
                            if (subjectName.isNotBlank()) {
                                navigateToGrantSurveySummaryScreen(
                                    navController,
                                    taskId = task.key,
                                    surveyId = it.surveyId,
                                    sectionId = it.sectionId,
                                    subjectType = it.subject,
                                    subjectName = subjectName,
                                    activityConfigId = it.activityConfigId,
                                    sanctionedAmount = task.value[TaskCardSlots.TASK_SUBTITLE_4.name]?.value?.toInt()
                                        ?: DEFAULT_ID,
                                )
                            }
                        }
                    }

                    SurveyFlow.LivelihoodPlanningScreen -> {
                        navigateToLivelihoodDropDownScreen(
                            navController,
                            taskId = task.key,
                            activityId = viewModel.activityId,
                            missionId = viewModel.missionId,
                            subjectName = subjectName
                        )
                    }

                    else -> {
                        viewModel.activityConfigUiModel?.let {
                            if (subjectName.isNotBlank()) {
                                val sanctionedAmount = try {
                                    task.value[TaskCardSlots.TASK_SUBTITLE_4.name]?.value?.toInt()
                                        ?: DEFAULT_ID
                                } catch (ex: Exception) {
                                    CoreLogger.e(
                                        tag = TAG,
                                        msg = "TaskRowView: exception -> ${ex.message}",
                                        ex = ex,
                                        stackTrace = true
                                    )
                                    DEFAULT_ID
                                }
                                navigateToSectionScreen(
                                    navController,
                                    missionId = viewModel.missionId,
                                    activityId = viewModel.activityId,
                                    taskId = task.key,
                                    surveyId = it.surveyId,
                                    subjectType = it.subject,
                                    subjectName = subjectName,
                                    activityType = viewModel.activityType,
                                    activityConfigId = it.activityConfigId,
                                    sanctionedAmount = sanctionedAmount,
                                )
                            }
                        }
                    }

                }

                /*when (ActivityTypeEnum.getActivityTypeFromId(it.activityTypeId)) {
                    ActivityTypeEnum.GRANT -> {
                        viewModel.activityConfigUiModel?.let {
                            if (subjectName.isNotBlank()) {
                                navigateToGrantSurveySummaryScreen(
                                    navController,
                                    taskId = task.key,
                                    surveyId = it.surveyId,
                                    sectionId = it.sectionId,
                                    subjectType = it.subject,
                                    subjectName = subjectName,
                                    activityConfigId = it.activityConfigId,
                                    sanctionedAmount = task.value[TaskCardSlots.TASK_SUBTITLE_4.name]?.value?.toInt()
                                        ?: DEFAULT_ID,
                                )
                            }
                        }
                    }

                    ActivityTypeEnum.LIVELIHOOD -> {
                        navigateToLivelihoodDropDownScreen(
                            navController,
                            taskId = task.key,
                            activityId = viewModel.activityId,
                            missionId = viewModel.missionId,
                            subjectName = subjectName
                        )
                    }

                    else -> {
                        viewModel.activityConfigUiModel?.let {
                            if (subjectName.isNotBlank()) {
                                val sanctionedAmount = try {
                                    task.value[TaskCardSlots.TASK_SUBTITLE_4.name]?.value?.toInt()
                                        ?: DEFAULT_ID
                                } catch (ex: Exception) {
                                    CoreLogger.e(
                                        tag = TAG,
                                        msg = "TaskRowView: exception -> ${ex.message}",
                                        ex = ex,
                                        stackTrace = true
                                    )
                                    DEFAULT_ID
                                }
                                navigateToSectionScreen(
                                    navController,
                                    missionId = viewModel.missionId,
                                    activityId = viewModel.activityId,
                                    taskId = task.key,
                                    surveyId = it.surveyId,
                                    subjectType = it.subject,
                                    subjectName = subjectName,
                                    activityType = viewModel.activityType,
                                    activityConfigId = it.activityConfigId,
                                    sanctionedAmount = sanctionedAmount,
                                )
                            }
                        }
                    }
                }*/
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
        subtitle5 = task.value[TaskCardSlots.TASK_SUBTITLE_5.name],
        subtitle7 = task.value[TaskCardSlots.TASK_SUBTITLE_6.name],
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

fun getFilterLabel(context: Context?, filterLabel: String?): String {
    var result = BLANK_STRING
    result = when (filterLabel) {
        FILTER_BY_SMALL_GROUP_LABEL -> context?.getString(CoreRes.string.small_group_filter_label)
            .value()

        FILTER_BY_VILLAGE_NAME_LABEL -> context?.getString(CoreRes.string.village_filter_label)
            .value()

        else -> BLANK_STRING
    }
    return result
}