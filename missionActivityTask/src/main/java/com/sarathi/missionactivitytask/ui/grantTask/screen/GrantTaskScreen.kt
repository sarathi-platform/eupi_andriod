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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ID
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_50_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dimen_72_dp
import com.nudge.core.ui.theme.white
import com.sarathi.contentmodule.ui.content_screen.screen.BaseContentScreen
import com.sarathi.contentmodule.utils.event.SearchEvent
import com.sarathi.dataloadingmangement.model.uiModel.GrantTaskCardSlots
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.navigation.navigateToActivityCompletionScreen
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
import kotlinx.coroutines.delay

private val function = {

}

@Composable
fun GrantTaskScreen(
    navController: NavController = rememberNavController(),
    viewModel: GrantTaskScreenViewModel = hiltViewModel(),
    missionId: Int,
    activityName: String,
    activityId: Int,
    onSettingClick: () -> Unit
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {

        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.setMissionActivityId(missionId, activityId)
        viewModel.onEvent(InitDataEvent.InitDataState)
        onDispose {}
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
                                )
                            )
                        })

                    if (viewModel.isGenerateFormButtonVisible.value) {
                        Spacer(modifier = Modifier.width(10.dp))
                        ButtonPositive(modifier = Modifier.weight(0.5f),
                            buttonTitle = stringResource(id = R.string.generate_form_e),
                            isActive = viewModel.isGenerateFormButtonEnable.value,
                            isArrowRequired = false,
                            onClick = {
                                navigateToDisbursmentSummaryScreen(
                                    navController = navController,
                                    activityId = activityId,
                                    missionId = missionId,
                                    taskIdList = viewModel.getTaskListOfDisburesementAmountEqualSanctionedAmount()
                                )
                            })
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
                                    queryTerm,
                                    viewModel.isGroupByEnable.value,
                                    BLANK_STRING
                                )
                            )
                        })
                }
                Spacer(modifier = Modifier.height(dimen_10_dp))
                if (viewModel.isGroupByEnable.value) {
                    LazyColumn(
                        modifier = Modifier.padding(bottom = dimen_50_dp)
                    ) {
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

                            itemsIndexed(
                                items = itemsInCategory
                            ) { _, task ->
                                TaskRowView(viewModel, navController, task)
                                CustomVerticalSpacer()
                            }
                            item {
                                CustomVerticalSpacer(size = dimen_20_dp)
                            }
                        }
                    }
                } else {
                    if (viewModel.filterList.value.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.padding(bottom = dimen_50_dp)) {
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
                        sanctionedAmount = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_4.name]?.value?.toInt()
                            ?: DEFAULT_ID,
                    )
                }

            }
        },
        onNotAvailable = {
            if (!viewModel.isActivityCompleted.value) {
                viewModel.updateTaskAvailableStatus(
                    taskId = task.key,
                    status = SurveyStatusEnum.NOT_AVAILABLE.name
                )
                viewModel.isActivityCompleted()
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
        formGeneratedCount = task.value[GrantTaskCardSlots.GRANT_TASK_FORM_GENERATED_COUNT.name],
        isActivityCompleted = viewModel.isActivityCompleted.value
    )
}

