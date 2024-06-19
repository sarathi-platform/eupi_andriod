package com.sarathi.surveymanager.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.black1
import com.nudge.core.ui.theme.dimen_5_dp
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.ui.component.ButtonPositive
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
    sanctionedAmount: Int,
    onNavigateSurveyScreen: (referenceId: String, activityConfigIs: Int, grantId: Int, grantType: String, sanctionedAmount: Int, totalSubmittedAmount: Int) -> Unit,
    onNavigateSuccessScreen: (mag: String) -> Unit
) {

    LaunchedEffect(key1 = true) {
        viewModel.setPreviousScreenData(
            surveyId,
            sectionId,
            taskId,
            subjectType,
            activityConfigId,
            sanctionedAmount
        )
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                ButtonPositive(
                    buttonTitle = "Done",
                    isActive = viewModel.isButtonEnable.value,
                    isLeftArrow = false,
                    onClick = {
                        viewModel.saveButtonClicked()
                        onNavigateSuccessScreen("")
                    }
                )
            }
        },
        onContentUI = { paddingValues ->
            CollapsibleCard(
                title = viewModel.grantConfigUi.value.grantComponentDTO?.grantComponentName
                    ?: BLANK_STRING,
                summaryCount = viewModel.taskList.value.entries.size,
                onClick = {
                    onNavigateSurveyScreen(
                        viewModel.createReferenceId(),
                        activityConfigId,
                        viewModel.grantConfigUi.value.grantId,
                        viewModel.grantConfigUi.value.grantType,
                        sanctionedAmount,
                        viewModel.getTotalSubmittedAmount()
                    )
                },
                isEditable = viewModel.isAddDisbursementButtonEnable.value,
                onContentUI = {
                    if (viewModel.taskList.value.isNotEmpty()) {
                        LazyColumn {
                            item {
                                Divider(
                                    modifier = Modifier.padding(vertical = dimen_5_dp),
                                    color = black1,
                                    thickness = 0.5.dp
                                )
                            }
                            itemsIndexed(
                                items = viewModel.taskList.value.entries.toList()
                            ) { index, task ->
                                val surveyData = viewModel.getSurveyUIModel(task.value)
                                DisbursementCard(
                                    subTitle1 = surveyData.subTittle1,
                                    subTitle2 = surveyData.subTittle2,
                                    subTitle3 = surveyData.subTittle3,
                                    subTitle4 = surveyData.subTittle4,
                                    subTitle5 = surveyData.subTittle5,
                                    isFormgenerated = surveyData.isFormGenerated,
                                    onEditSurvey = {
                                        onNavigateSurveyScreen(
                                            surveyData.referenceId,
                                            activityConfigId,
                                            viewModel.grantConfigUi.value.grantId,
                                            viewModel.grantConfigUi.value.grantType
                                                ?: BLANK_STRING,
                                            sanctionedAmount,
                                            viewModel.getTotalSubmittedAmount()
                                        )
                                    },
                                    onDeleteSurvey = {
                                        if (!viewModel.isActivityCompleted.value) {
                                            viewModel.showDialog.value =
                                                Pair(true, surveyData.referenceId)
                                        }
                                    }
                                )
                                if (index != viewModel.taskList.value.entries.size - 1) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = dimen_5_dp),
                                        color = black1,
                                        thickness = 0.5.dp
                                    )
                                }

                            }
                        }
                        if (viewModel.showDialog.value.first) {
                            ShowCustomDialog(
                                message = stringResource(R.string.are_you_sure_you_want_to_delete),
                                negativeButtonTitle = stringResource(R.string.no),
                                positiveButtonTitle = stringResource(R.string.yes),
                                onNegativeButtonClick = {
                                    viewModel.showDialog.value = Pair(false, BLANK_STRING)
                                },
                                onPositiveButtonClick = {
                                    viewModel.showDialog.value.second?.let {
                                        viewModel.deleteSurveyAnswer(
                                            referenceId = it
                                        ) { deleteCount ->
                                            viewModel.onEvent(InitDataEvent.InitDataState)
                                        }
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
