@file:JvmName("SurveyScreenKt")

package com.sarathi.surveymanager.ui.screen

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.ACTIVITY_COMPLETED_ERROR
import com.nudge.core.FORM_RESPONSE_LIMIT_ERROR
import com.nudge.core.showCustomToast
import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.surveymanager.R
import kotlinx.coroutines.launch

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
    navigateToMediaPlayerScreen: (content: ContentList) -> Unit = {},
    onSettingClick: () -> Unit,
    onFormTypeQuestionClicked: (sectionId: Int, surveyId: Int, formId: Int, taskId: Int, activityId: Int, activityConfigId: Int, missionId: Int, subjectType: String, referenceId: String) -> Unit,
    onViewFormSummaryClicked: (taskId: Int, surveyId: Int, sectionId: Int, formId: Int, activityConfigId: Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
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
        navigateToMediaPlayerScreen = { content ->
            navigateToMediaPlayerScreen(content)
        },
        onBackClicked = {
            if (viewModel.isNoSection.value) {
                navController.popBackStack()
                navController.popBackStack()
            } else {
                navController.popBackStack()
            }
        },
        onAnswerSelect = { questionUiModel ->
            coroutineScope.launch {
                viewModel.runValidationCheck(questionId = questionUiModel.questionId) { isValid, message ->
                    viewModel.fieldValidationAndMessageMap[questionUiModel.questionId] =
                        Triple(
                            isValid, message, if (QuestionType.userInputQuestionTypeList.contains(
                                    questionUiModel.type.toLowerCase()
                                )
                            ) (questionUiModel.options?.firstOrNull()?.selectedValue
                                ?: com.nudge.core.BLANK_STRING) else null
                        )
                }
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
            }
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
                if (viewModel.isNoSection.value) {
                    navController.popBackStack()
                    navController.popBackStack()
                } else {
                    navController.popBackStack()
                }
            }
        },
        surveyQuestionContent = { maxHeight ->
            SurveyScreenContent(
                viewModel = viewModel,
                sanctionedAmount = sanctionedAmount,
                totalSubmittedAmount = totalSubmittedAmount,
                navigateToMediaPlayerScreen = { content ->
                    navigateToMediaPlayerScreen(content)
                },
                grantType = activityType,
                maxHeight = maxHeight,
                onAnswerSelect = { questionUiModel ->
                    coroutineScope.launch {
                        viewModel.updateQuestionResponseMap(questionUiModel)
                        viewModel.runConditionCheck(questionUiModel)
                        viewModel.runValidationCheck(questionId = questionUiModel.questionId) { isValid, message ->
                            viewModel.fieldValidationAndMessageMap[questionUiModel.questionId] =
                                Triple(
                                    isValid,
                                    message,
                                    if (QuestionType.userInputQuestionTypeList.contains(
                                            questionUiModel.type.toLowerCase()
                                        )
                                    ) (questionUiModel.options?.firstOrNull()?.selectedValue
                                        ?: com.nudge.core.BLANK_STRING) else null
                                )
                        }

                        viewModel.saveSingleAnswerIntoDb(questionUiModel)
                        viewModel.updateMissionFilter()
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
                    }

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
    navigateToMediaPlayerScreen: (content: ContentList) -> Unit = {},
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
                        index,
                        navigateToMediaPlayerScreen = { content ->
                            navigateToMediaPlayerScreen(content)
                        }
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
                            },
                            showEditErrorToast = { context, errorType ->
                                if (errorType == ACTIVITY_COMPLETED_ERROR) {
                                    showCustomToast(
                                        context = context,
                                        context.getString(R.string.edit_disable_message)
                                    )
                                    return@FormQuestionUiContent
                                }

                                if (errorType == FORM_RESPONSE_LIMIT_ERROR) {
                                    showCustomToast(
                                        context = context,
                                        context.getString(R.string.details_have_already_been_added)
                                    )
                                    return@FormQuestionUiContent
                                }
                            }
                        )
                    }
                }
            }
            formIdCountMap[question.formId] = formIdCount + 1
        }


    }
}


