package com.sarathi.surveymanager.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.surveymanager.ui.component.ShowCustomDialog
import com.sarathi.surveymanager.ui.component.ToolBarWithMenuComponent
import com.sarathi.surveymanager.viewmodels.DisbursementSummaryScreenViewModel

@Composable
fun DisbursementSummaryScreen(
    navController: NavController = rememberNavController(),
    viewModel: DisbursementSummaryScreenViewModel,
    onSettingClick: () -> Unit,
    surveyId: Int,
    sectionId: Int,
    taskId: Int,
    subjectType: String,
    subjectName: String,
    activityConfigId: Int,
    onNavigateSurveyScreen: (referenceId: String, activityConfigIs: Int) -> Unit,
) {

    LaunchedEffect(key1 = true) {
        viewModel.setPreviousScreenData(surveyId, sectionId, taskId, subjectType, activityConfigId)
        viewModel.onEvent(InitDataEvent.InitDataState)
    }
    ToolBarWithMenuComponent(
        title = subjectName,
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = false,
        isDataAvailable = false,
        onSearchValueChange = {

        },
        onBottomUI = {
        },
        onContentUI = { paddingValues ->
            CollapsibleCard(
                summaryCount = viewModel.taskList.value.entries.size, onClick = {
                    onNavigateSurveyScreen(viewModel.getReferenceId(), activityConfigId)
                },
                onContentUI = {
                    if (viewModel.taskList.value.isNotEmpty()) {
                        LazyColumn {
                            itemsIndexed(
                                items = viewModel.taskList.value.entries.toList()
                            ) { index, task ->
                                val surveyData = viewModel.getSurveyUIModel(task.value)
                                DisbursementCard(
                                    subTitle1 = surveyData.subTittle1,
                                    subTitle2 = surveyData.subTittle2,
                                    subTitle3 = surveyData.subTittle3,
                                    onEditSurvey = {
                                        onNavigateSurveyScreen(
                                            surveyData.referenceId,
                                            activityConfigId
                                        )
                                    },
                                    onDeleteSurvey = {
                                        viewModel.showDialog.value =
                                            Pair(true, surveyData.referenceId)
                                    }
                                )
                            }
                        }
                        if (viewModel.showDialog.value.first) {
                            ShowCustomDialog(
                                "Main Title",
                                message = "New Message You wealth ranking for",
                                negativeButtonTitle = "No",
                                positiveButtonTitle = "Yes",
                                onNegativeButtonClick = {
                                    viewModel.showDialog.value = Pair(false, BLANK_STRING)
                                },
                                onPositiveButtonClick = {
                                    viewModel.showDialog.value.second?.let {
                                        viewModel.deleteSurveyAnswer(
                                            referenceId = it
                                        )
                                    }
                                    viewModel.showDialog.value = Pair(false, BLANK_STRING)
                                }
                            )
                        }
                    }
                })

        },
        onSettingClick = onSettingClick
    )
}
