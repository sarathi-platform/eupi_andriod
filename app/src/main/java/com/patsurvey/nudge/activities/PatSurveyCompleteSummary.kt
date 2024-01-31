package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.survey.PatSummeryScreenDidiDetailBox
import com.patsurvey.nudge.activities.survey.SectionTwoSummeryItem
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.buttonTextStyle
import com.patsurvey.nudge.activities.ui.theme.redDark
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.navigation.home.BpcDidiListScreens
import com.patsurvey.nudge.navigation.home.PatScreens
import com.patsurvey.nudge.utils.ARG_FROM_PAT_SUMMARY_SCREEN
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.ExclusionType
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.QUESTION_FLAG_WEIGHT
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.TYPE_INCLUSION
import com.patsurvey.nudge.utils.showDidiImageDialog

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PatSurveyCompleteSummary(
    navController: NavHostController,
    modifier: Modifier,
    patSectionSummaryViewModel: PatSectionSummaryViewModel,
    didiId: Int,
    fromScreen: String
) {
    LaunchedEffect(key1 = true) {
        patSectionSummaryViewModel.updatePATEditAndStepStatus(didiId)
        patSectionSummaryViewModel.setDidiDetailsFromDb(didiId)
        patSectionSummaryViewModel.getQuestionAnswerListForSectionOne(didiId)
    }

    BackHandler() {
        if(patSectionSummaryViewModel.patSectionRepository.prefRepo.isUserBPC()){
            if (fromScreen == ARG_FROM_PAT_SUMMARY_SCREEN)
                navController.popBackStack()
            else
                navController.popBackStack(BpcDidiListScreens.BPC_DIDI_LIST.route, inclusive = false)
        } else {
            if (fromScreen == ARG_FROM_PAT_SUMMARY_SCREEN)
                navController.popBackStack()
            else
                navController.popBackStack(PatScreens.PAT_LIST_SCREEN.route, inclusive = false)
        }
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    val localDensity = LocalDensity.current

    val context = LocalContext.current

    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }
    val didi = patSectionSummaryViewModel.didiEntity.collectAsState()
    val answerSummeryList by patSectionSummaryViewModel.answerSummeryList.collectAsState()

    val questionList by patSectionSummaryViewModel.questionList.collectAsState()
    val inclusionQuestionList by patSectionSummaryViewModel.inclusionQuestionList.collectAsState()
    val answerList by patSectionSummaryViewModel.answerList.collectAsState()

    LaunchedEffect(key1 = Unit) {
        questionList.forEach {
            val answer = answerList.find { it.questionId == it.questionId }
        }
    }

    if(patSectionSummaryViewModel.showDidiImageDialog.value){
        patSectionSummaryViewModel.didiEntity.value?.let {
            showDidiImageDialog(didi = it){
                patSectionSummaryViewModel.showDidiImageDialog.value = false
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier)
    ) {
        val (bottomActionBox, mainBox) = createRefs()


        Box(modifier = Modifier
            .constrainAs(mainBox) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(bottomActionBox.top)
                height = Dimension.fillToConstraints
            }
            .padding(top = 14.dp)
        ) {

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                VOAndVillageBoxView(
                    prefRepo = patSectionSummaryViewModel.patSectionRepository.prefRepo,
                    modifier = Modifier.fillMaxWidth(),
                    startPadding = 0.dp
                )
                Text(
                    text = if (patSectionSummaryViewModel.patSectionRepository.prefRepo.isUserBPC()) stringResource(id = R.string.bpc_pat_survey_complete) else stringResource(
                        id = R.string.pat_survey_complete
                    ),
                    modifier = Modifier
                        .layoutId("sectionText"),
                    color = textColorDark,
                    style = buttonTextStyle.copy(lineHeight = 22.sp)
                )
                Text(
                    text = stringResource(id = R.string.summary_text),
                    modifier = Modifier
                        .layoutId("sectionText"),
                    color = textColorDark,
                    style = buttonTextStyle.copy(lineHeight = 22.sp)
                )

                Spacer(
                    modifier = Modifier
                        .height(10.dp)
                        .fillMaxWidth()
                )

                PatSummeryScreenDidiDetailBox(
                    modifier = Modifier,
                    screenHeight = screenHeight,
                    didi = didi.value
                ){
                    patSectionSummaryViewModel.showDidiImageDialog.value=true
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.section_1_text),
                            style = TextStyle(
                                color = textColorDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = NotoSans
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item { Spacer(modifier = Modifier.height(4.dp)) }

                    itemsIndexed(questionList.sortedBy { it.order }) { index, question ->
                        val answer = answerList.find { it.questionId == question.questionId }
                        SectionOneSummeryItem(index = index,
                            quesSummery = answer?.questionId?.let {
                                patSectionSummaryViewModel.getQuestionSummary(
                                    it
                                )
                            } ?: BLANK_STRING,
                            answerValue = answer?.questionId?.let {
                                answer.optionId?.let { it1 ->
                                    patSectionSummaryViewModel.getOptionForLanguage(
                                        it, it1, BLANK_STRING
                                    )
                                }
                            } ?: BLANK_STRING,
                            optionValue =  answer?.optionValue?:0,
                            isArrowVisible = isArrowVisible(patSectionSummaryViewModel,didi),
                            questionImageUrl =question.questionImageUrl?: BLANK_STRING ){
                            if ((patSectionSummaryViewModel.patSectionRepository.prefRepo.isUserBPC() && patSectionSummaryViewModel.isBPCVerificationStepComplete.value == StepStatus.INPROGRESS.ordinal)
                                || patSectionSummaryViewModel.isPATStepComplete.value == StepStatus.INPROGRESS.ordinal) {
                                patSectionSummaryViewModel.patSectionRepository.prefRepo.saveQuestionScreenOpenFrom(
                                    PageFrom.SUMMARY_PAGE.ordinal
                                )
                                if(patSectionSummaryViewModel.patSectionRepository.prefRepo.isUserBPC())
                                    navController.navigate("bpc_single_question_screen/${didiId}/$TYPE_EXCLUSION/$index")
                                else navController.navigate("single_question_screen/${didiId}/$TYPE_EXCLUSION/$it")
                            }
                        }
                    }

                    if(didi.value.patExclusionStatus != ExclusionType.NO_EXCLUSION.ordinal) {
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_error_outline_24),
                                    contentDescription = "Negative Button",
                                    tint = redDark,
                                    modifier = Modifier
                                        .absolutePadding(top = 2.dp, right = 10.dp)
                                )
                                Text(
                                    text = stringResource(R.string.exclusion_error_message),
                                    style = TextStyle(
                                        color = redDark,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = NotoSans
                                    ),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    if (answerSummeryList.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.section_2_text),
                                style = TextStyle(
                                    color = textColorDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = NotoSans
                                ),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item { Spacer(modifier = Modifier.height(4.dp)) }
                        itemsIndexed(answerSummeryList) { index, answer ->
                            val question = inclusionQuestionList.find { it.questionId == answer.questionId }
                            SectionTwoSummeryItem(
                                index = index,
                                quesSummery = answer.questionId.let {
                                    patSectionSummaryViewModel.getQuestionSummary(
                                        it
                                    )
                                },
                                answerValue = answer.questionId.let {
                                    answer.optionId?.let { it1 ->
                                        patSectionSummaryViewModel.getOptionForLanguage(
                                            it, it1,answer.answerValue?:"0"
                                        )
                                    }
                                } ?: BLANK_STRING,
                                questionType = answer?.type?: QuestionType.List.name,
                                isSummaryEnable = didi.value.patExclusionStatus != ExclusionType.EDIT_PAT_EXCLUSION.ordinal,
                                isArrowVisible = isArrowVisible(patSectionSummaryViewModel,didi),
                                questionImageUrl=question?.questionImageUrl?: BLANK_STRING,
                                questionFlag = answer?.questionFlag ?: QUESTION_FLAG_WEIGHT
                            ){
                                if(patSectionSummaryViewModel.isPATStepComplete.value == StepStatus.INPROGRESS.ordinal) {
                                    patSectionSummaryViewModel.patSectionRepository.prefRepo.saveQuestionScreenOpenFrom(
                                        PageFrom.SUMMARY_PAGE.ordinal
                                    )
                                    if(patSectionSummaryViewModel.patSectionRepository.prefRepo.isUserBPC())
                                        navController.navigate("bpc_single_question_screen/${didiId}/$TYPE_INCLUSION/$index")
                                    else navController.navigate("single_question_screen/${didiId}/$TYPE_INCLUSION/$it")
                                }
                            }
                        }
                    }
                }
            }
        }


        DoubleButtonBox(
            modifier = Modifier
                .constrainAs(bottomActionBox) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
                .onGloballyPositioned { coordinates ->
                    bottomPadding = with(localDensity) {
                        coordinates.size.height.toDp()
                    }
                },

            positiveButtonText = stringResource(id = R.string.done_text),
            negativeButtonRequired = false,
            positiveButtonOnClick = {
                if (fromScreen != ARG_FROM_PAT_SUMMARY_SCREEN) {
                    patSectionSummaryViewModel.writePatEvents()
                }
                if(patSectionSummaryViewModel.patSectionRepository.prefRepo.isUserBPC()){
                    if (fromScreen == ARG_FROM_PAT_SUMMARY_SCREEN)
                        navController.popBackStack()
                    else
                        navController.popBackStack(BpcDidiListScreens.BPC_DIDI_LIST.route, inclusive = false)
                }else{
                    if (fromScreen == ARG_FROM_PAT_SUMMARY_SCREEN) {
                        (context as MainActivity).isBackFromSummary.value = true
                        navController.popBackStack()
                    } else {
                        navController.popBackStack(PatScreens.PAT_LIST_SCREEN.route, inclusive = false)
                    }
                }
            },
            negativeButtonOnClick = {/*Nothing to do here*/ }
        )

    }

}