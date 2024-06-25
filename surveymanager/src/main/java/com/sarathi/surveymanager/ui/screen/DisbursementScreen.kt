package com.sarathi.surveymanager.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.nudge.core.toInMillisec
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.dimen_100_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.red
import com.nudge.core.ui.theme.textColorDark
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
        onSearchValueChange = {

        },
        onBottomUI = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_10_dp)
            ) {
                ButtonPositive(
                    buttonTitle = if (viewModel.isManualTaskCompleteActive()) stringResource(R.string.complete) else stringResource(
                        R.string.go_back
                    ),
                    isActive = viewModel.isButtonEnable.value,
                    isArrowRequired = !viewModel.isManualTaskCompleteActive(),
                    isLeftArrow = !viewModel.isManualTaskCompleteActive(),
                    onClick = {
                        if (viewModel.isManualTaskCompleteActive()) {
                        viewModel.saveButtonClicked()
                        onNavigateSuccessScreen(
                            "${
                                viewModel.grantConfigUi.value.grantComponentDTO?.grantComponentName
                                    ?: BLANK_STRING
                            } for ${subjectName}"
                        )
                        } else {
                            navController.popBackStack()
                        }
                    }
                )
            }
        },
        onContentUI = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    if (!viewModel.isAddDisbursementButtonEnable.value && sanctionedAmount != 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimen_16_dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            color = red,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = NotoSans
                                        )
                                    ) {
                                        append("*")
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            color = textColorDark,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = NotoSans
                                        )
                                    ) {
                                        append("Sanctioned amount has been fully disbursed’")
                                    }

                                }
                            )
                        }
                    }

                }
                item {
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
                                Column {
                                    viewModel.taskList.value.entries.toList().sortedByDescending {
                                        viewModel.getSurveyUIModel(it.value).subTittle1.toInMillisec(
                                            format = "dd MMM, yyyy"
                                        )
                                    }
                                        .forEachIndexed { index, task ->
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
                                            Spacer(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(
                                                        dimen_10_dp
                                                    )
                                            )
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
                }
                item {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimen_100_dp)
                    )
                }
            }
        },
        onSettingClick = onSettingClick
    )
}
