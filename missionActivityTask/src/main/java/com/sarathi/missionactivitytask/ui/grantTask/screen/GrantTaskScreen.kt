package com.sarathi.missionactivitytask.ui.grantTask.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ID
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.sarathi.contentmodule.ui.content_screen.screen.BaseContentScreen
import com.sarathi.contentmodule.utils.event.SearchEvent
import com.sarathi.dataloadingmangement.model.uiModel.ContentCategoryEnum
import com.sarathi.dataloadingmangement.model.uiModel.GrantTaskCardSlots
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.navigation.navigateToContentDetailScreen
import com.sarathi.missionactivitytask.navigation.navigateToDisbursmentSummaryScreen
import com.sarathi.missionactivitytask.navigation.navigateToGrantSurveySummaryScreen
import com.sarathi.missionactivitytask.navigation.navigateToMediaPlayerScreen
import com.sarathi.missionactivitytask.ui.basic_content.component.GrantTaskCard
import com.sarathi.missionactivitytask.ui.components.SearchWithFilterViewComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.grantTask.model.GrantTaskCardModel
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.GrantTaskScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.surveymanager.ui.component.ButtonPositive

@Composable
fun GrantTaskScreen(
    navController: NavController = rememberNavController(),
    viewModel: GrantTaskScreenViewModel = hiltViewModel(),
    missionId: Int,
    activityName: String,
    activityId: Int,
    onSettingClick: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.setMissionActivityId(missionId, activityId)
        viewModel.onEvent(InitDataEvent.InitDataState)
    }
    ToolBarWithMenuComponent(
        title = activityName,
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = true,
        onSearchValueChange = { queryTerm ->

        },
        onBottomUI = {
            BottomAppBar(
                backgroundColor = white
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimen_10_dp),
                ) {
                    ButtonPositive(
                        modifier = Modifier.weight(0.4f),
                        buttonTitle = stringResource(R.string.complete_activity),
                        isActive = viewModel.isButtonEnable.value,
                        isArrowRequired = false,
                        onClick = {
                            viewModel.markActivityCompleteStatus()
                            navController.popBackStack()
                        })
                    Spacer(modifier = Modifier.width(10.dp))
                    ButtonPositive(modifier = Modifier.weight(0.4f),
                        buttonTitle = stringResource(id = R.string.generate_form_e),
                        isActive = viewModel.isGenerateFormButtonEnable.value,
                        isArrowRequired = false,
                        onClick = {
                            navigateToDisbursmentSummaryScreen(navController)
                        })
                }
            }
        },
        onContentUI = { paddingValues, isSearch, onSearchValueChanged ->

            Column {
                BaseContentScreen(
                    matId = activityId, contentScreenCategory = ContentCategoryEnum.ACTIVITY.ordinal
                ) { contentValue, contentKey, contentType, isLimitContentData ->
                    if (!isLimitContentData) {
                        navigateToMediaPlayerScreen(navController, contentKey, contentType)
                    } else {
                        navigateToContentDetailScreen(
                            navController,
                            matId = activityId,
                            contentScreenCategory = ContentCategoryEnum.ACTIVITY.ordinal
                        )
                    }
                }
                if (isSearch) {
                    SearchWithFilterViewComponent(
                        placeholderString = viewModel.searchLabel.value,
                        filterSelected = viewModel.isGroupByEnable.value,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        showFilter = viewModel.isFilerEnable.value,
                        onFilterSelected = {
                            if (viewModel.filterList.value.isNotEmpty()) {
                                viewModel.isGroupByEnable.value = !it
                            }
                        },
                        onSearchValueChange = { queryTerm ->
                            viewModel.onEvent(
                                SearchEvent.PerformSearch(
                                    queryTerm, false, BLANK_STRING
                                )
                            )
                        })
                }
                Spacer(modifier = Modifier.height(20.dp))
                if (viewModel.filterTaskMap.isNotEmpty() && viewModel.isGroupByEnable.value) {
                    LazyColumn(
                        modifier = Modifier.padding(bottom = 50.dp)
                    ) {
                        viewModel.filterTaskMap.forEach { (category, itemsInCategory) ->
                            item {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
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
                            itemsIndexed(
                                items = itemsInCategory
                            ) { _, task ->
                                TaskRowView(viewModel, navController, task)
                            }
                        }
                    }
                } else {
                    if (viewModel.filterList.value.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.padding(bottom = 50.dp)) {
                            itemsIndexed(
                                items = viewModel.filterList.value.entries.toList()
                            ) { _, task ->
                                TaskRowView(viewModel, navController, task)
                            }
                        }
                    }
                }
            }
        },
        onSettingClick = onSettingClick
    )
}

@Composable
private fun TaskRowView(
    viewModel: GrantTaskScreenViewModel,
    navController: NavController,
    task: MutableMap.MutableEntry<Int, HashMap<String, GrantTaskCardModel>>
) {
    GrantTaskCard(
        onPrimaryButtonClick = { subjectName ->
            if (!viewModel.isActivityCompleted.value) {
                task.value[GrantTaskCardSlots.GRANT_TASK_STATUS.name]?.value =
                    SurveyStatusEnum.INPROGRESS.name
                viewModel.updateTaskAvailableStatus(
                    taskId = task.key,
                    status = SurveyStatusEnum.INPROGRESS.name,

                    )
            }
            viewModel.activityConfigUiModel?.let {
                navigateToGrantSurveySummaryScreen(
                    navController,
                    taskId = task.key,
                    surveyId = it.surveyId,
                    sectionId = it.sectionId,
                    subjectType = it.subject,
                    subjectName = subjectName,
                    activityConfigId = it.activityConfigId,
                    sanctionedAmount = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_4.name]?.value?.toInt()
                        ?: DEFAULT_ID,
                )
            }
        },
        onNotAvailable = {
            if (!viewModel.isActivityCompleted.value) {

                task.value[GrantTaskCardSlots.GRANT_TASK_STATUS.name]?.value =
                    SurveyStatusEnum.NOT_AVAILABLE.name
                viewModel.updateTaskAvailableStatus(
                    taskId = task.key,
                    status = SurveyStatusEnum.NOT_AVAILABLE.name
                )
                viewModel.checkButtonValidation()
            }
        },
        imagePath = viewModel.getFilePathUri(
            task.value[GrantTaskCardSlots.GRANT_TASK_IMAGE.name]?.value ?: BLANK_STRING
        ),
        title = task.value[GrantTaskCardSlots.GRANT_TASK_TITLE.name],
        subTitle1 = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE.name],
        primaryButtonText = task.value[GrantTaskCardSlots.GRANT_TASK_PRIMARY_BUTTON.name],
        secondaryButtonText = task.value[GrantTaskCardSlots.GRANT_TASK_SECONDARY_BUTTON.name],
        status = task.value[GrantTaskCardSlots.GRANT_TASK_STATUS.name],
        subtitle2 = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_2.name],
        subtitle3 = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_3.name],
        subtitle4 = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_4.name],
        subtitle5 = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_5.name],
        formGeneratedCount = task.value[GrantTaskCardSlots.GRANT_TASK_FORM_GENERATED_COUNT.name]
    )
}

