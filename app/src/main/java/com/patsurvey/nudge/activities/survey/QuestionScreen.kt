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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.model.dataModel.AnswerOptionModel
import com.patsurvey.nudge.navigation.home.HomeScreens
import com.patsurvey.nudge.navigation.home.PatScreens
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.*
import kotlinx.coroutines.delay
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

    LaunchedEffect(key1 = true) {
        delay(200)
        val mAnsweredQuestion = answerList.size
        if (mAnsweredQuestion > 0) {
            pagerState.animateScrollToPage(mAnsweredQuestion + 1)
        } else if (mAnsweredQuestion == questionList.size) {
            pagerState.animateScrollToPage(mAnsweredQuestion)
        }
    }

    val context = LocalContext.current
    BackHandler() {
        navController.popBackStack(PatScreens.PAT_LIST_SCREEN.route, inclusive = false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    partNumber = if(sectionType.equals(TYPE_EXCLUSION,true)) 1 else 2
                )
                answeredQuestion.value = answerList.size
                HorizontalPager(
                    pageCount = questionList.size,
                    state = pagerState,
                    userScrollEnabled = true
                ) {
                    viewModel.findQuestionOptionList(pagerState.currentPage)
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
                            var sortedOptionList =
                                viewModel.optionList.value.sortedBy { it.optionValue }
                            var selectedOption = -1
                            answerList.forEach { answer ->
                                if (questionList[it].questionId == answer.questionId) {
                                    selectedOption = answer.optionValue ?: -1
                                }
                            }
                            RadioButtonTypeQuestion(
                                modifier = modifier,
                                questionNumber = (it + 1),
                                question = questionList[it].questionDisplay ?: "",
                                selectedOption,
                                sortedOptionList
                            ) { selectedIndex ->
                                viewModel.setAnswerToQuestion(
                                    didiId = didiId,
                                    questionId = questionList[it].questionId ?: 0,
                                    answerOptionModel= sortedOptionList[selectedIndex],
                                    assetAmount = 0,
                                    quesType = QuestionType.RadioButton.name,
                                    summary = viewModel.optionList.value[selectedIndex].summary?: BLANK_STRING
                                ) {
                                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                        if (answeredQuestion.value < (questionList.size)) {
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
                        } else if (questionList[it].type == QuestionType.List.name) {
                            var selIndex=-1
                            Log.d("TAG", "QuestionScreenData $${Gson().toJson(answerList)}")
                            answerList.forEachIndexed{ansIndex,answerEntity->

                                if(answerEntity.questionId==questionList[it].questionId){
                                    selIndex=ansIndex
                                }
                            }
                            Log.d("TAG", "QuestionScreen Size: ${answerList.size}")
                            ListTypeQuestion(
                                modifier = modifier,
                                questionNumber = (it + 1),
                                index = selIndex,
                                question = questionList[it].questionDisplay ?: "",
                                optionList = viewModel.optionList.value
                            ) { selectedIndex ->
                                Log.d("TAG", "QuestionScreen Index: $selectedIndex")
                                viewModel.setAnswerToQuestion(
                                    didiId = didiId,
                                    questionId = questionList[it].questionId ?: 0,
                                    answerOptionModel= viewModel.optionList.value[selectedIndex],
                                    assetAmount = 0,
                                    quesType = QuestionType.List.name,
                                    summary = viewModel.optionList.value[selectedIndex].summary?: BLANK_STRING
                                ) {
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
                        } else if (questionList[it].type == QuestionType.Numeric_Field.name) {
                            viewModel.totalAssetAmount.value=0
                            NumericFieldTypeQuestion(
                                modifier = modifier,
                                questionNumber = (it + 1),
                                question = questionList[it].questionDisplay ?: "",
                                didiId = didiId,
                                questionId = questionList[it].questionId ?: 0,
                                optionList = viewModel.optionList.value,
                                viewModel = viewModel
                            ){
                                val newAnswerOptionModel=AnswerOptionModel(0, BLANK_STRING,false,0,
                                    BLANK_STRING,0)
                                viewModel.setAnswerToQuestion(
                                    didiId = didiId,
                                    questionId = questionList[it].questionId ?: 0,
                                    answerOptionModel= newAnswerOptionModel,
                                    assetAmount = viewModel.totalAssetAmount.value,
                                    quesType = QuestionType.Numeric_Field.name,
                                    summary = context.getString(R.string.total_productive_asset_value,viewModel.totalAssetAmount.value.toString())
                                ) {
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
                        }
                    }
                }
            }



        }

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
        //Previous Ques Button
        AnimatedVisibility(visible = prevButtonVisible.value, modifier = Modifier
            .padding(all = 16.dp)
            .visible(prevButtonVisible.value)
            .align(alignment = Alignment.BottomStart)) {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(all = 16.dp)
                    .visible(prevButtonVisible.value)
                    .align(alignment = Alignment.BottomStart),
                shape = RoundedCornerShape(6.dp),
                backgroundColor = languageItemActiveBg,
                onClick = {
                    val prevPageIndex = pagerState.currentPage - 1
                    coroutineScope.launch { pagerState.animateScrollToPage(prevPageIndex) }
                },
                text = {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_arrow_back),
                        contentDescription = "Negative Button",
                        modifier = Modifier
                            .height(20.dp)
                            .absolutePadding(top = 2.dp),
                        colorFilter = ColorFilter.tint(textColorDark)
                    )

                    Text(text = "Q${pagerState.currentPage}",
                        color = textColorDark,
                        style = TextStyle(
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Start
                        ) )

                },
            )
        }


        //Next Ques Button
        AnimatedVisibility(visible = nextButtonVisible.value, modifier = Modifier
            .padding(all = 16.dp)
            .visible(nextButtonVisible.value)
            .align(alignment = Alignment.BottomEnd)) {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(all = 16.dp)
                    .visible(nextButtonVisible.value)
                    .align(alignment = Alignment.BottomEnd),
                shape = RoundedCornerShape(6.dp),
                backgroundColor = languageItemActiveBg,
                onClick = {
                    val nextPageIndex = pagerState.currentPage + 1
                    coroutineScope.launch { pagerState.animateScrollToPage(nextPageIndex) }
                },
                text = {
                    Text(text = "Q${pagerState.currentPage + 2}",
                        color = textColorDark,
                        style = TextStyle(
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Start
                        ) )
                    Image(
                        painter = painterResource(id = R.drawable.baseline_arrow_forward_ios_24),
                        contentDescription = "Negative Button",
                        modifier = Modifier
                            .height(20.dp)
                            .absolutePadding(top = 2.dp),
                        colorFilter = ColorFilter.tint(textColorDark)
                    )

                },
            )
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
    if(quesViewModel.sectionType.value.equals(TYPE_EXCLUSION,true))
            navController.navigate("pat_section_one_summary_screen/$didiId")
    else     navController.navigate("pat_section_two_summary_screen/$didiId")
//    navController.navigate(Graph.HOME) {
//        popUpTo(HomeScreens.PROGRESS_SCREEN.route) {
//            inclusive = true
//        }
//    }
}

