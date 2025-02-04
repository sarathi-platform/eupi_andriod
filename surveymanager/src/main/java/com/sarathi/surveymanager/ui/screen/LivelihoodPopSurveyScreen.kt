package com.sarathi.surveymanager.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.ui.theme.bgGreyLight
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.uncheckedTrackColor
import com.nudge.core.value
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.constants.QuestionType

@Composable
fun LivelihoodPopSurveyScreen(
    navController: NavController = rememberNavController(),
    viewModel: LivelihoodPopSurveyScreenViewModel,
    surveyId: Int,
    sectionId: Int,
    taskId: Int,
    subjectType: String,
    referenceId: String,
    toolbarTitle: String,
    activityConfigId: Int,
    grantId: Int,
    activityType: String,
    sanctionedAmount: Int,
    totalSubmittedAmount: Int,
    onSettingClick: () -> Unit
) {
    BaseSurveyScreen(
        isActivityReferenceId = viewModel.activityConfig?.activityType.equals(
            ActivityTypeEnum.LIVELIHOOD_PoP.name, true
        ) && viewModel.activityConfig?.referenceId == null && viewModel.activityConfig?.referenceId == 0,
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
        grantType = activityType,
        sanctionedAmount = sanctionedAmount,
        totalSubmittedAmount = totalSubmittedAmount,
        onSettingClick = onSettingClick,
        onAnswerSelect = { questionUiModel ->
            /**
             * Not Required here as it is handled in the LivelihoodPopSurveyQuestionContent itself
             * */
        },
        onSubmitButtonClick = {
            viewModel.checkAndWriteMoneyJournalEvent()
            viewModel.updateTaskStatus(taskId, true)
            navController.popBackStack()
            navController.popBackStack()
        },
        surveyQuestionContent = { maxHeight ->

            LivelihoodPopSurveyQuestionContent(
                viewModel = viewModel,
                sanctionedAmount = sanctionedAmount,
                totalSubmittedAmount = totalSubmittedAmount,
                onAnswerSelect = { questionUiModel ->

                    viewModel.updateQuestionResponseMap(questionUiModel)
                    viewModel.runConditionCheck(questionUiModel)

                    viewModel.runValidationCheck(questionUiModel.questionId) { isValid, message ->
                        viewModel.fieldValidationAndMessageMap[questionUiModel.questionId] =
                            Triple(
                                isValid,
                                message,
                                if (QuestionType.userInputQuestionTypeList.contains(
                                        questionUiModel.type.toLowerCase()
                                    )
                                ) (questionUiModel.options?.firstOrNull()?.selectedValue
                                    ?: BLANK_STRING) else null
                            )
                    }

                    viewModel.saveSingleAnswerIntoDb(questionUiModel)
                    viewModel.updateTaskStatus(taskId)
                },
                activityType = activityType,
                maxHeight = maxHeight
            )

        }
    )
}


fun LazyListScope.LivelihoodPopSurveyQuestionContent(
    viewModel: BaseSurveyScreenViewModel,
    sanctionedAmount: Int,
    totalSubmittedAmount: Int,
    onAnswerSelect: (QuestionUiModel) -> Unit,
    activityType: String,
    maxHeight: Dp
) {

    item {
        Box(
            modifier = Modifier
                .background(
                    bgGreyLight, RoundedCornerShape(
                        roundedCornerRadiusDefault
                    )
                )
                .border(
                    width = dimen_1_dp,
                    color = uncheckedTrackColor,
                    shape = RoundedCornerShape(
                        roundedCornerRadiusDefault
                    )
                )
                .padding(bottom = dimen_8_dp)
                .padding(horizontal = dimen_8_dp)
        ) {
            Column {
                viewModel.questionUiModel.value.forEachIndexed { index, question ->
                    if (viewModel.visibilityMap[question.questionId].value()) {
                        QuestionUiContent(
                            question,
                            sanctionedAmount,
                            totalSubmittedAmount,
                            viewModel,
                            onAnswerSelect,
                            maxHeight,
                            activityType,
                            index,
                        )
                    }
                }
            }
        }
    }

}
