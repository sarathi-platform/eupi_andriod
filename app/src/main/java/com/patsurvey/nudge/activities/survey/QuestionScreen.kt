package com.patsurvey.nudge.activities.survey

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.navigation.home.BpcDidiListScreens
import com.patsurvey.nudge.navigation.home.PatScreens
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.EXTENSION_WEBP
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QUESTION_FLAG_RATIO
import com.patsurvey.nudge.utils.QUESTION_FLAG_WEIGHT
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.getImagePath
import com.patsurvey.nudge.utils.singleClick
import com.patsurvey.nudge.utils.stringToDouble
import com.patsurvey.nudge.utils.visible
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("SuspiciousIndentation", "StateFlowValueCalledInComposition",
    "CoroutineCreationDuringComposition"
)
@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun QuestionScreen(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: QuestionScreenViewModel,
    didiId: Int,
    sectionType:String,
    questionIndex:Int
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit) {
        try {
            viewModel.setDidiDetails(didiId)
            viewModel.sectionType.value=sectionType
            viewModel.getAllQuestionsAnswers(didiId)
            delay(300)
            val mAnswerList  = viewModel.answerList.value
            val mAnsweredQuestion = mAnswerList.size
            if (mAnsweredQuestion > 0 && !mAnswerList.isNullOrEmpty()) {
                viewModel.isAnswerSelected.value=false
                viewModel.prefRepo.saveNeedQuestionToScroll(true)
                pagerState.animateScrollToPage(mAnsweredQuestion)
            }
        } catch (ex: Exception) {
            NudgeLogger.e("QuestionScreen", "LaunchedEffect -> exception", ex)
        }

        if(viewModel.prefRepo.questionScreenOpenFrom() == PageFrom.SUMMARY_PAGE.ordinal
            || viewModel.prefRepo.questionScreenOpenFrom() == PageFrom.SUMMARY_ONE_PAGE.ordinal
            || viewModel.prefRepo.questionScreenOpenFrom() == PageFrom.SUMMARY_TWO_PAGE.ordinal){
            pagerState.animateScrollToPage(questionIndex)
            viewModel.prefRepo.saveNeedQuestionToScroll(true)
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

    val eventToPageChange = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = Unit, key2 = !questionList.isNullOrEmpty()) {
        try {
            delay(100)
            val mAnswerList  = viewModel.answerList.value
            val mAnsweredQuestion = mAnswerList.size
            if (mAnsweredQuestion > 0 && !mAnswerList.isNullOrEmpty()) {
                viewModel.isAnswerSelected.value=false
                if (pagerState.currentPage != mAnsweredQuestion) {
                    viewModel.prevButtonVisible.value=true
                    pagerState.animateScrollToPage(mAnsweredQuestion)
                }
            }
        } catch (ex: Exception) {
            NudgeLogger.e("QuestionScreen", "LaunchedEffect(key1 = Unit, key2 = !questionList.isNullOrEmpty())  -> exception", ex)
        }
    }


    val context = LocalContext.current
    BackHandler() {
        if (viewModel.prefRepo.questionScreenOpenFrom() == PageFrom.DIDI_LIST_PAGE.ordinal) {
            if (viewModel.prefRepo.isUserBPC()) {
                navController.popBackStack(
                    BpcDidiListScreens.BPC_DIDI_LIST.route,
                    inclusive = false
                )
            } else {
                navController.popBackStack(PatScreens.PAT_LIST_SCREEN.route, inclusive = false)
            }
        } else {
            navController.popBackStack(PatScreens.PAT_LIST_SCREEN.route, inclusive = false)
        }
    }


    if(eventToPageChange.value){
        if(pagerState.currentPage == questionList.size-1){
            viewModel.nextButtonVisible.value=false
        }else viewModel.nextButtonVisible.value = viewModel.isClickEnable.value &&  (pagerState.currentPage < questionList.size - 1 && pagerState.currentPage < answerList.size)

       viewModel.prevButtonVisible.value= pagerState.currentPage > 0
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
                    .padding(horizontal = 5.dp)
            ) {

                SurveyHeader(
                    modifier = Modifier,
                    didiName = viewModel.didiName.value,
                    questionCount = viewModel.maxQuesCount.value,
                    currentQuestion = pagerState.currentPage + 1,
                    answeredCount = answerList.size,
                    partNumber = if(sectionType.equals(TYPE_EXCLUSION,true)) 1 else 2,
                    viewModel = viewModel
                )
                answeredQuestion.value = answerList.size
                viewModel.findListTypeSelectedAnswer(pagerState.currentPage,didiId)
                eventToPageChange.value=false
               HorizontalPager(
                    pageCount = questionList.size,
                    state = pagerState,
                    userScrollEnabled = false
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        if (questionList[it].questionImageUrl?.isNotEmpty() == true){
                        val quesImage: File? =
                            questionList[it].questionImageUrl?.let { it1 ->
                                getImagePath(
                                    context,
                                    it1
                                )
                            }
                        if (quesImage?.extension.equals(EXTENSION_WEBP, true)) {
                            GlideImage(
                                model = quesImage,
                                contentDescription = "Question Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .width(/*dimensionResource(id = R.dimen.ques_image_width)*/60.dp)
                                    .height(/*dimensionResource(id = R.dimen.ques_image_width)*/60.dp),
                            )
                        } else {
                            if (quesImage?.exists() == true) {
                                GlideImage(
                                    model = quesImage,
                                    contentDescription = "Question Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .width(/*dimensionResource(id = R.dimen.ques_image_width)*/
                                            60.dp
                                        )
                                        .height(/*dimensionResource(id = R.dimen.ques_image_width)*/
                                            60.dp
                                        ),
                                )
                            }
                        }
                    }
                        Spacer(modifier = Modifier.height(10.dp))
                        if (questionList[it].type == QuestionType.RadioButton.name) {
                            val sortedOptionList =
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
                                selectedOptionIndex= selectedOption,
                                optionList = sortedOptionList,
                                isLastIndex = (it == questionList.size-1),
                                isAnswerSelected = viewModel.isAnswerSelected.value
                            ) { selectedIndex,nextButtonClick ->
                                viewModel.prevButtonVisible.value=false
                                viewModel.nextButtonVisible.value=false
                                viewModel.isAnswerSelected.value =true
                                viewModel.setAnswerToQuestion(
                                    didiId = didiId,
                                    questionId = questionList[it].questionId ?: 0,
                                    answerOptionModel= sortedOptionList?.get(selectedIndex)!!,
                                    assetAmount = 0.0,
                                    quesType = QuestionType.RadioButton.name,
                                    summary = questionList[it].questionSummary?: BLANK_STRING,
                                    selIndex = selectedIndex,
                                    enteredAssetAmount = "0",
                                    questionFlag = BLANK_STRING
                                ) {
                                    if(viewModel.prefRepo.questionScreenOpenFrom() != PageFrom.DIDI_LIST_PAGE.ordinal){
                                        if(!nextButtonClick)
                                                viewModel.updateDidiQuesSection(didiId, PatSurveyStatus.INPROGRESS.ordinal)
                                    }


                                    coroutineScope.launch {
                                        delay(250)
                                        if (answeredQuestion.value < (questionList.size)) {
                                            selQuesIndex.value=selQuesIndex.value+1
                                            answeredQuestion.value = answeredQuestion.value + 1
                                            val nextPageIndex = pagerState.currentPage + 1
                                            coroutineScope.launch {
                                                delay(150)
                                                eventToPageChange.value = true
                                            }
                                                pagerState.animateScrollToPage(
                                                    nextPageIndex
                                                )
                                                viewModel.isAnswerSelected.value=false


                                        } else {
                                            navigateToSummeryPage(
                                                navController,
                                                didiId,
                                                viewModel
                                            )
                                        }
                                    }


                                }
                            }
                        } else if (questionList[it].type == QuestionType.List.name) {
                            ListTypeQuestion(
                                modifier = modifier,
                                questionNumber = (it + 1),
                                index = viewModel.selIndValue.collectAsState().value,
                                question = questionList[it].questionDisplay ?: "",
                                selectedIndex = viewModel.selIndValue.collectAsState().value,
                                optionList = questionList[it].options,
                                isAnswerSelected = viewModel.isAnswerSelected.value
                            ) { selectedIndex ->
                                viewModel.prevButtonVisible.value=false
                                viewModel.nextButtonVisible.value=false
                                viewModel.prefRepo.saveNeedQuestionToScroll(true)
                                viewModel.isAnswerSelected.value=true
                                if(viewModel.prefRepo.questionScreenOpenFrom() != PageFrom.DIDI_LIST_PAGE.ordinal)
                                    viewModel.updateDidiQuesSection(didiId, PatSurveyStatus.INPROGRESS.ordinal)
                                viewModel.setAnswerToQuestion(
                                    didiId = didiId,
                                    questionId = questionList[it].questionId ?: 0,
                                    answerOptionModel= questionList[it].options[selectedIndex],
                                    assetAmount = 0.0,
                                    quesType = QuestionType.List.name,
                                    summary = questionList[it].questionSummary?: BLANK_STRING,
                                    selIndex = viewModel.listTypeAnswerIndex.value,
                                    enteredAssetAmount = "0",
                                    questionFlag = BLANK_STRING
                                ) {
                                    coroutineScope.launch {
                                        delay(250)
                                        if (answeredQuestion.value < (questionList.size)) {
                                            selQuesIndex.value=selQuesIndex.value+1
                                            answeredQuestion.value = answeredQuestion.value + 1
                                            val nextPageIndex = pagerState.currentPage + 1
                                            viewModel.findListTypeSelectedAnswer(pagerState.currentPage,didiId)
                                                pagerState.animateScrollToPage(
                                                    nextPageIndex
                                                )
                                                viewModel.isAnswerSelected.value=false
                                            eventToPageChange.value = true
                                        } else {
                                            navigateToSummeryPage(
                                                navController,
                                                didiId,
                                                viewModel
                                            )
                                        }
                                    }
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
                                showNextButton = (viewModel.prevButtonVisible.value && !viewModel.nextButtonVisible.value ) ,
                                questionFlag=questionList[it].questionFlag?:QUESTION_FLAG_WEIGHT,
                                totalValueTitle = questionList[it].headingProductAssetValue?: BLANK_STRING
                            ){ value->
                                val newAnswerOptionModel= OptionsItem( display = (if (questionList[it].questionFlag?.equals(QUESTION_FLAG_RATIO, true) == true) viewModel.totalAmount.value.toString()
                                else (viewModel.totalAmount.value + stringToDouble(viewModel.enteredAmount.value)).toString()),0,0,0,
                                    BLANK_STRING)
                                viewModel.setAnswerToQuestion(
                                    didiId = didiId,
                                    questionId = questionList[it].questionId ?: 0,
                                    answerOptionModel= newAnswerOptionModel,
                                    assetAmount = if(questionList[it].questionFlag.equals(
                                            QUESTION_FLAG_RATIO,true))viewModel.totalAmount.value else  (viewModel.totalAmount.value + stringToDouble(viewModel.enteredAmount.value)),
                                    quesType = QuestionType.Numeric_Field.name,
                                    summary = questionList[it].questionSummary?: BLANK_STRING/*(questionList[it].questionSummary?: BLANK_STRING) + " " + if (questionList[it].questionFlag?.equals(QUESTION_FLAG_RATIO, true) == true) context.getString(R.string.total_productive_asset_value_for_ratio,viewModel.totalAmount.value.toString())
                                    else context.getString(R.string.total_productive_asset_value,(viewModel.totalAmount.value + stringToDouble(viewModel.enteredAmount.value)).toString())*/,
                                    selIndex = -1,
                                    enteredAssetAmount = if(viewModel.enteredAmount.value.isNullOrEmpty()) BLANK_STRING else viewModel.enteredAmount.value,
                                    questionFlag = questionList[it].questionFlag ?: QUESTION_FLAG_WEIGHT
                                ) {
                                    if(value == 1) {
                                        viewModel.prevButtonVisible.value=false
                                        viewModel.nextButtonVisible.value=false
                                        viewModel.prefRepo.saveNeedQuestionToScroll(true)
                                        coroutineScope.launch {
                                            delay(250)
                                            if (answeredQuestion.value < (questionList.size)) {
                                                selQuesIndex.value = selQuesIndex.value + 1
                                                answeredQuestion.value = answeredQuestion.value + 1
                                                val nextPageIndex = pagerState.currentPage + 1
                                                viewModel.findListTypeSelectedAnswer(
                                                    pagerState.currentPage,
                                                    didiId
                                                )
                                                    pagerState.animateScrollToPage(
                                                        nextPageIndex
                                                    )
                                                viewModel.isQuestionChange.value=true
                                                eventToPageChange.value = true

                                            } else {
                                                navigateToSummeryPage(
                                                    navController,
                                                    didiId,
                                                    viewModel
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }



        }

        //Previous Ques Button

        if(viewModel.prevButtonVisible.value){
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(all = 16.dp)
                    .visible(viewModel.prevButtonVisible.value)
                    .align(alignment = Alignment.BottomStart)
                    .shadow(0.dp)
                    .padding(bottom = 40.dp),
                shape = RoundedCornerShape(6.dp),
                backgroundColor = languageItemActiveBg,
                onClick = singleClick {
                    viewModel.prevButtonVisible.value=false
                    viewModel.nextButtonVisible.value=false
                    viewModel.prefRepo.saveNeedQuestionToScroll(true)
                    viewModel.isAnswerSelected.value=false
                    selQuesIndex.value=selQuesIndex.value-1
                    val prevPageIndex = pagerState.currentPage - 1
                    viewModel.findListTypeSelectedAnswer(pagerState.currentPage-1,didiId)
                    if (questionList[pagerState.currentPage].type == QuestionType.Numeric_Field.name
                    /*&& questionList[pagerState.currentPage].questionFlag.equals(
                        QUESTION_FLAG_WEIGHT,true)*/){
                        val newAnswerOptionModel= OptionsItem( display = (if (questionList[pagerState.currentPage].questionFlag?.equals(QUESTION_FLAG_RATIO, true) == true) viewModel.totalAmount.value.toString()
                        else (viewModel.totalAmount.value + stringToDouble(viewModel.enteredAmount.value)).toString()),0,0,0,
                            BLANK_STRING)
                        viewModel.setAnswerToQuestion(
                            didiId =didiId,
                            questionId = questionList[pagerState.currentPage].questionId ?: 0,
                            answerOptionModel = newAnswerOptionModel,
                            assetAmount = if(questionList[pagerState.currentPage].questionFlag.equals(
                                    QUESTION_FLAG_RATIO,true))viewModel.totalAmount.value else  (viewModel.totalAmount.value + viewModel.enteredAmount.value.toDouble()),
                            quesType = QuestionType.Numeric_Field.name,
                            summary = questionList[pagerState.currentPage].questionSummary?: BLANK_STRING  /*(questionList[pagerState.currentPage].questionSummary?: BLANK_STRING) + " " + if (questionList[pagerState.currentPage].questionFlag?.equals(QUESTION_FLAG_RATIO, true) == true) context.getString(R.string.total_productive_asset_value_for_ratio,viewModel.totalAmount.value.toString())
                            else context.getString(R.string.total_productive_asset_value,(viewModel.totalAmount.value + stringToDouble(viewModel.enteredAmount.value)).toString())*/,
                            selIndex = -1,
                            enteredAssetAmount = if(viewModel.enteredAmount.value.isNullOrEmpty()) "0" else viewModel.enteredAmount.value,
                            questionFlag = questionList[pagerState.currentPage].questionFlag ?: QUESTION_FLAG_WEIGHT
                        ) {
                            coroutineScope.launch {
                                delay(300)
                                eventToPageChange.value=true
                            }
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(prevPageIndex)
                            }
                        }
                    }else{
                        coroutineScope.launch {
                            delay(300)
                            eventToPageChange.value=true
                        }
                        coroutineScope.launch { pagerState.animateScrollToPage(prevPageIndex) }
                    }
                },
                text = {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_arrow_back),
                        contentDescription = "Negative Button",
                        modifier = Modifier
                            .height(20.dp)
                            .absolutePadding(top = 2.dp),
                        colorFilter = ColorFilter.tint(blueDark)
                    )

                    Text(text = "Q${pagerState.currentPage}",
                        color = blueDark,
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
        if(viewModel.nextButtonVisible.value){
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(all = 16.dp)
                    .visible(viewModel.nextButtonVisible.value)
                    .align(alignment = Alignment.BottomEnd)
                    .shadow(0.dp)
                    .padding(bottom = 40.dp),
                shape = RoundedCornerShape(6.dp),
                backgroundColor = languageItemActiveBg,
                onClick = singleClick{
                    if(viewModel.isClickEnable.value) {
                        viewModel.prevButtonVisible.value = false
                        viewModel.nextButtonVisible.value = false
                        viewModel.prefRepo.saveNeedQuestionToScroll(true)
                        viewModel.isAnswerSelected.value = false
                        selQuesIndex.value = selQuesIndex.value + 1
                        val nextPageIndex = pagerState.currentPage + 1
                        viewModel.findListTypeSelectedAnswer(pagerState.currentPage + 1, didiId)
                        if (questionList[pagerState.currentPage].type == QuestionType.Numeric_Field.name) {
                            val newAnswerOptionModel = OptionsItem(
                                display = (if (questionList[pagerState.currentPage].questionFlag?.equals(
                                        QUESTION_FLAG_RATIO,
                                        true
                                    ) == true
                                ) viewModel.totalAmount.value.toString()
                                else (viewModel.totalAmount.value + stringToDouble(viewModel.enteredAmount.value)).toString()),
                                0,
                                0,
                                0,
                                BLANK_STRING
                            )
                            viewModel.setAnswerToQuestion(
                                didiId = didiId,
                                questionId = questionList[pagerState.currentPage].questionId ?: 0,
                                answerOptionModel = newAnswerOptionModel,
                                assetAmount = if (questionList[pagerState.currentPage].questionFlag.equals(
                                        QUESTION_FLAG_RATIO, true
                                    )
                                ) viewModel.totalAmount.value else (viewModel.totalAmount.value + viewModel.enteredAmount.value.toDouble()),
                                quesType = QuestionType.Numeric_Field.name,
                                summary = questionList[pagerState.currentPage].questionSummary
                                    ?: BLANK_STRING  /*(questionList[pagerState.currentPage].questionSummary?: BLANK_STRING) + " " + if (questionList[pagerState.currentPage].questionFlag?.equals(QUESTION_FLAG_RATIO, true) == true) context.getString(R.string.total_productive_asset_value_for_ratio,viewModel.totalAmount.value.toString())
                            else context.getString(R.string.total_productive_asset_value,(viewModel.totalAmount.value + stringToDouble(viewModel.enteredAmount.value)).toString())*/,
                                selIndex = -1,
                                enteredAssetAmount = if (viewModel.enteredAmount.value.isNullOrEmpty()) "0" else viewModel.enteredAmount.value,
                                questionFlag = questionList[pagerState.currentPage].questionFlag
                                    ?: QUESTION_FLAG_WEIGHT
                            ) {
                                coroutineScope.launch {
                                    delay(300)
                                    eventToPageChange.value=true
                                }
                                coroutineScope.launch { pagerState.animateScrollToPage(nextPageIndex) }
                            }
                        } else {
                            coroutineScope.launch {
                                delay(300)
                                eventToPageChange.value=true
                            }
                            coroutineScope.launch { pagerState.animateScrollToPage(nextPageIndex) }
                        }
                    }
                },
                text = {
                    Text(text = "Q${if((pagerState.currentPage + 2)<= questionList.size)(pagerState.currentPage + 2) else (pagerState.currentPage + 1)}",
                        color = blueDark,
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
                        colorFilter = ColorFilter.tint(blueDark)
                    )

                },
            )
        }
        /*AnimatedVisibility(visible =  viewModel.nextButtonVisible.value, modifier = Modifier
            .padding(all = 16.dp)
            .visible(viewModel.nextButtonVisible.value)
            .padding(bottom = 25.dp)
            .align(alignment = Alignment.BottomEnd)) {
            viewModel.nextCTAVisibility.value=viewModel.nextButtonVisible.value

        }*/
    }
}

fun navigateToSummeryPage(navController: NavHostController, didiId: Int,quesViewModel: QuestionScreenViewModel) {
    if(quesViewModel.sectionType.value.equals(TYPE_EXCLUSION,true)){
        if(quesViewModel.prefRepo.isUserBPC())
            navController.navigate("bpc_pat_section_one_summary_screen/$didiId")
        else navController.navigate("pat_section_one_summary_screen/$didiId")
    }else{
        if(quesViewModel.prefRepo.isUserBPC())
            navController.navigate("bpc_pat_section_two_summary_screen/$didiId")
         else  navController.navigate("pat_section_two_summary_screen/$didiId")

    }
}

