package com.sarathi.missionactivitytask.ui.grantTask.screen

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.isOnline
import com.nudge.core.showCustomToast
import com.nudge.core.ui.commonUi.CustomLinearProgressIndicator
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.rememberCustomProgressState
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_50_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dimen_72_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.white
import com.nudge.core.value
import com.sarathi.contentmodule.ui.content_screen.screen.BaseContentScreen
import com.sarathi.contentmodule.utils.event.SearchEvent
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardModel
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardSlots
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.navigation.navigateToActivityCompletionScreen
import com.sarathi.missionactivitytask.navigation.navigateToContentDetailScreen
import com.sarathi.missionactivitytask.navigation.navigateToLivelihoodDropDownScreen
import com.sarathi.missionactivitytask.navigation.navigateToMediaPlayerScreen
import com.sarathi.missionactivitytask.ui.basic_content.component.LivelihoodTaskCard
import com.sarathi.missionactivitytask.ui.components.LinearProgressBarComponent
import com.sarathi.missionactivitytask.ui.components.SearchWithFilterViewComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.LivelihoodTaskScreenViewModel
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.TaskScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.surveymanager.ui.component.ButtonPositive

@Composable
fun LivelihoodTaskScreen(
    navController: NavController = rememberNavController(),
    viewModel: LivelihoodTaskScreenViewModel = hiltViewModel(),
    missionId: Int,
    activityName: String,
    activityId: Int,
    totalCount:Int,
    pendingCount:Int,
    onSettingClick: () -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.onEvent(InitDataEvent.InitLivelihoodPlanningScreenState(missionId, activityId))
    }
    LivelihoodPlaningTaskScreen(
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
        pendingCount = pendingCount,
        totalCount =totalCount,
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
    val context = LocalContext.current
    LivelihoodTaskCard(
        onPrimaryButtonClick = { subjectName ->
            viewModel.activityConfigUiModelWithoutSurvey?.let {
                when (ActivityTypeEnum.getActivityTypeFromId(it.activityTypeId)) {
                    ActivityTypeEnum.LIVELIHOOD -> {
                        if (!viewModel.isActivityCompleted.value) {
                            navigateToLivelihoodDropDownScreen(
                                navController,
                                taskId = task.key,
                                activityId = viewModel.activityId,
                                missionId = viewModel.missionId,
                                subjectName = subjectName
                            )
                        } else {
                            showCustomToast(
                                context,
                                context.getString(R.string.activity_completed_unable_to_edit)
                            )
                        }
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
        subtitle5 = task.value[TaskCardSlots.TASK_SUBTITLE_5.name]?.copy(value = viewModel.livelihoodsEntityList.find { it.livelihoodId==viewModel.subjectLivelihoodMappingMap.get(viewModel.taskUiModel?.find { it.taskId==task.key }?.subjectId)?.primaryLivelihoodId.value() }?.name.value()),
        subtitle7 = task.value[TaskCardSlots.TASK_SUBTITLE_6.name]?.copy(value = viewModel.livelihoodsEntityList.find { it.livelihoodId==viewModel.subjectLivelihoodMappingMap.get(viewModel.taskUiModel?.find { it.taskId==task.key }?.subjectId)?.secondaryLivelihoodId.value() }?.name.value()) ,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LivelihoodPlaningTaskScreen(
    navController: NavController,
    viewModel: TaskScreenViewModel,
    missionId: Int,
    activityName: String,
    activityId: Int,
    secondaryButtonText: String,
    isSecondaryButtonEnable: Boolean = false,
    onSecondaryButtonClick: () -> Unit,
    isSecondaryButtonVisible: Boolean = false,
    isProgressBarVisible: Boolean = false,
    taskList: List<TaskUiModel>? = null,
    onSettingClick: () -> Unit,
    totalCount:Int?=0,
    pendingCount:Int?=0,
    taskScreenContent: LazyListScope.(viewModel: TaskScreenViewModel, navController: NavController) -> Unit
) {
    val context = LocalContext.current
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

    val linearProgressState = rememberCustomProgressState()

    LaunchedEffect(taskList?.size) {
        viewModel.setMissionActivityId(missionId, activityId)
        viewModel.onEvent(InitDataEvent.InitTaskScreenState(taskList))
    }

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
                            .padding(start = dimen_8_dp, end = dimen_8_dp, bottom = dimen_10_dp)
                    ) {
                        SearchWithFilterViewComponent(
                            placeholderString = viewModel.searchLabel.value,
                            filterSelected = viewModel.isGroupByEnable.value,
                            modifier = Modifier.padding(horizontal = 10.dp),
                            showFilter = viewModel.isFilterEnable.value,
                            onFilterSelected = {
                                if (viewModel.filterList.value.isNotEmpty()) {
                                    viewModel.isGroupByEnable.value = !it
                                }
                            },
                            onSearchValueChange = { queryTerm ->
                                viewModel.onEvent(
                                    SearchEvent.PerformSearch(
                                        queryTerm,
                                        viewModel.isGroupByEnable.value,
                                        BLANK_STRING
                                    )
                                )
                            })
                        Row(verticalAlignment = Alignment.CenterVertically
                        ) {
                            LinearProgressBarComponent( modifier = Modifier
                                .weight(1f)
                                .padding( 10.dp)
                                .clip(RoundedCornerShape(14.dp)),
                                progress = (pendingCount?.toFloat()?.div(totalCount!!)
                                    ?: 0) as Float,
                            )
                            Spacer(modifier = Modifier.width(dimen_5_dp))

                            Text(
                                text = "$pendingCount / $totalCount ",
                                style = smallTextStyle.copy(color = blueDark),
                            )
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
                        if (/*viewModel.isProgressEnable.value*/false) {
                            item {
                                CustomLinearProgressIndicator(
                                    modifier = Modifier
                                        .padding(dimen_10_dp),
                                    progressState = linearProgressState
                                )
                            }
                        }
                        if (viewModel.isFilterEnable.value && viewModel.isGroupByEnable.value) {
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

                                        androidx.compose.material.Text(
                                            text = category ?: BLANK_STRING,
                                            style = defaultTextStyle.copy(color = blueDark)
                                        )
                                    }
                                }
                                item {
                                    CustomVerticalSpacer()
                                }

                                taskScreenContent(viewModel, navController)
                            }

                        } else {
                            if (viewModel.filterList.value.isNotEmpty() && !viewModel.loaderState.value.isLoaderVisible) {

                                taskScreenContent(viewModel, navController)

                            }
                        }
                    }
                }
            }
        },
        onSettingClick = onSettingClick
    )
}


