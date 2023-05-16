package com.patsurvey.nudge.activities.survey

import android.widget.GridLayout.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.model.dataModel.DidiDetailsModel
import com.patsurvey.nudge.utils.ButtonNegative
import com.patsurvey.nudge.utils.ButtonPositive
import com.patsurvey.nudge.utils.visible

@Composable
fun YesNoQuestionScreen(
    navController: NavHostController,
    modifier: Modifier,
    surveyHeaderUiState: YesNoQuestionViewModel.SurveyHeaderUiState,
    questionAnswerUiState: YesNoQuestionViewModel.QuestionAnswerUiState = YesNoQuestionViewModel.QuestionAnswerUiState(),
    nextPreviousUiState: YesNoQuestionViewModel.NextPreviousUiState,
    onEvent: (YesNoQuestionViewModel.MainEvent) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {
            SurveyHeader(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                didiDetailsModel = surveyHeaderUiState.didiDetailsModel,
                surveyTitle = surveyHeaderUiState.surveyTitle,
                questionCount = surveyHeaderUiState.questionCount,
                answeredCount = surveyHeaderUiState.answeredCount,
                partNumber = surveyHeaderUiState.partNumber
            )
            YesNoQuestion(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                questionNumber = questionAnswerUiState.questionNumber,
                question = questionAnswerUiState.question,
                answer = questionAnswerUiState.answer,
                answered = questionAnswerUiState.answered,
                onYesClicked = {
                    onEvent(YesNoQuestionViewModel.MainEvent.OnButtonClicked(isYes = true))
                },
                onNoClicked = {
                    onEvent(YesNoQuestionViewModel.MainEvent.OnButtonClicked(isYes = false))
                }
            )
        }

        if(nextPreviousUiState.nextVisible || nextPreviousUiState.previousVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 5.dp, bottom = 25.dp),
            ) {
                if(nextPreviousUiState.previousVisible) {
                    ButtonNegative(
                        buttonTitle = nextPreviousUiState.previousText,
                        horizontalPadding = 0.dp,
                        modifier = Modifier
                            .background(color = languageItemActiveBg)
                            .width(100.dp)
                            .align(androidx.compose.ui.Alignment.CenterStart)
                    ) {
                        onEvent(YesNoQuestionViewModel.MainEvent.OnPreviousClicked)
                    }
                }
                if(nextPreviousUiState.nextVisible) {
                    ButtonPositive(
                        buttonTitle = nextPreviousUiState.nextText,
                        isArrowRequired = true,
                        textColor = textColorDark,
                        iconTintColor = textColorDark,
                        modifier = Modifier
                            .background(color = languageItemActiveBg)
                            .width(100.dp)
                            .align(androidx.compose.ui.Alignment.CenterEnd)
                    ) {
                        onEvent(YesNoQuestionViewModel.MainEvent.OnNextClicked)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun YesNoQuestionScreenPreview() {
    val surveyHeader = YesNoQuestionViewModel.SurveyHeaderUiState(
        didiDetailsModel = DidiDetailsModel(
            1,
            "Urmila Devi",
            "Sundar Pahar",
            "Sundar Pahar",
            "Kahar",
            "112",
            "Rajesh"
        ),
        "PAT Survey",
        questionCount = 6,
        answeredCount = 1,
        partNumber = 1
    )


    YesNoQuestionScreen(
        navController = rememberNavController(),
        modifier = Modifier.fillMaxSize(),
        surveyHeaderUiState = YesNoQuestionViewModel.SurveyHeaderUiState(
            surveyHeader.didiDetailsModel,
            surveyHeader.surveyTitle,
            surveyHeader.questionCount,
            surveyHeader.answeredCount,
            surveyHeader.partNumber
        ),
        questionAnswerUiState = YesNoQuestionViewModel.QuestionAnswerUiState(
            question = "Is anyone in the family in government service?",
            questionNumber = 1,
            answer = false,
            answered = false
        ),
        nextPreviousUiState = YesNoQuestionViewModel.NextPreviousUiState(
            nextVisible = true,
            nextText = "Q3",
            previousText = "Q1",
            previousVisible = true
        ),
        onEvent = {}

    )
}