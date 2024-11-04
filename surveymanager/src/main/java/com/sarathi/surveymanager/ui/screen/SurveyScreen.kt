@file:JvmName("SurveyScreenKt")

package com.sarathi.surveymanager.ui.screen

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum

@Composable
fun SurveyScreen(
    navController: NavController = rememberNavController(),
    viewModel: SurveyScreenViewModel,
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
    onSettingClick: () -> Unit,
    onFormTypeQuestionClicked: (sectionId: Int, surveyId: Int, formId: Int, taskId: Int, activityId: Int, activityConfigId: Int, missionId: Int, subjectType: String, referenceId: String) -> Unit,
    onViewFormSummaryClicked: (taskId: Int, surveyId: Int, sectionId: Int, formId: Int, activityConfigId: Int) -> Unit
) {
    BaseSurveyScreen(
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
        onBackClicked = {
            if (viewModel.isNoSection.value) {
                navController.popBackStack()
                navController.popBackStack()
            } else {
                navController.popBackStack()
            }
        },
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
            if (viewModel.isNoSection.value) {
                viewModel.updateTaskStatus(taskId, true)
            }
            viewModel.updateSectionStatus(
                missionId,
                surveyId,
                sectionId,
                taskId,
                SurveyStatusEnum.COMPLETED.name
            ) {
                navController.popBackStack()
                navController.popBackStack()
            }
        },
        surveyQuestionContent = { maxHeight ->
            SurveyScreenContent(
                viewModel = viewModel,
                sanctionedAmount = sanctionedAmount,
                totalSubmittedAmount = totalSubmittedAmount,
                grantType = activityType,
                maxHeight = maxHeight,
                onAnswerSelect = { questionUiModel ->
                    viewModel.updateQuestionResponseMap(questionUiModel)
                    viewModel.runConditionCheck(questionUiModel)

                    viewModel.saveSingleAnswerIntoDb(questionUiModel)
                    viewModel.updateTaskStatus(taskId)
                },
                onViewSummaryClicked = { questionUiModel ->
                    onViewFormSummaryClicked(
                        taskId,
                        surveyId,
                        sectionId,
                        questionUiModel.formId,
                        activityConfigId
                    )
                },
                onFormTypeQuestionClicked = { sectionId, surveyId, formId, referenceId ->
                    onFormTypeQuestionClicked(
                        sectionId,
                        surveyId,
                        formId,
                        taskId,
                        activityId,
                        activityConfigId,
                        missionId,
                        subjectType,
                        referenceId
                    )
                },
            )
        }
    )


}

fun LazyListScope.SurveyScreenContent(
    viewModel: BaseSurveyScreenViewModel,
    sanctionedAmount: Int,
    totalSubmittedAmount: Int,
    grantType: String,
    maxHeight: Dp,
    onAnswerSelect: (QuestionUiModel) -> Unit,
    onViewSummaryClicked: (QuestionUiModel) -> Unit,
    onFormTypeQuestionClicked: (sectionId: Int, surveyId: Int, formId: Int, referenceId: String) -> Unit,
) {

    val formIdCountMap: MutableMap<Int, Int> = mutableMapOf()

    viewModel.questionUiModel.value.sortedBy { it.order }.forEachIndexed { index, question ->
        if (question.formId == 0) {
            item {
                if (viewModel.visibilityMap[question.questionId].value()) {
                    QuestionUiContent(
                        question,
                        sanctionedAmount,
                        totalSubmittedAmount,
                        viewModel,
                        onAnswerSelect,
                        maxHeight,
                        grantType,
                        index
                    )
                }
            }
        } else {
            val formIdCount = formIdCountMap.get(question.formId).value(0)
            if (formIdCount == 0) {
                if (viewModel.visibilityMap[question.questionId] == true) {
                    item {
                        FormQuestionUiContent(
                            question = question,
                            viewModel = viewModel,
                            maxHeight = maxHeight,
                            grantType = grantType,
                            index = index,
                            onAnswerSelect = onAnswerSelect,
                            onClick = {
                                onFormTypeQuestionClicked(
                                    question.sectionId,
                                    question.surveyId,
                                    question.formId,
                                    viewModel.referenceId
                                )
                            },
                            onViewSummaryClicked = { questionUiModel ->
                                onViewSummaryClicked(questionUiModel)
                            }
                        )
                    }
                }
            }
            formIdCountMap[question.formId] = formIdCount + 1
        }


    }
}


