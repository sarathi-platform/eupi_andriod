package com.sarathi.missionactivitytask.ui.grantTask.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.sarathi.contentmodule.utils.event.SearchEvent
import com.sarathi.missionactivitytask.navigation.navigateToMediaPlayerScreen
import com.sarathi.missionactivitytask.navigation.navigateToSurveyScreen
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.GrantTaskScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent

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
            viewModel.onEvent(
                SearchEvent.PerformSearch(
                    queryTerm,
                    false,
                    BLANK_STRING
                )
            )
        },
        onBottomUI = {
        },
        onContentUI = { paddingValues, isSearch, onSearchValueChanged ->
            if (viewModel.filterList.value.isNotEmpty()) {
                GrantTaskList(
                    taskList = viewModel.filterList.value,
                    isSearch = isSearch,
                    searchPlaceholder = viewModel.searchLabel.value,
                    onSearchValueChange = onSearchValueChanged,
                    navController = navController,
                    onContentData = { contentValue, contentKey, contentType ->
                        navigateToMediaPlayerScreen(navController, contentKey, contentType)
                    },
                    onPrimaryButtonClick = { taskId ->
                        viewModel.activityConfigUiModel?.let {
                            navigateToSurveyScreen(
                                navController,
                                taskId = taskId,
                                surveyId = it.surveyId,
                                sectionId = it.sectionId,
                                subjectType = it.subject
                            )

                        }
                    }
                )
            }
        },
        onSettingClick = onSettingClick
    )
}

