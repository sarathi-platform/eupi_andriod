package com.sarathi.surveymanager.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum

@Composable
fun BaselineSurveyScreen(
    navController: NavController = rememberNavController(),
    viewModel: BaselineSurveyScreenViewModel,
    missionId: Int,
    activityId: Int,
    surveyId: Int,
    sectionId: Int,
    taskId: Int,
    subjectType: String,
    toolbarTitle: String,
    activityConfigId: Int,
    grantId: Int,
    activityType: String,
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
        grantType = activityType,
        sanctionedAmount = sanctionedAmount,
        totalSubmittedAmount = totalSubmittedAmount,
        onSettingClick = onSettingClick,
        onAnswerSelect = { questionUiModel ->
            viewModel.saveSingleAnswerIntoDb(questionUiModel)
            viewModel.updateTaskStatus(taskId)
            viewModel.updateSectionStatus(
                missionId,
                surveyId,
                sectionId,
                taskId,
                SurveyStatusEnum.INPROGRESS.name,
                callBack = {
                    //No Implementation required here.
                }
            )
        },
        onSubmitButtonClick = {
            viewModel.updateSectionStatus(
                missionId,
                surveyId,
                sectionId,
                taskId,
                SurveyStatusEnum.COMPLETED.name
            ) {
                navController.popBackStack()
            }
        }
    )


}


