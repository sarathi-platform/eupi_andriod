package com.sarathi.surveymanager.ui.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.ui.theme.dimen_14_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.greyBorderColor
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel

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
            viewModel.saveSingleAnswerIntoDb(questionUiModel)
        },
        onSubmitButtonClick = {
            viewModel.updateTaskStatus(taskId, true)
        },
        surveyQuestionContent = { maxHeight ->

            LivelihoodPopSurveyQuestionContent(
                viewModel = viewModel,
                sanctionedAmount = sanctionedAmount,
                totalSubmittedAmount = totalSubmittedAmount,
                onAnswerSelect = { questionUiModel ->
                    viewModel.saveSingleAnswerIntoDb(questionUiModel)
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

    itemsIndexed(viewModel.questionUiModel.value) { index, question ->

        Box(
            modifier = Modifier
                .border(
                    width = dimen_1_dp,
                    color = greyBorderColor,
                    shape = RoundedCornerShape(
                        roundedCornerRadiusDefault
                    )
                )
                .padding(vertical = dimen_14_dp)
        ) {
            QuestionUiContent(
                question,
                sanctionedAmount,
                totalSubmittedAmount,
                viewModel,
                onAnswerSelect,
                maxHeight,
                activityType,
                index
            )
        }

    }

}
