package com.patsurvey.nudge.activities.survey

import android.widget.GridLayout.Alignment
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.model.dataModel.DidiDetailsModel
import com.patsurvey.nudge.navigation.home.HomeScreens
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.ButtonNegative
import com.patsurvey.nudge.utils.ButtonPositive
import com.patsurvey.nudge.utils.TYPE_RADIO_BUTTON
import com.patsurvey.nudge.utils.visible
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun YesNoQuestionScreen(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: YesNoQuestionViewModel,
    didiId: Int = 106
) {

    LaunchedEffect(key1 = true) {
        viewModel.setDidiDetails(didiId)
    }

    val questionList by viewModel.questionList.collectAsState()

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val answeredQuestion = remember {
        mutableStateOf(0)
    }

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {
            SurveyHeader(
                modifier = Modifier,
                didiName = viewModel.didiName.value,
                villageEntity = viewModel.prefRepo.getSelectedVillage(),
                questionCount = questionList.size,
                answeredCount = answeredQuestion.value,
                partNumber = 1
            )

            HorizontalPager(pageCount = questionList.size, state = pagerState, userScrollEnabled = false) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (questionList[it].type == TYPE_RADIO_BUTTON)
                    YesNoQuestion(
                        modifier = Modifier.fillMaxWidth(),
                        questionNumber = it,
                        question = questionList[it].questionDisplay ?: "",
                        onYesClicked = {
                            if (answeredQuestion.value < questionList.size) {
                                answeredQuestion.value = answeredQuestion.value + 1
                                val nextPageIndex = pagerState.currentPage + 1
                                coroutineScope.launch { pagerState.animateScrollToPage(nextPageIndex) }
                            } else if (answeredQuestion.value == questionList.size) {
                                navController.navigate(Graph.HOME) {
                                    popUpTo(HomeScreens.PROGRESS_SCREEN.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        },
                        onNoClicked = {
                            if (answeredQuestion.value < questionList.size) {
                                answeredQuestion.value = answeredQuestion.value + 1
                                val nextPageIndex = pagerState.currentPage + 1
                                coroutineScope.launch { pagerState.animateScrollToPage(nextPageIndex) }
                            } else if (answeredQuestion.value == questionList.size) {
                                navController.navigate(Graph.HOME) {
                                    popUpTo(HomeScreens.PROGRESS_SCREEN.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    )
                }
            }
            /*YesNoQuestion(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
            )*/
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val prevButtonVisible = remember {
                derivedStateOf {
                    pagerState.currentPage > 0
                }
            }

            val nextButtonVisible = remember {
                derivedStateOf {
                    pagerState.currentPage < questionList.size-1 // total pages are 5
                }
            }

            AnimatedVisibility (prevButtonVisible.value) {
                Button(
                    enabled = prevButtonVisible.value,
                    onClick = {
                        val prevPageIndex = pagerState.currentPage - 1
                        coroutineScope.launch { pagerState.animateScrollToPage(prevPageIndex) }
                    },
                ) {
                    Text(text = "Q${questionList[pagerState.currentPage].questionId}")
                }
            }
            Spacer(modifier = Modifier.width(if (prevButtonVisible.value) 0.dp else ButtonDefaults.MinWidth))
            AnimatedVisibility (nextButtonVisible.value) {
                Button(
                    enabled = nextButtonVisible.value,
                    onClick = {
                        val nextPageIndex = pagerState.currentPage + 1
                        coroutineScope.launch { pagerState.animateScrollToPage(nextPageIndex) }
                    },
                ) {
                    Text(text = "Q${questionList[pagerState.currentPage].questionId}")
                }
            }
        }
    }
}

/*
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
}*/
