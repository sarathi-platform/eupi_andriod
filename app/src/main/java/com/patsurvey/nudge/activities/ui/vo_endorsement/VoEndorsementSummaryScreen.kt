package com.patsurvey.nudge.activities.ui.vo_endorsement

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.smallTextStyle
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.SectionOneSummeryItem
import com.patsurvey.nudge.activities.survey.PatSummeryScreenDidiDetailBox
import com.patsurvey.nudge.activities.survey.SectionTwoSummeryItem
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.acceptEndorsementColor
import com.patsurvey.nudge.activities.ui.theme.acceptEndorsementTextColor
import com.patsurvey.nudge.activities.ui.theme.borderGrey
import com.patsurvey.nudge.activities.ui.theme.buttonTextStyle
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.redNoAnswer
import com.patsurvey.nudge.activities.ui.theme.rejectEndorsementColor
import com.patsurvey.nudge.activities.ui.theme.rejectEndorsementTextColor
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.data.prefs.SharedPrefs.Companion.PREF_KEY_VO_SUMMARY_OPEN_FROM
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.AcceptRejectButtonBox
import com.patsurvey.nudge.utils.AcceptRejectButtonBoxPreFilled
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.HUSBAND_STRING
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QUESTION_FLAG_WEIGHT
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.showDidiImageDialog
import com.patsurvey.nudge.utils.visible
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VoEndorsementSummaryScreen(
    navController: NavController,
     viewModel: VoEndorsementSummaryViewModel,
     didiId: Int,
     didiStatus:Int
){
    val answerSection1List by viewModel.answerSection1List.collectAsState()
    val answerSection2List by viewModel.answerSection2List.collectAsState()
    val quesList by viewModel.quesList.collectAsState()
    val voDidiList by viewModel.didiList.collectAsState()
    val localDensity = LocalDensity.current

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val didi = viewModel?.didiEntity?.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        voDidiList.size
    }
    val coroutineScope = rememberCoroutineScope()
    val dialogActionType = remember { mutableStateOf(DidiEndorsementStatus.ENDORSED.ordinal) }
    val selectedDidiForDialog= viewModel.selectedDidiEntity.collectAsState()


    LaunchedEffect(key1 = Unit) {
       viewModel?.setDidiDetailsFromDb(didiId)
        delay(200)
        try {
            if (viewModel.selPageIndex.value < voDidiList.size && voDidiList.isNotEmpty()) {
                pagerState.animateScrollToPage(viewModel.selPageIndex.value)
            }
        } catch (ex: Exception) {
            NudgeLogger.e("VoEndorsementSummaryScreen", "LaunchedEffect -> exception", ex)
        }

    }
    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    BackHandler {
        navController.popBackStack()
    }

    LaunchedEffect(key1 = Unit, key2 = !voDidiList.isNullOrEmpty()) {
        delay(100)
        try {
            if (viewModel.selPageIndex.value < voDidiList.size && voDidiList.isNotEmpty()) {
                pagerState.animateScrollToPage(viewModel.selPageIndex.value)
            }
        } catch (ex: Exception) {
            NudgeLogger.e("VoEndorsementSummaryScreen", "LaunchedEffect(key1 = Unit, key2 = !voDidiList.isNullOrEmpty()) -> exception", ex)
        }

    }

    if(viewModel.showDidiImageDialog.value){
        viewModel.didiEntity.value?.let {
            showDidiImageDialog(didi = it){
                viewModel.showDidiImageDialog.value = false
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
    ){

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {

            if (showDialog.value) {

                ShowAcceptRejectDialog(
                    didi = selectedDidiForDialog.value,
                    action = dialogActionType.value,
                    screenHeight = screenHeight
                )
                /*AnimatedTransitionDialog(
                didi = didi,
                action = dialogActionType.value,
                screenHeight = screenHeight,
                onDismissRequest = { showDialog.value=false })*/
            }
            val (bottomActionBox, mainBox) = createRefs()
            Box(modifier = Modifier
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .constrainAs(mainBox) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(
                        if (viewModel.repository.prefRepo.getPref(
                                PREF_KEY_VO_SUMMARY_OPEN_FROM,
                                6
                            ) == PageFrom.VO_ENDORSEMENT_LIST_PAGE.ordinal
                        ) bottomActionBox.top else parent.bottom
                    )
                    height = Dimension.fillToConstraints
                }
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                    viewModel?.repository?.prefRepo?.let {
                        VOAndVillageBoxView(
                            prefRepo = it,
                            modifier = Modifier.fillMaxWidth(),
                            startPadding = 0.dp
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.pat_survey_complete),
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


                    HorizontalPager(
                        state = pagerState,
                        userScrollEnabled = false
                    ) {
                        viewModel.getSummaryQuesList(voDidiList[pagerState.currentPage].id)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {


                            didi?.value?.let {
                                PatSummeryScreenDidiDetailBox(
                                    modifier = Modifier,
                                    screenHeight = screenHeight,
                                    didi = it
                                ){
                                    viewModel.showDidiImageDialog.value=true
                                }
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
                                    val shgFlagValue =
                                        SHGFlag.fromInt(viewModel.didiEntity.value.shgFlag).value
                                    val ableBodiedFlagValue =
                                        AbleBodiedFlag.fromInt(viewModel.didiEntity.value.ableBodiedFlag).value

                                    if (shgFlagValue != -1 && ableBodiedFlagValue != -1) {
                                        Column(modifier = Modifier.padding(bottom = dimen_5_dp)) {

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = stringResource(id = R.string.shg_member_text),
                                                    style = smallTextStyle.copy(
                                                        textColorDark
                                                    )
                                                )
                                                Spacer(modifier = Modifier.weight(1.0f))
                                                Text(
                                                    modifier = Modifier.padding(end = dimen_5_dp),
                                                    text = SHGFlag.fromInt(shgFlagValue).toString(),
                                                    style = smallTextStyle.copy(
                                                        if (shgFlagValue == 1) textColorDark else redNoAnswer
                                                    )
                                                )
                                            }
                                            Divider(
                                                color = borderGrey,
                                                thickness = 1.dp,
                                                modifier = Modifier
                                                    .padding(
                                                        vertical = 10.dp
                                                    )
                                                    .fillMaxWidth()
                                            )

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = stringResource(id = R.string.able_bodied_women_text),
                                                    style = smallTextStyle.copy(
                                                        textColorDark
                                                    )
                                                )
                                                Spacer(modifier = Modifier.weight(1.0f))
                                                Text(
                                                    modifier = Modifier.padding(end = dimen_5_dp),
                                                    text = AbleBodiedFlag.fromInt(
                                                        ableBodiedFlagValue
                                                    )
                                                        .toString(),
                                                    style = smallTextStyle.copy(
                                                        if (ableBodiedFlagValue == 1) textColorDark else redNoAnswer
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
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
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(10.dp)
                                    )
                                }
                                itemsIndexed(answerSection1List) { index, answer ->
                                    SectionOneSummeryItem(
                                        index = index,
                                        answerValue = answer?.questionId?.let {
                                            answer.optionId?.let { it1 ->
                                                viewModel.getOptionForLanguage(
                                                    it, it1, BLANK_STRING
                                                )
                                            }
                                        } ?: BLANK_STRING,
                                        quesSummery = answer?.questionId?.let {
                                            viewModel.getQuestionSummary(
                                                it
                                            )
                                        }
                                            ?: BLANK_STRING,
                                        isArrowVisible = false,
                                        optionValue = answer.optionValue ?: 0,
                                        questionImageUrl = answer.questionImageUrl?: BLANK_STRING
                                    ){

                                    }
                                }
                                if (answerSection2List.isNotEmpty()) {
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
                                        Spacer(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(10.dp)
                                        )
                                    }

                                    itemsIndexed(answerSection2List) { index, answer ->
                                        val question = quesList.find { it.questionId == answer.questionId }
                                        SectionTwoSummeryItem(
                                            index = index,
                                            quesSummery = answer.questionId.let {
                                                viewModel.getQuestionSummary(
                                                    it
                                                )
                                            },
                                            answerValue = answer.questionId.let {
                                                answer.optionId?.let { it1 ->
                                                    viewModel.getOptionForLanguage(
                                                        it, it1,answer.answerValue?:"0"
                                                    )
                                                }
                                            } ?: BLANK_STRING,
                                            questionType = answer.type,
                                            isArrowVisible = false,
                                            isSummaryEnable = true,
                                            questionImageUrl=question?.questionImageUrl?: BLANK_STRING,
                                            questionFlag = answer.questionFlag ?: QUESTION_FLAG_WEIGHT
                                        ){

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

//            if(didiStatus == DidiEndorsementStatus.NO_STARTED.ordinal) {
            AnimatedVisibility(visible = didi?.value?.voEndorsementStatus == DidiEndorsementStatus.NOT_STARTED.ordinal, enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier
                    .constrainAs(bottomActionBox) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .onGloballyPositioned { coordinates ->
                        bottomPadding = with(localDensity) {
                            coordinates.size.height.toDp()
                        }
                    }
            ) {
                AcceptRejectButtonBox(
                    modifier = Modifier
//                        .visible(didiStatus == DidiEndorsementStatus.NO_STARTED.ordinal)
                        /*.constrainAs(bottomActionBox) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                        .onGloballyPositioned { coordinates ->
                            bottomPadding = with(localDensity) {
                                coordinates.size.height.toDp()
                            }
                        }*/,

                    positiveButtonText = stringResource(id = R.string.endorse),
                    negativeButtonText = stringResource(id = R.string.reject),
                    negativeButtonRequired = true,
                    positiveButtonOnClick = {
                        showDialog.value = true
                        dialogActionType.value = DidiEndorsementStatus.ENDORSED.ordinal
                        viewModel._selectedDidiEntity.value = voDidiList[pagerState.currentPage]
                        coroutineScope.launch {
                            viewModel.updateVoEndorsementStatus(
                                voDidiList[pagerState.currentPage].id,
                                DidiEndorsementStatus.ENDORSED.ordinal
                            )
                            val nextPageIndex = pagerState.currentPage + 1
                            if (nextPageIndex < voDidiList.size) {
                                viewModel.updateDidiDetailsForBox(voDidiList[nextPageIndex].id)
                                delay(500)
                                showDialog.value = false
                                delay(100)
                                pagerState.animateScrollToPage(nextPageIndex)
                            } else {
                                delay(500)
                                showDialog.value = false
                                delay(100)
                                navController.popBackStack()
                            }
                        }
                    },
                    negativeButtonOnClick = {
                        showDialog.value = true
                        dialogActionType.value = DidiEndorsementStatus.REJECTED.ordinal
                        viewModel._selectedDidiEntity.value = voDidiList[pagerState.currentPage]
                        coroutineScope.launch {
                            viewModel.updateVoEndorsementStatus(
                                voDidiList[pagerState.currentPage].id,
                                DidiEndorsementStatus.REJECTED.ordinal
                            )
                            val nextPageIndex = pagerState.currentPage + 1
                            if (nextPageIndex < voDidiList.size) {
                                viewModel.updateDidiDetailsForBox(voDidiList[nextPageIndex].id)
                                delay(500)
                                showDialog.value = false
                                delay(100)

                                pagerState.animateScrollToPage(nextPageIndex)
                            } else {
                                delay(500)
                                showDialog.value = false
                                delay(100)
                                navController.popBackStack()
                            }
                        }
                    }
                )
            }

            AnimatedVisibility(visible = (didi?.value?.voEndorsementStatus != DidiEndorsementStatus.NOT_STARTED.ordinal || didiStatus != DidiEndorsementStatus.NOT_STARTED.ordinal)
                    && viewModel.voEndorsementStatus.value != StepStatus.COMPLETED.ordinal && didi?.value?.voEndorsementEdit ?: true, enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier
                    .constrainAs(bottomActionBox) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .onGloballyPositioned { coordinates ->
                        bottomPadding = with(localDensity) {
                            coordinates.size.height.toDp()
                        }
                    }
            ) {
                AcceptRejectButtonBoxPreFilled(
                    modifier = Modifier
//                        .visible(didiStatus == DidiEndorsementStatus.NO_STARTED.ordinal)
                    /*.constrainAs(bottomActionBox) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .onGloballyPositioned { coordinates ->
                        bottomPadding = with(localDensity) {
                            coordinates.size.height.toDp()
                        }
                    }*/,

                    positiveButtonText = stringResource(id = R.string.endorse),
                    negativeButtonText = stringResource(id = R.string.reject),
                    negativeButtonRequired = true,
                    preFilledValue = didi?.value?.voEndorsementStatus ?: 0,
                    positiveButtonOnClick = {
                        showDialog.value = true
                        dialogActionType.value = DidiEndorsementStatus.ENDORSED.ordinal
                        viewModel._selectedDidiEntity.value = voDidiList[pagerState.currentPage]
                        didi?.value?.voEndorsementStatus = DidiEndorsementStatus.ENDORSED.ordinal
                        viewModel.updateVoEndorsementStatus(voDidiList[pagerState.currentPage].id, DidiEndorsementStatus.ENDORSED.ordinal)
                        coroutineScope.launch {
//                            showDialog.value = false
                            if (viewModel.repository.prefRepo.getPref(PREF_KEY_VO_SUMMARY_OPEN_FROM,6) == PageFrom.VO_ENDORSEMENT_LIST_PAGE.ordinal) {
                                val nextPageIndex = pagerState.currentPage + 1
                                if (nextPageIndex < voDidiList.size) {
                                    viewModel.updateDidiDetailsForBox(voDidiList[nextPageIndex].id)
                                    delay(500)
                                    showDialog.value = false
                                    delay(100)
                                    pagerState.animateScrollToPage(nextPageIndex)
                                } else {
                                    delay(500)
                                    showDialog.value = false
                                    delay(100)
                                    navController.popBackStack()
                                }
                            } else {
                                delay(1000)
                                navController.popBackStack()
                            }
                        }
                    },
                    negativeButtonOnClick = {
                        showDialog.value = true
                        dialogActionType.value = DidiEndorsementStatus.REJECTED.ordinal
                        viewModel._selectedDidiEntity.value = voDidiList[pagerState.currentPage]
                        didi?.value?.voEndorsementStatus = DidiEndorsementStatus.REJECTED.ordinal
                        viewModel.updateVoEndorsementStatus(voDidiList[pagerState.currentPage].id, DidiEndorsementStatus.REJECTED.ordinal)
                        coroutineScope.launch {
//                            showDialog.value = false
                            if (viewModel.repository.prefRepo.getPref(PREF_KEY_VO_SUMMARY_OPEN_FROM, 6) == PageFrom.VO_ENDORSEMENT_LIST_PAGE.ordinal) {
                                val nextPageIndex = pagerState.currentPage + 1
                                if (nextPageIndex < voDidiList.size) {
                                    viewModel.updateDidiDetailsForBox(voDidiList[nextPageIndex].id)
                                    delay(500)
                                    showDialog.value = false
                                    delay(100)
                                    pagerState.animateScrollToPage(nextPageIndex)
                                } else {
                                    delay(500)
                                    showDialog.value = false
                                    delay(100)
                                    navController.popBackStack()
                                }
                            } else {
                                delay(1000)
                                navController.popBackStack()
                            }
                        }
                    }
                )
            }

        }

        val prevButtonVisible = remember {
            derivedStateOf {
                pagerState.currentPage > 0
            }
        }
        val nextButtonVisible = remember {
            derivedStateOf {
                pagerState.currentPage < voDidiList.size-1
            }
        }

        AnimatedVisibility(visible = (prevButtonVisible.value && viewModel.repository.prefRepo.getPref(PREF_KEY_VO_SUMMARY_OPEN_FROM, 6) == PageFrom.VO_ENDORSEMENT_LIST_PAGE.ordinal), modifier = Modifier
            .padding(end = 5.dp)
            .padding(top = 200.dp)
            .visible(prevButtonVisible.value)
            .align(alignment = Alignment.TopStart)) {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(all = 5.dp)
                    .visible(prevButtonVisible.value)
                    .align(alignment = Alignment.CenterStart),
                shape = RoundedCornerShape(6.dp),
                backgroundColor = languageItemActiveBg,
                onClick = {
                    val prevPageIndex = pagerState.currentPage - 1
                    coroutineScope.launch { pagerState.animateScrollToPage(prevPageIndex)
                        delay(100)}
                    viewModel.updateDidiDetailsForBox(voDidiList[prevPageIndex].id)
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

                },
            )
        }
        AnimatedVisibility(visible = ( nextButtonVisible.value && viewModel.repository.prefRepo.getPref(PREF_KEY_VO_SUMMARY_OPEN_FROM, 6) == PageFrom.VO_ENDORSEMENT_LIST_PAGE.ordinal), modifier = Modifier
            .padding(end = 5.dp)
            .padding(top = 200.dp)
            .visible(nextButtonVisible.value)
            .align(alignment = Alignment.TopEnd)) {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(all = 5.dp)
                    .visible(nextButtonVisible.value)
                    .align(alignment = Alignment.CenterEnd),
                shape = RoundedCornerShape(6.dp),
                backgroundColor = languageItemActiveBg,
                onClick = {

                    val nextPageIndex = pagerState.currentPage + 1
                    coroutineScope.launch { pagerState.animateScrollToPage(nextPageIndex)
                    delay(100)
                    }
                    viewModel.updateDidiDetailsForBox(voDidiList[nextPageIndex].id)

                },
                text = {
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

@Preview(showBackground = true)
@Composable
fun VoEndorsementSummaryScreenPreview(){
//    VoEndorsementSummaryScreen(didiId = 281)
}

@Preview(showBackground = true)
@Composable
fun ShowAcceptRejectDialogPreview(){
    val didi = DidiEntity(
        id = 0,
        name = "Didi Name",
        guardianName = "Dada Name",
        address = BLANK_STRING,
        castId = 1,
        castName = "SC",
        cohortId = 1,
        cohortName = "Tola",
        relationship = HUSBAND_STRING,
        villageId = 1,
        createdDate = 0,
        modifiedDate = System.currentTimeMillis(),
        shgFlag = 0,
        patSurveyStatus = PatSurveyStatus.COMPLETED.ordinal,
        section1Status = PatSurveyStatus.COMPLETED.ordinal,
        section2Status = PatSurveyStatus.COMPLETED.ordinal,
        ableBodiedFlag = AbleBodiedFlag.NOT_MARKED.value
        )
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    ShowAcceptRejectDialog(didi,DidiEndorsementStatus.ENDORSED.ordinal,screenHeight)
}

@Composable
fun ShowAcceptRejectDialog(
    didi:DidiEntity,
    action:Int,
    screenHeight:Int,
) {
    Dialog(onDismissRequest = { /*setShowDialog(false)*/ },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress=true
        ),
    ) {
        Surface(modifier = Modifier.fillMaxSize(),
            color = Color.White,
        ) {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .padding(14.dp)
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PatSummeryScreenDidiDetailBox(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .background(
                                if (action == DidiEndorsementStatus.REJECTED.ordinal)
                                    rejectEndorsementColor else acceptEndorsementColor
                            ),
                        screenHeight = screenHeight,
                        didi = didi
                    ){ }
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(15.dp))
                    Text(
                        text = stringResource(id = if(action == DidiEndorsementStatus.REJECTED.ordinal)
                            R.string.rejected else R.string.endorsed),
                        style = TextStyle(
                            color = if(action == DidiEndorsementStatus.REJECTED.ordinal)
                                rejectEndorsementTextColor else acceptEndorsementTextColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )


                }
            }
        }
//      }
    }
}
