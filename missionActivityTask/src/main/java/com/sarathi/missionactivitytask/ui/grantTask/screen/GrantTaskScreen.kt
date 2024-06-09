package com.sarathi.missionactivitytask.ui.grantTask.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.sarathi.contentmodule.ui.content_screen.screen.BaseContentScreen
import com.sarathi.contentmodule.utils.event.SearchEvent
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.navigation.navigateToContentDetailScreen
import com.sarathi.missionactivitytask.navigation.navigateToGrantSurveySummaryScreen
import com.sarathi.missionactivitytask.navigation.navigateToMediaPlayerScreen
import com.sarathi.missionactivitytask.ui.basic_content.component.GrantTaskCard
import com.sarathi.missionactivitytask.ui.components.SearchWithFilterViewComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.grantTask.model.GrantTaskCardSlots
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                ButtonPositive(
                    buttonTitle = stringResource(R.string.complete_activity),
                    isActive = viewModel.isButtonEnable.value,
                    isArrowRequired = false,
                    onClick = {
                        viewModel.markActivityCompleteStatus()
                        navController.popBackStack()
                    }
                )
            }
        },
        onContentUI = { paddingValues, isSearch, onSearchValueChanged ->

            Column {
                BaseContentScreen { contentValue, contentKey, contentType, isLimitContentData ->
                    if (!isLimitContentData) {
                        navigateToMediaPlayerScreen(navController, contentKey, contentType)
                    } else {
                        navigateToContentDetailScreen(navController)
                    }
                }
                if (isSearch) {
                    SearchWithFilterViewComponent(
                        placeholderString = viewModel.searchLabel.value,
                        filterSelected = false,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        showFilter = false,
                        onFilterSelected = {},
                        onSearchValueChange = { queryTerm ->
                            viewModel.onEvent(
                                SearchEvent.PerformSearch(
                                    queryTerm,
                                    false,
                                    BLANK_STRING
                                )
                            )
                        })
                }
                Spacer(modifier = Modifier.height(20.dp))
                if (viewModel.filterList.value.isNotEmpty()) {
                    LazyColumn {
                        itemsIndexed(
                            items = viewModel.filterList.value.entries.toList()
                        ) { index, task ->

                            GrantTaskCard(
                                onPrimaryButtonClick = { subjectName ->
                                    viewModel.activityConfigUiModel?.let {
                                        navigateToGrantSurveySummaryScreen(
                                            navController,
                                            taskId = task.key,
                                            surveyId = it.surveyId,
                                            sectionId = it.sectionId,
                                            subjectType = it.subject,
                                            subjectName = subjectName,
                                            activityConfigId = it.activityConfigId,
                                        )
                                    }
                                },
                                imagePath = task.value[GrantTaskCardSlots.GRANT_TASK_IMAGE.name]
                                    ?: BLANK_STRING,
                                title = task.value[GrantTaskCardSlots.GRANT_TASK_TITLE.name]
                                    ?: BLANK_STRING,
                                subTitle1 = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE.name]
                                    ?: BLANK_STRING,
                                primaryButtonText = task.value[GrantTaskCardSlots.GRANT_TASK_PRIMARY_BUTTON.name]
                                    ?: BLANK_STRING,
                                secondaryButtonText = task.value[GrantTaskCardSlots.GRANT_TASK_SECONDARY_BUTTON.name]
                                    ?: BLANK_STRING,
                                status = task.value[GrantTaskCardSlots.GRANT_TASK_STATUS.name]
                                    ?: BLANK_STRING,

                                subtitle2 = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_2.name]
                                    ?: BLANK_STRING,
                                subtitle3 = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_3.name]
                                    ?: BLANK_STRING,
                                subtitle4 = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_4.name]
                                    ?: BLANK_STRING,
                                subtitle5 = task.value[GrantTaskCardSlots.GRANT_TASK_SUBTITLE_5.name]
                                    ?: BLANK_STRING
                            )
                        }
                    }
                }
            }
        },
        onSettingClick = onSettingClick
    )
}

