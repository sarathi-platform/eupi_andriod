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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.navigation.home.PatScreens
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
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.setDidiDetails(didiId)
        viewModel.sectionType.value=sectionType
        viewModel.getAllQuestionsAnswers(didiId)
        delay(300)
        val mAnswerList  = viewModel.answerList.value
        val mAnsweredQuestion = mAnswerList.size
        if (mAnsweredQuestion > 0) {
            pagerState.animateScrollToPage(mAnsweredQuestion)
        }
    }

    val questionList by viewModel.questionList.collectAsState()
    /*val totalAmount by viewModel.totalAssetAmount.collectAsState()*/
    val answerList by viewModel.answerList.collectAsState()


    val answeredQuestion = remember {
        mutableStateOf(0)
    }
    val selQuesIndex = remember {
        mutableStateOf(0)
    }



    val context = LocalContext.current

    val screenHeight = LocalConfiguration.current.screenHeightDp

    BackHandler() {
        navController.popBackStack(PatScreens.PAT_LIST_SCREEN.route, inclusive = false)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(top = 14.dp)) {
        Column(
            modifier = modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            VOAndVillageBoxView(
                prefRepo = viewModel.prefRepo,
                modifier = Modifier
                    .fillMaxWidth(),
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
                    questionCount = viewModel.maxQuesCount.value,
                    answeredCount = answerList.size,
                    partNumber = if(sectionType.equals(TYPE_EXCLUSION,true)) 1 else 2,
                    viewModel = viewModel
                )
                answeredQuestion.value = answerList.size
                viewModel.findListTypeSelectedAnswer(pagerState.currentPage,didiId)
                HorizontalPager(
                    pageCount = questionList.size,
                    state = pagerState,
                    userScrollEnabled = false
                ) {
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
                                questionList[it].options?.sortedBy { it?.optionValue }
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
                                    answerOptionModel= sortedOptionList?.get(selectedIndex)!!,
                                    assetAmount = 0,
                                    quesType = QuestionType.RadioButton.name,
                                    summary = questionList[it].questionSummary?: BLANK_STRING,
                                    selIndex = selectedIndex
                                ) {
                                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                        if (answeredQuestion.value < (questionList.size)) {
                                            selQuesIndex.value=selQuesIndex.value+1
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
                                    }, 250)
                                }
                            }
                        } else if (questionList[it].type == QuestionType.List.name) {
                            ListTypeQuestion(
                                modifier = modifier,
                                questionNumber = (it + 1),
                                index = viewModel.selIndValue.collectAsState().value,
                                question = questionList[it].questionDisplay ?: "",
                                selectedIndex = viewModel.selIndValue.collectAsState().value,
                                optionList = questionList[it].options
                            ) { selectedIndex ->
                                viewModel.setAnswerToQuestion(
                                    didiId = didiId,
                                    questionId = questionList[it].questionId ?: 0,
                                    answerOptionModel= questionList[it].options[selectedIndex],
                                    assetAmount = 0,
                                    quesType = QuestionType.List.name,
                                    summary = questionList[it].questionSummary?: BLANK_STRING,
                                    selIndex = viewModel.listTypeAnswerIndex.value
                                ) {
                                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                        if (answeredQuestion.value < (questionList.size)) {
                                            selQuesIndex.value=selQuesIndex.value+1
                                            answeredQuestion.value = answeredQuestion.value + 1
                                            val nextPageIndex = pagerState.currentPage + 1
                                            viewModel.findListTypeSelectedAnswer(pagerState.currentPage,didiId)
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
                                    }, 250)
                                }
                            }
                        } else if (questionList[it].type == QuestionType.Numeric_Field.name) {
                            NumericFieldTypeQuestion(
                                modifier = modifier,
                                questionNumber = (it + 1),
                                question = questionList[it].questionDisplay ?: "",
                                didiId = didiId,
                                questionId = questionList[it].questionId ?: 0,
                                optionList = questionList[it].options,
                                viewModel = viewModel,
                                totalValueTitle = questionList[it].headingProductAssetValue?: BLANK_STRING
                            ){
                                val newAnswerOptionModel= OptionsItem( BLANK_STRING,0,0,0,
                                    BLANK_STRING)
                                viewModel.setAnswerToQuestion(
                                    didiId = didiId,
                                    questionId = questionList[it].questionId ?: 0,
                                    answerOptionModel= newAnswerOptionModel,
                                    assetAmount = viewModel.totalAmount.value,
                                    quesType = QuestionType.Numeric_Field.name,
                                    summary = (questionList[it].questionSummary?: BLANK_STRING) + " " +context.getString(R.string.total_productive_asset_value,viewModel.totalAmount.value.toString()),
                                    selIndex = -1
                                ) {
                                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                        if (answeredQuestion.value < (questionList.size)) {
                                            selQuesIndex.value=selQuesIndex.value+1
                                            answeredQuestion.value = answeredQuestion.value + 1
                                            val nextPageIndex = pagerState.currentPage + 1
                                            viewModel.findListTypeSelectedAnswer(pagerState.currentPage,didiId)
                                            coroutineScope.launch {
//                                                viewModel.calculateTotalAmount(pagerState.currentPage)
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
                                    }, 250)
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
            .padding(bottom = 25.dp)
            .align(alignment = Alignment.BottomStart)) {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(all = 16.dp)
                    .visible(prevButtonVisible.value)
                    .align(alignment = Alignment.BottomStart),
                shape = RoundedCornerShape(6.dp),
                backgroundColor = languageItemActiveBg,
                onClick = {
                    selQuesIndex.value=selQuesIndex.value-1
                    val prevPageIndex = pagerState.currentPage - 1
                    viewModel.findListTypeSelectedAnswer(pagerState.currentPage+1,didiId)
//                    viewModel.calculateTotalAmount(pagerState.currentPage+1)
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
            .padding(bottom = 25.dp)
            .align(alignment = Alignment.BottomEnd)) {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(all = 16.dp)
                    .visible(nextButtonVisible.value)
                    .align(alignment = Alignment.BottomEnd),
                shape = RoundedCornerShape(6.dp),
                backgroundColor = languageItemActiveBg,
                onClick = {
                    selQuesIndex.value=selQuesIndex.value+1
                    val nextPageIndex = pagerState.currentPage + 1
                    viewModel.findListTypeSelectedAnswer(pagerState.currentPage+1,didiId)
//                    viewModel.calculateTotalAmount(pagerState.currentPage+1)
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

fun navigateToSummeryPage(navController: NavHostController, didiId: Int,quesViewModel: QuestionScreenViewModel) {
    if(quesViewModel.sectionType.value.equals(TYPE_EXCLUSION,true))
            navController.navigate("pat_section_one_summary_screen/$didiId")
    else     navController.navigate("pat_section_two_summary_screen/$didiId")
}

