package com.sarathi.surveymanager.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sarathi.dataloadingmangement.BLANK_STRING

@Composable
fun BaselineSurveyScreen(
    navController: NavController = rememberNavController(),
    viewModel: BaselineSurveyScreenViewModel,
    surveyId: Int,
    sectionId: Int,
    taskId: Int,
    subjectType: String,
    toolbarTitle: String,
    activityConfigId: Int,
    grantId: Int,
    grantType: String,
    sanctionedAmount: Int,
    totalSubmittedAmount: Int,
    onSettingClick: () -> Unit
) {
    SurveyScreen(
        viewModel = viewModel,
        navController = navController,
        surveyId = surveyId,
        sectionId = sectionId,
        taskId = taskId,
        subjectType = subjectType,
        referenceId = BLANK_STRING,
        toolbarTitle = toolbarTitle,
        activityConfigId = activityConfigId,
        grantId = grantId,
        grantType = grantType,
        sanctionedAmount = sanctionedAmount,
        totalSubmittedAmount = totalSubmittedAmount,
        onSettingClick = onSettingClick,
        onAnswerSelect = { questionUiModel ->
            viewModel.saveSingleAnswerIntoDb(questionUiModel)

        }
    )


}


