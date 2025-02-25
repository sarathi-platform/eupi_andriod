package com.sarathi.surveymanager.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sarathi.dataloadingmangement.R
import com.sarathi.surveymanager.ui.component.ShowCustomDialog

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
    if (viewModel.showCustomDialog.value) {
        ShowCustomDialog(
            title = viewModel.stringResource(R.string.are_you_sure),
            message = viewModel.stringResource(R.string.form_alert_dialog_message),
            positiveButtonTitle = viewModel.stringResource(R.string.proceed_txt),
            negativeButtonTitle = viewModel.stringResource(R.string.cancel_txt),
            onPositiveButtonClick = {
                viewModel.showCustomDialog.value = false
                if (viewModel.isSettingClicked.value) {
                    onSettingClick()
                } else {
                    navController.popBackStack()
                }
            }, onNegativeButtonClick = {
                viewModel.showCustomDialog.value = false

            }
        )
    }

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
        onSettingClick = {
            viewModel.isSettingClicked.value = true
            if (viewModel.isAnyOptionValueChanged.value) {
                viewModel.showCustomDialog.value = true
            } else {
                onSettingClick()
            }
        },
        onAnswerSelect = { questionUiModel ->
            viewModel.isAnyOptionValueChanged.value = true
            viewModel.isButtonEnable.value = viewModel.checkButtonValidation()
        },
        onSubmitButtonClick = {
            viewModel.saveButtonClicked()
            navController.popBackStack()
        },
        onBackClicked = {
            viewModel.isSettingClicked.value = false
            if (viewModel.isAnyOptionValueChanged.value) {
                viewModel.showCustomDialog.value = true
            } else {
                navController.popBackStack()
            }
        }
    )

}