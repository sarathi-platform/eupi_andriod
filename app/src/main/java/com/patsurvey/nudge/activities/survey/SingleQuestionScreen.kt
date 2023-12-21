package com.patsurvey.nudge.activities.survey

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.CustomFloatingButton
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.navigation.home.BpcDidiListScreens
import com.patsurvey.nudge.navigation.home.PatScreens
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.ButtonArrowNegative
import com.patsurvey.nudge.utils.EXTENSION_WEBP
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QUESTION_FLAG_RATIO
import com.patsurvey.nudge.utils.QUESTION_FLAG_WEIGHT
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.TYPE_INCLUSION
import com.patsurvey.nudge.utils.getImagePath
import com.patsurvey.nudge.utils.stringToDouble
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun SingleQuestionScreen(navController: NavHostController,
                         modifier: Modifier,
                         viewModel: QuestionScreenViewModel,
                         didiId: Int,
                         sectionType:String,
                         questionIndex:Int
) {
//    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit) {
        try {
            viewModel.setDidiDetails(didiId)
            viewModel.sectionType.value=sectionType
            viewModel.getAllQuestionsAnswers(didiId)
        } catch (ex: Exception) {
            NudgeLogger.e("QuestionScreen", "LaunchedEffect -> exception", ex)
        }
    }

    val questionList by viewModel.questionList.collectAsState()
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

    val context = LocalContext.current
    BackHandler() {
        navController.popBackStack()
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(top = 14.dp)) {
        Column(
            modifier = modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier=Modifier.wrapContentWidth(Alignment.Start)) {
                if(viewModel.sectionType.value.equals(TYPE_INCLUSION,true)) {
                    ButtonArrowNegative(buttonTitle = stringResource(id = R.string.go_back_to_section_1_summary)) {
                        navigateToSummeryOnePage(navController,didiId,viewModel)
                    }
                }
            }

            VOAndVillageBoxView(
                prefRepo = viewModel.repository.prefRepo,
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
                    currentQuestion = viewModel.answerList.value.size,
                    answeredCount = answerList.size,
                    partNumber = if (sectionType.equals(TYPE_EXCLUSION, true)) 1 else 2,
                    viewModel = viewModel
                )
                answeredQuestion.value = answerList.size
                viewModel.findListTypeSelectedAnswer(questionIndex, didiId)
                eventToPageChange.value = false
                if(!questionList.isNullOrEmpty()) {
                    questionIndex?.let {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            if (questionList[it].questionImageUrl?.isNotEmpty() == true) {
                                val quesImage: File? =
                                    questionList[it].questionImageUrl?.let { it1 ->
                                        getImagePath(
                                            context,
                                            it1
                                        )
                                    }
                                if (quesImage?.extension.equals(EXTENSION_WEBP, true)) {
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
                                    selectedOptionIndex = selectedOption,
                                    optionList = sortedOptionList,
                                    isLastIndex = (it == questionList.size - 1),
                                    isAnswerSelected = viewModel.isAnswerSelected.value
                                ) { selectedIndex, nextButtonClick ->
                                    viewModel.prevButtonVisible.value = false
                                    viewModel.nextButtonVisible.value = false
                                    viewModel.isAnswerSelected.value = true
                                    viewModel.setAnswerToQuestion(
                                        didiId = didiId,
                                        questionId = questionList[it].questionId ?: 0,
                                        answerOptionModel = sortedOptionList?.get(selectedIndex)!!,
                                        assetAmount = 0.0,
                                        quesType = QuestionType.RadioButton.name,
                                        summary = questionList[it].questionSummary ?: BLANK_STRING,
                                        selIndex = selectedIndex,
                                        enteredAssetAmount = "0",
                                        questionFlag = BLANK_STRING
                                    ) {
                                        if (viewModel.repository.prefRepo.questionScreenOpenFrom() != PageFrom.DIDI_LIST_PAGE.ordinal) {
                                            if (!nextButtonClick)
                                                viewModel.updateDidiQuesSection(
                                                    didiId,
                                                    PatSurveyStatus.INPROGRESS.ordinal
                                                )
                                        }

                                        coroutineScope.launch {
                                        delay(250)
                                        navigateToSummeryPage(
                                            navController,
                                            didiId,
                                            viewModel
                                        )

                                }


                                    }
                                }
                            } else if (questionList[it].type == QuestionType.List.name) {
                                val sortedOptionList = questionList[it].options.sortedBy {it.optionValue}
                                ListTypeQuestion(
                                    modifier = modifier,
                                    questionNumber = (it + 1),
                                    index = viewModel.selIndValue.collectAsState().value,
                                    question = questionList[it].questionDisplay ?: "",
                                    selectedIndex = viewModel.selIndValue.collectAsState().value,
                                    optionList = sortedOptionList,
                                    isAnswerSelected = viewModel.isAnswerSelected.value
                                ) { selectedOptionId,selectedIndex ->
                                    viewModel.prevButtonVisible.value = false
                                    viewModel.nextButtonVisible.value = false
                                    viewModel.repository.prefRepo.saveNeedQuestionToScroll(true)
                                    viewModel.isAnswerSelected.value = true
                                    if (viewModel.repository.prefRepo.questionScreenOpenFrom() != PageFrom.DIDI_LIST_PAGE.ordinal)
                                        viewModel.updateDidiQuesSection(
                                            didiId,
                                            PatSurveyStatus.INPROGRESS.ordinal
                                        )
                                    viewModel.setAnswerToQuestion(
                                        didiId = didiId,
                                        questionId = questionList[it].questionId ?: 0,
                                        answerOptionModel =sortedOptionList[selectedIndex] /*questionList[it].options[questionList[it].options.map {it.optionId }.indexOf(selectedOptionId)]*/,
                                        assetAmount = 0.0,
                                        quesType = QuestionType.List.name,
                                        summary = questionList[it].questionSummary ?: BLANK_STRING,
                                        selIndex = viewModel.listTypeAnswerIndex.value,
                                        enteredAssetAmount = "0",
                                        questionFlag = BLANK_STRING
                                    ) {
                                         coroutineScope.launch {
                                    delay(250)
                                        navigateToSummeryPage(
                                            navController,
                                            didiId,
                                            viewModel
                                        )
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
                                    showNextButton = true,
                                    isEditPAT = true,
                                    questionFlag = questionList[it].questionFlag
                                        ?: QUESTION_FLAG_WEIGHT,
                                    totalValueTitle = questionList[it].headingProductAssetValue
                                        ?: BLANK_STRING,
                                    pagerState = null
                                ) { value ->
                                    val newAnswerOptionModel = OptionsItem(
                                        display = (if (questionList[it].questionFlag?.equals(
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
                                        questionId = questionList[it].questionId ?: 0,
                                        answerOptionModel = newAnswerOptionModel,
                                        assetAmount = if (questionList[it].questionFlag.equals(
                                                QUESTION_FLAG_RATIO, true
                                            )
                                        ) viewModel.totalAmount.value else (viewModel.totalAmount.value + stringToDouble(
                                            viewModel.enteredAmount.value
                                        )),
                                        quesType = QuestionType.Numeric_Field.name,
                                        summary = questionList[it].questionSummary ?: BLANK_STRING/*(questionList[it].questionSummary?: BLANK_STRING) + " " + if (questionList[it].questionFlag?.equals(QUESTION_FLAG_RATIO, true) == true) context.getString(R.string.total_productive_asset_value_for_ratio,viewModel.totalAmount.value.toString())
                                    else context.getString(R.string.total_productive_asset_value,(viewModel.totalAmount.value + stringToDouble(viewModel.enteredAmount.value)).toString())*/,
                                        selIndex = -1,
                                        enteredAssetAmount = if (viewModel.enteredAmount.value.isNullOrEmpty()) BLANK_STRING else viewModel.enteredAmount.value,
                                        questionFlag = questionList[it].questionFlag
                                            ?: QUESTION_FLAG_WEIGHT
                                    ) {
                                        if (value == 1) {
                                    viewModel.prevButtonVisible.value = false
                                    viewModel.nextButtonVisible.value = false
                                    viewModel.repository.prefRepo.saveNeedQuestionToScroll(true)
                                    coroutineScope.launch {
                                        delay(250)
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
//                }
                    }
                }

            }
        }

    }
}