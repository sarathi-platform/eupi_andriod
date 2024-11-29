package com.sarathi.surveymanager.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun GrantSurveyScreen(
    navController: NavController = rememberNavController(),
    viewModel: GrantSurveyScreenViewModel,
    surveyId: Int,
    sectionId: Int,
    taskId: Int,
    subjectType: String,
    referenceId: String,
    toolbarTitle: String,
    activityConfigId: Int,
    grantId: Int,
    grantType: String,
    sanctionedAmount: Int,
    totalSubmittedAmount: Int,
    onSettingClick: () -> Unit
) {

    BaseSurveyScreen(
        viewModel = viewModel,
        navController = navController,
        surveyId = surveyId,
        sectionId = sectionId,
        taskId = taskId,
        subjectType = subjectType,
        referenceId = referenceId,
        toolbarTitle = toolbarTitle,
        activityConfigId = activityConfigId,
        grantId = grantId,
        grantType = grantType,
        sanctionedAmount = sanctionedAmount,
        totalSubmittedAmount = totalSubmittedAmount,
        onSettingClick = onSettingClick,
        onAnswerSelect = { questionUiModel ->
            viewModel.isButtonEnable.value = viewModel.checkButtonValidation()
        },
        onSubmitButtonClick = {
            viewModel.saveButtonClicked()
            navController.popBackStack()
        }
    )

}