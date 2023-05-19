package com.patsurvey.nudge.activities.survey

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.navigation.home.HomeScreens
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.AnswerOptionType
import com.patsurvey.nudge.utils.TYPE_RADIO_BUTTON
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuestionScreen(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: QuestionScreenViewModel,
    didiId: Int
) {

    LaunchedEffect(key1 = true) {
        viewModel.setDidiDetails(didiId)
        viewModel.getAllQuestionsAnswers(didiId)
    }

    val questionList by viewModel.questionList.collectAsState()
    val answerList by viewModel.answerList.collectAsState()

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val answeredQuestion = remember {
        mutableStateOf(0)
    }
    val context = LocalContext.current

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        VOAndVillageBoxView(
            prefRepo = viewModel.prefRepo,
            modifier = Modifier.fillMaxWidth()
                .padding(top = 10.dp),
            startPadding = 0.dp
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {

            SurveyHeader(
                modifier = Modifier,
                didiName = viewModel.didiName.value,
                questionCount = questionList.size,
                answeredCount = answerList.size,
                partNumber = 1
            )
            answeredQuestion.value=answerList.size
            HorizontalPager(pageCount = questionList.size, state = pagerState, userScrollEnabled = false) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Image(
                            painter = painterResource(id = R.drawable.home_icn),
                            contentDescription = "home image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .width(50.dp)
                                .height(50.dp),
                            colorFilter = ColorFilter.tint(textColorDark)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        if (questionList[it].type == TYPE_RADIO_BUTTON)
                            YesNoQuestion(
                                modifier = Modifier.fillMaxWidth(),
                                questionNumber = (it+1),
                                question = questionList[it].questionDisplay ?: "",
                                answer = viewModel.isYesClick.value,
                                answered = viewModel.isAnswered.value,
                                onYesClicked = {
                                    viewModel.isYesClick.value=true
                                    viewModel.isAnswered.value=true
                                    viewModel.setAnswerToQuestion(didiId,it,AnswerOptionType.OPTION_A.name,
                                        context.getString(R.string.option_yes)){
                                        Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                            if (answeredQuestion.value < (questionList.size-1)) {
                                                answeredQuestion.value = answeredQuestion.value + 1
                                                val nextPageIndex = pagerState.currentPage + 1
                                                coroutineScope.launch { pagerState.animateScrollToPage(nextPageIndex) }
                                                viewModel.isYesClick.value=false
                                                viewModel.isAnswered.value=false
                                                viewModel.updateAnswerOptions(it+1,didiId)
                                            } else if (answeredQuestion.value == (questionList.size-1)) {
                                                navigateToSummeryPage(navController)
                                            }else{
                                                navigateToSummeryPage(navController)
                                            }
                                        },500)

                                    }


                                },
                                onNoClicked = {
                                    viewModel.isYesClick.value=false
                                    viewModel.isAnswered.value=true
                                    viewModel.setAnswerToQuestion(didiId,it,AnswerOptionType.OPTION_B.name,
                                        context.getString(R.string.option_no)){

                                        Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                            if (answeredQuestion.value < (questionList.size-1)) {
                                                answeredQuestion.value = answeredQuestion.value + 1
                                                val nextPageIndex = pagerState.currentPage + 1
                                                coroutineScope.launch { pagerState.animateScrollToPage(nextPageIndex) }
                                                viewModel.isYesClick.value=false
                                                viewModel.isAnswered.value=false
                                                viewModel.updateAnswerOptions(it+1,didiId)
                                            } else if (answeredQuestion.value == (questionList.size-1)) {
                                                navigateToSummeryPage(navController)
                                            }else{
                                                navigateToSummeryPage(navController)
                                            }
                                        },500)
                                    }
                                }
                            )
                    }

                }
            }
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
                    (pagerState.currentPage < questionList.size-1  && pagerState.currentPage <answerList.size)// total pages are 5
                }
            }

            AnimatedVisibility (prevButtonVisible.value) {
                Button(
                    enabled = prevButtonVisible.value,
                    onClick = {
                        val prevPageIndex = pagerState.currentPage - 1
                        coroutineScope.launch { pagerState.animateScrollToPage(prevPageIndex) }
                        viewModel.updateAnswerOptions(prevPageIndex,didiId)
                    },
                ) {
                    Text(text = "Q${pagerState.currentPage}")
                }
            }
            Spacer(modifier = Modifier.width(if (prevButtonVisible.value) 0.dp else ButtonDefaults.MinWidth))
            AnimatedVisibility (nextButtonVisible.value) {
                Button(
                    enabled = nextButtonVisible.value,
                    onClick = {
                        val nextPageIndex = pagerState.currentPage + 1
                        coroutineScope.launch { pagerState.animateScrollToPage(nextPageIndex) }
                        viewModel.updateAnswerOptions(nextPageIndex,didiId)
                    },
                ) {
                    Text(text = "Q${pagerState.currentPage+2}")
                }
            }
        }
    }
}

fun navigateToSummeryPage(navController: NavHostController) {
    navController.navigate(Graph.HOME) {
        popUpTo(HomeScreens.PROGRESS_SCREEN.route) {
            inclusive = true
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
