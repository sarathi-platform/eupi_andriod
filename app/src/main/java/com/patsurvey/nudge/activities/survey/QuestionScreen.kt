package com.patsurvey.nudge.activities.survey

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.model.dataModel.AnswerOptionModel
import com.patsurvey.nudge.navigation.home.HomeScreens
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.AnswerOptionType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QuestionType
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuestionScreen(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: QuestionScreenViewModel,
    didiId: Int,
    sectionType:String
) {

    LaunchedEffect(key1 = true) {
        viewModel.setDidiDetails(didiId)
        viewModel.sectionType.value=sectionType
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

    BackHandler() {
        navController.navigate(Graph.HOME) {
            popUpTo(HomeScreens.PROGRESS_SCREEN.route) {
                inclusive = true
            }
        }
    }

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        VOAndVillageBoxView(
            prefRepo = viewModel.prefRepo,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            startPadding = 0.dp
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {

            SurveyHeader(
                modifier = Modifier,
                didiName = viewModel.didiName.value,
                questionCount = questionList.size,
                answeredCount = answerList.size,
                partNumber = 1
            )
            answeredQuestion.value = answerList.size
            HorizontalPager(
                pageCount = questionList.size,
                state = pagerState,
                userScrollEnabled = true
            ) {
                viewModel.findQuestionOptionList(it)
                Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Image(
                            painter = painterResource(id = R.drawable.pat_sample_icon),
                            contentDescription = "home image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .width(50.dp)
                                .height(50.dp),
                            colorFilter = ColorFilter.tint(textColorDark)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        if (questionList[it].type == QuestionType.RadioButton.name) {
                            var sortedOptionList=viewModel.optionList.value.sortedBy { it.optionValue}
                            var selectedOption=-1
                            answerList.forEach { answer->
                             if(questionList[it].questionId== answer.questionId){
                                 selectedOption= answer.optionValue ?: -1
                             }
                            }
                                RadioButtonTypeQuestion(
                                modifier = modifier,  questionNumber = (it + 1),
                                question = questionList[it].questionDisplay ?: "",selectedOption,sortedOptionList){selectedIndex->
                                viewModel.setAnswerToQuestion(
                                    didiId, questionList[it].questionId ?:0,sortedOptionList[selectedIndex]){
                                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                        if (answeredQuestion.value < (questionList.size - 1)) {
                                            answeredQuestion.value = answeredQuestion.value + 1
                                            val nextPageIndex = pagerState.currentPage + 1
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(
                                                    nextPageIndex
                                                )
                                            }
                                        } else {
                                            navigateToSummeryPage(
                                                navController,
                                                didiId,
                                                viewModel
                                            )
                                        }
                                    }, 500)
                                }
                            }
                        }else if(questionList[it].type == QuestionType.List.name){
                            ListTypeQuestion(
                                modifier = modifier,  questionNumber = (it + 1),
                                question = questionList[it].questionDisplay ?: "",viewModel.optionList.value){selectedIndex->
                                viewModel.setAnswerToQuestion(
                                    didiId, questionList[it].questionId ?:0,viewModel.optionList.value[selectedIndex]){
                                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                        if (answeredQuestion.value < (questionList.size - 1)) {
                                            answeredQuestion.value = answeredQuestion.value + 1
                                            val nextPageIndex = pagerState.currentPage + 1
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(
                                                    nextPageIndex
                                                )
                                            }
                                        } else if (answeredQuestion.value == (questionList.size - 1)) {
                                            navigateToSummeryPage(
                                                navController,
                                                didiId,
                                                viewModel
                                            )
                                        } else {
                                            navigateToSummeryPage(
                                                navController,
                                                didiId,
                                                viewModel
                                            )
                                        }
                                    }, 500)
                                }
                            }
                        }else if(questionList[it].type == QuestionType.Numeric_Field.name){
                            NumericFieldTypeQuestion(
                                modifier = modifier,  questionNumber = (it + 1),
                                question = questionList[it].questionDisplay ?: "",viewModel.optionList.value)
                        }
                    }
            }
        }

        Row(
            modifier = Modifier
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
                    (pagerState.currentPage < questionList.size - 1 && pagerState.currentPage < answerList.size)// total pages are 5
                }
            }

            AnimatedVisibility(prevButtonVisible.value) {
                Button(
                    enabled = prevButtonVisible.value,
                    onClick = {
                        val prevPageIndex = pagerState.currentPage - 1
                        coroutineScope.launch { pagerState.animateScrollToPage(prevPageIndex) }
//                        viewModel.updateAnswerOptions(prevPageIndex, didiId)
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = languageItemActiveBg
                    ),
                ) {
                    Text(text = "Q${pagerState.currentPage}", color = textColorDark)
                }
            }
            Spacer(modifier = Modifier.width(if (prevButtonVisible.value) 0.dp else ButtonDefaults.MinWidth))
            AnimatedVisibility(nextButtonVisible.value) {
                Button(
                    enabled = nextButtonVisible.value,
                    onClick = {
                        val nextPageIndex = pagerState.currentPage + 1
                        coroutineScope.launch { pagerState.animateScrollToPage(nextPageIndex) }
//                        viewModel.updateAnswerOptions(nextPageIndex, didiId)
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = languageItemActiveBg
                    ),
                ) {
                    Text(text = "Q${pagerState.currentPage + 2}", color = textColorDark)
                }
            }
        }
    }
}

fun selectAnswerOptionValue(sortedOptionList: List<AnswerOptionModel>, optionValue: Int?) :List<AnswerOptionModel>{
    sortedOptionList.forEach {
        it.isSelected = optionValue==it.optionValue
    }
    return sortedOptionList
}

fun navigateToSummeryPage(navController: NavHostController, didiId: Int,quesViewModel: QuestionScreenViewModel) {

    quesViewModel.updateDidiQuesSection(didiId,PatSurveyStatus.COMPLETED.ordinal)
    navController.navigate("pat_section_one_summary_screen/$didiId")
//    navController.navigate(Graph.HOME) {
//        popUpTo(HomeScreens.PROGRESS_SCREEN.route) {
//            inclusive = true
//        }
//    }
}

