package com.patsurvey.nudge.activities.ui.bpc.progress_screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.*
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BpcProgressScreen(
    modifier: Modifier = Modifier,
    bpcProgreesScreenViewModel: BpcProgressScreenViewModel,
    navController: NavHostController,
    onNavigateToStep:(Int, Int) ->Unit,
    onNavigateToSetting:()->Unit
) {

    LaunchedEffect(key1 = Unit) {
        bpcProgreesScreenViewModel.init()
        delay(1000)
        bpcProgreesScreenViewModel.showLoader.value = false
    }

    val scaffoldState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = false)
    val scope = rememberCoroutineScope()

//    val steps by bpcProgreesScreenViewModel.stepList.collectAsState()
    val villages by bpcProgreesScreenViewModel.villageList.collectAsState()
    val steps by bpcProgreesScreenViewModel.stepList.collectAsState()


    val summaryData = bpcProgreesScreenViewModel.summaryData.collectAsState()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    val mainActivity = LocalContext.current as? MainActivity
    mainActivity?.isLoggedInLive?.postValue(bpcProgreesScreenViewModel.isLoggedIn())

    setKeyboardToPan(mainActivity!!)

    LaunchedEffect(key1 = Unit) {
//        bpcProgreesScreenViewModel.updateSelectedDidiPatStatus()
//        delay(100)
        bpcProgreesScreenViewModel.addDidisToDidiDaoIfNeeded()
    }
    val context = LocalContext.current

    val localDensity = LocalDensity.current
    val voTextHeight = remember {
        mutableStateOf(Offset(0f, 0f))
    }

    LaunchedEffect(key1 = true) {
        bpcProgreesScreenViewModel.setBpcVerificationCompleteForVillages()
    }

    BackHandler {
        (context as? Activity)?.finish()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        ModalBottomSheetLayout(
            sheetContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 50.dp)
                        .height(((2 * screenHeight) / 3).dp)
                ) {
                    Text(
                        text = stringResource(R.string.seletc_village_screen_text),
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = textColorDark,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                        itemsIndexed(villages) { index, village ->
                            VillageAndVoBoxForBottomSheet(
                                tolaName = village.name,
                                voName = village.federationName,
                                index = index,
                                selectedIndex = bpcProgreesScreenViewModel.villageSelected.value,
                                isBpcUser = true,
                                isVoEndorsementComplete = bpcProgreesScreenViewModel.isBpcVerificationComplete.value[village.id] ?: false
                            ) {
                                bpcProgreesScreenViewModel.showLoader.value = true
                                bpcProgreesScreenViewModel.villageSelected.value = it/*
                                bpcProgreesScreenViewModel.getStepsList(village.id)
                                bpcProgreesScreenViewModel.findInProgressStep(villageId = village.id)*/
                                bpcProgreesScreenViewModel.fetchBpcSummaryData(village.id)
                                bpcProgreesScreenViewModel.updateSelectedVillage(village)
                                bpcProgreesScreenViewModel.selectedText.value = bpcProgreesScreenViewModel.villageList.value[it].name
                                scope.launch {
                                    scaffoldState.hide()
                                    delay(1000)
                                    bpcProgreesScreenViewModel.showLoader.value = false
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                }
            },
            sheetState = scaffoldState,
            sheetElevation = 20.dp,
            sheetBackgroundColor = Color.White,
            sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        ) {
            Scaffold(
                modifier = Modifier,
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                                Icon(painter = painterResource(id = R.drawable.sarathi_logo_mini), contentDescription = "app bar icon", tint = textColorDark,modifier= Modifier.size(26.dp))
                                Spacer(modifier = Modifier.size(5.dp))
                                Text(
                                    text = "SARATHI",
                                    color = textColorDark,
                                    fontFamily = NotoSans,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                bpcProgreesScreenViewModel.prefRepo.savePref(PREF_OPEN_FROM_HOME, true)
                                bpcProgreesScreenViewModel.prefRepo.saveSettingOpenFrom(PageFrom.HOME_PAGE.ordinal)
                                onNavigateToSetting()
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.more_icon),
                                    contentDescription = "more action button",
                                    tint = blueDark,
                                    modifier = Modifier
                                        .padding(10.dp)
                                )
                            }
                        },
                        backgroundColor = Color.White
                    )
//                    ProgressScreenTopBar() {
//                        bpcProgreesScreenViewModel.prefRepo.savePref(PREF_OPEN_FROM_HOME, true)
//                        onNavigateToSetting()
//                    }
                }
            ) { it ->
                if (bpcProgreesScreenViewModel.showLoader.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(top = it.calculateTopPadding() + 30.dp)
                    ) {
                        CircularProgressIndicator(
                            color = blueDark,
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.Center)
                        )
                    }
                } else {

                    var isStepCompleted =
                        if (!steps.isNullOrEmpty()) {
                            bpcProgreesScreenViewModel.isStepComplete(
                                steps.sortedBy { it.orderNumber }.last().id,
                                bpcProgreesScreenViewModel.prefRepo.getSelectedVillage().id
                            )?.observeAsState()?.value ?: 0
                        } else {
                            1
                        }


                    Column(modifier = Modifier) {

                        LazyColumn(
                            Modifier
                                .background(Color.White)
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = it.calculateTopPadding(),
                                    bottom = 50.dp
                                ),
                        ) {

                            item {
                                UserDataView(
                                    modifier = Modifier,
                                    name = bpcProgreesScreenViewModel.prefRepo.getPref(
                                        PREF_KEY_NAME,
                                        BLANK_STRING
                                    ) ?: "",
                                    identity = bpcProgreesScreenViewModel.prefRepo.getPref(
                                        PREF_KEY_IDENTITY_NUMBER,
                                        BLANK_STRING
                                    ) ?: ""
                                )
                            }

                            item {
                                VillageSelectorDropDown(selectedText = bpcProgreesScreenViewModel.selectedText.value) {
                                    scope.launch {
                                        if (!scaffoldState.isVisible) {
                                            scaffoldState.show()
                                        } else {
                                            scaffoldState.hide()
                                        }
                                    }
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(white, shape = RoundedCornerShape(6.dp))
                                        .border(
                                            width = 1.dp,
                                            shape = RoundedCornerShape(6.dp),
                                            color = borderGrey
                                        )
                                ) {
                                    Column(modifier = Modifier) {
                                        Text(
                                            text = stringResource(R.string.crp_group_name_text),
                                            textAlign = TextAlign.Start,
                                            style = mediumTextStyle,
                                            color = textColorDark,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                        )

                                        Divider(
                                            thickness = 1.dp,
                                            color = borderGrey
                                        )

                                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(
                                                        style = SpanStyle(
                                                            color = textColorDark,
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontFamily = NotoSans
                                                        )
                                                    ) {


                                                        append(String.format("%03d", summaryData.value.cohortCount))
                                                    }
                                                    withStyle(
                                                        style = SpanStyle(
                                                            color = textColorDark,
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.Normal,
                                                            fontFamily = NotoSans
                                                        )
                                                    ) {
                                                        append(
                                                            if (summaryData.value.cohortCount > 1)
                                                                stringResource(R.string.summary_tolas_added_text_plural)
                                                            else
                                                                stringResource(R.string.summary_tolas_added_text_singular)
                                                        )
                                                    }
                                                },
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )
                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(
                                                        style = SpanStyle(
                                                            color = textColorDark,
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontFamily = NotoSans
                                                        )
                                                    ) {
                                                        append(String.format("%03d", summaryData.value.mobilisedCount))
                                                    }
                                                    withStyle(
                                                        style = SpanStyle(
                                                            color = textColorDark,
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.Normal,
                                                            fontFamily = NotoSans
                                                        )
                                                    ) {
                                                        append(
                                                            if (summaryData.value.mobilisedCount > 1)
                                                                stringResource(R.string.summary_didis_mobilised_text_plural)
                                                            else
                                                                stringResource(R.string.summary_didis_mobilised_text_singular)
                                                        )
                                                    }
                                                },
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )
                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(
                                                        style = SpanStyle(
                                                            color = textColorDark,
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontFamily = NotoSans
                                                        )
                                                    ) {
                                                        append(String.format("%03d", summaryData.value.poorDidiCount))
                                                    }
                                                    withStyle(
                                                        style = SpanStyle(
                                                            color = textColorDark,
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.Normal,
                                                            fontFamily = NotoSans
                                                        )
                                                    ) {
                                                        append(
                                                            if (summaryData.value.poorDidiCount > 1)
                                                                stringResource(R.string.summary_wealth_ranking_text_plural)
                                                            else
                                                                stringResource(R.string.summary_wealth_ranking_text_singular)
                                                        )
                                                    }
                                                },
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )
                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(
                                                        style = SpanStyle(
                                                            color = textColorDark,
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontFamily = NotoSans
                                                        )
                                                    ) {
                                                        append(String.format("%03d", summaryData.value.sentVoEndorsementCount))
                                                    }
                                                    withStyle(
                                                        style = SpanStyle(
                                                            color = textColorDark,
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.Normal,
                                                            fontFamily = NotoSans
                                                        )
                                                    ) {
                                                        append(
                                                            if (summaryData.value.sentVoEndorsementCount > 1)
                                                                stringResource(R.string.summary_vo_endoresement_text_plural)
                                                            else
                                                                stringResource(R.string.summary_vo_endoresement_text_singular)
                                                        )
                                                    }
                                                },
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )
                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(
                                                        style = SpanStyle(
                                                            color = textColorDark,
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontFamily = NotoSans
                                                        )
                                                    ) {
                                                        append(String.format("%03d", summaryData.value.voEndorsedCount))
                                                    }
                                                    withStyle(
                                                        style = SpanStyle(
                                                            color = textColorDark,
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.Normal,
                                                            fontFamily = NotoSans
                                                        )
                                                    ) {
                                                        append(
                                                            if (summaryData.value.voEndorsedCount > 1)
                                                                stringResource(R.string.didis_endorsed_by_vo_plural)
                                                            else stringResource(
                                                                R.string.didi_endorsed_by_vo_singular)
                                                        )
                                                    }
                                                },
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )

                                            Text(
                                                text = stringResource(R.string.current_status_text),
                                                style = buttonTextStyle,
                                                color = textColorDark
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Box(modifier = Modifier) {
                                                    Divider(
                                                        modifier = Modifier
                                                            .height(1.dp)
                                                            .align(Alignment.Center)
                                                            .padding(horizontal = 16.dp)
                                                            .background(
                                                                greyBorder,
                                                                shape = DottedShape(step = 4.dp)
                                                            )
                                                    )
                                                    Row(
                                                        modifier = Modifier
                                                            .align(Alignment.Center)
                                                            .fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceEvenly
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(CircleShape)
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .background(
                                                                    greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .padding(6.dp)
                                                                .size(24.dp)
                                                                .aspectRatio(1f),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "1",
                                                                color = white,
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier
                                                                    .align(Alignment.Center)
                                                                    .absolutePadding(bottom = 3.dp),
                                                                style = smallerTextStyleNormalWeight,
                                                            )
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(CircleShape)
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .background(
                                                                    greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .padding(6.dp)
                                                                .size(24.dp)
                                                                .aspectRatio(1f),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "2",
                                                                color = white,
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier
                                                                    .align(Alignment.Center)
                                                                    .absolutePadding(bottom = 3.dp),
                                                                style = smallerTextStyleNormalWeight,
                                                            )
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(CircleShape)
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .background(
                                                                    greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .padding(6.dp)
                                                                .size(24.dp)
                                                                .aspectRatio(1f),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "3",
                                                                color = white,
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier
                                                                    .align(Alignment.Center)
                                                                    .absolutePadding(bottom = 3.dp),
                                                                style = smallerTextStyleNormalWeight,
                                                            )
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(CircleShape)
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .background(
                                                                    greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .padding(6.dp)
                                                                .size(24.dp)
                                                                .aspectRatio(1f),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "4",
                                                                color = white,
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier
                                                                    .align(Alignment.Center)
                                                                    .absolutePadding(bottom = 3.dp),
                                                                style = smallerTextStyleNormalWeight,
                                                            )
                                                        }

                                                        Box(
                                                            modifier = Modifier
                                                                .clip(CircleShape)
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .background(
                                                                    greenOnline,
                                                                    shape = CircleShape
                                                                )
                                                                .padding(6.dp)
                                                                .size(24.dp)
                                                                .aspectRatio(1f),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "5",
                                                                color = white,
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier
                                                                    .align(Alignment.Center)
                                                                    .absolutePadding(bottom = 3.dp),
                                                                style = smallerTextStyleNormalWeight,
                                                            )
                                                        }

                                                        Box(
                                                            modifier = Modifier
                                                                .clip(CircleShape)
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = if (isStepCompleted == StepStatus.COMPLETED.ordinal) greenOnline else stepBoxActiveColor,
                                                                    shape = CircleShape
                                                                )
                                                                .background(
                                                                    if (isStepCompleted == StepStatus.COMPLETED.ordinal) greenOnline else stepBoxActiveColor,
                                                                    shape = CircleShape
                                                                )
                                                                .padding(6.dp)
                                                                .size(24.dp)
                                                                .aspectRatio(1f),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "6",
                                                                color = if (isStepCompleted == StepStatus.COMPLETED.ordinal) white else textColorDark,
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier
                                                                    .align(Alignment.Center)
                                                                    .absolutePadding(bottom = 3.dp),
                                                                style = smallerTextStyleNormalWeight,
                                                            )
                                                        }
                                                    }
                                                }
                                                if (isStepCompleted != StepStatus.COMPLETED.ordinal) {
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Text(
                                                        text = stringResource(id = R.string.vo_endorsement_completed),
                                                        fontSize = 12.sp,
                                                        fontFamily = NotoSans,
                                                        fontWeight = FontWeight.Medium,
                                                        color = textColorDark,
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.padding(start = (3 * 48).dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(16.dp))
                                        }
                                    }
                                }
                            }

                            if (isStepCompleted == StepStatus.COMPLETED.ordinal) {
                                bpcProgreesScreenViewModel.getBpcCompletedDidiCount()
                            }

                            bpcProgreesScreenViewModel.isBpcVerificationComplete.value[bpcProgreesScreenViewModel.prefRepo.getSelectedVillage().id] = isStepCompleted == StepStatus.COMPLETED.ordinal

                            item {
                                StepsBoxForBpc(
                                    boxTitle = stringResource(id = R.string.step_bpc_verification),
                                    stepNo = 6,
                                    index = 1,
                                    iconId = 6,
                                    viewModel = bpcProgreesScreenViewModel,
                                    shouldBeActive = isStepCompleted == StepStatus.INPROGRESS.ordinal || isStepCompleted == StepStatus.COMPLETED.ordinal,
                                    isCompleted = isStepCompleted == StepStatus.COMPLETED.ordinal,
                                    onclick = {
                                        if ((context as MainActivity).isOnline.value ?: false) {
                                            bpcProgreesScreenViewModel.callWorkFlowApiToGetWorkFlowId()
                                        }
//                                        bpcProgreesScreenViewModel.prefRepo.savePref(PREF_NEED_TO_POST_BPC_MATCH_SCORE_FOR_ + bpcProgreesScreenViewModel.prefRepo.getSelectedVillage().id, false)
                                        if (isStepCompleted == StepStatus.INPROGRESS.ordinal || isStepCompleted == StepStatus.COMPLETED.ordinal) {
                                            if (!steps.isNullOrEmpty()) {
                                                val stepId =
                                                    steps.sortedBy { it.orderNumber }.last().id
                                                onNavigateToStep(
                                                    bpcProgreesScreenViewModel.prefRepo.getSelectedVillage().id,
                                                    stepId,
                                                )
                                            } else {
                                                showCustomToast(context, "Something went wrong!")
                                            }
                                        }
                                    }
                                )
                            }

                            /*itemsIndexed(items = steps.sortedBy { it.orderNumber }) { index, step ->
                                if ((bpcProgreesScreenViewModel.prefRepo.getPref(PREF_PROGRAM_NAME, "")
                                        ?: "").equals("CRP Program", true) && index < 5
                                ) {
                                    val villageId=villages[bpcProgreesScreenViewModel.villageSelected.value].id
                                    var isStepCompleted =
                                        bpcProgreesScreenViewModel.isStepComplete(steps[index].id,villageId).observeAsState().value
                                            ?: 0
                                    if (index == 4){
                                        bpcProgreesScreenViewModel.isVoEndorsementComplete.value[villageId] = isStepCompleted == StepStatus.COMPLETED.ordinal
                                    }
                                    if(steps[index].orderNumber==1 && isStepCompleted==0){
                                        isStepCompleted= StepStatus.INPROGRESS.ordinal
                                    }
                                    if (isStepCompleted == StepStatus.COMPLETED.ordinal) {
                                        bpcProgreesScreenViewModel.updateSelectedStep(steps[index].stepId)
                                    }
                                    StepsBox(
                                        boxTitle = step.name,
                                        stepNo = step.orderNumber,
                                        index = index,
                                        iconId = step.orderNumber,
                                        bpcProgreesScreenViewModel = bpcProgreesScreenViewModel,
                                        shouldBeActive = isStepCompleted == StepStatus.INPROGRESS.ordinal || isStepCompleted == StepStatus.COMPLETED.ordinal,
                                        isCompleted = isStepCompleted == StepStatus.COMPLETED.ordinal
                                    ) { index ->
                                        bpcProgreesScreenViewModel.stepSelected.value = index
                                        val step=bpcProgreesScreenViewModel.stepList.value[index]
                                        bpcProgreesScreenViewModel.prefRepo.saveFromPage(ARG_FROM_PROGRESS)
                                        if (mainActivity?.isOnline?.value == true) {
                                            bpcProgreesScreenViewModel.callWorkFlowAPI(villageId,step.id,step.programId)
                                        }
                                        when (index) {
                                            0 -> {
//                                            onNavigateToTransWalk(villageId,stepId,index)
                                            }
                                            1 -> {
//                                            onNavigateToTransWalk(villageId,stepId,index)
                                            }
                                            2 -> {}
                                            3 -> {
                                                bpcProgreesScreenViewModel.prefRepo.saveFromPage(ARG_FROM_PAT_SURVEY)
                                            }
                                            4 -> {}
                                            5 -> {}
                                        }
                                        onNavigateToStep(villageId,step.id,index)
                                    }
                                }
                            }*/
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                        Spacer(
                            modifier = Modifier
                                .height(100.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun StepsBoxForBpc(
    modifier: Modifier = Modifier,
    boxTitle: String,
    stepNo: Int,
    index: Int,
    iconId: Int,
    viewModel: BpcProgressScreenViewModel,
    isCompleted: Boolean = false,
    shouldBeActive: Boolean = false,
    onclick: (Int) -> Unit
) {
    val dividerMargins = 32.dp
    if (stepNo == 6)
        Spacer(modifier = Modifier.height(20.dp))

    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .background(Color.White)
            .border(
                width = 0.dp,
                color = Color.Transparent,
            )
            .then(modifier)
    ) {
        val (step_no, stepBox, divider1, divider2) = createRefs()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = if (isCompleted) greenOnline else greyBorder,
                    shape = RoundedCornerShape(6.dp)
                )
                .background(Color.White)
                .clickable {
                    if (!viewModel.stepList.value.isNullOrEmpty()) {
                        onclick(index)
                    } else {
                        showCustomToast(context, "Something went wrong!") //Confirm this message.
                    }
                }
                .constrainAs(stepBox) {
                    start.linkTo(parent.start)
                    top.linkTo(step_no.bottom, margin = -16.dp)
                }

        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isCompleted) greenLight else if (shouldBeActive) stepBoxActiveColor else white)
                    .padding(vertical = /*if (isCompleted) 10.dp else */14.dp)
                    .padding(end = 16.dp, start = 8.dp),
            ) {
                val (textContainer, buttonContainer, iconContainer) = createRefs()
                val iconResourceId = R.drawable.bpc_verification_icon
                if (iconResourceId != null) {
                    Icon(
                        painter = painterResource(id = iconResourceId),
                        contentDescription = null,
                        tint = if (shouldBeActive) {
                            if (isCompleted) stepIconCompleted else stepIconEnableColor
                        } else stepIconDisableColor,
                        modifier = Modifier
                            .constrainAs(iconContainer) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                            .size(48.dp)
                            .padding(
                                top = if (isCompleted) 0.dp else 6.dp,
                                start = if (isCompleted) 0.dp else 4.dp
                            )
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 14.dp)
                        .constrainAs(textContainer) {
                            top.linkTo(iconContainer.top)
                            start.linkTo(iconContainer.end)
                            bottom.linkTo(iconContainer.bottom)
                            end.linkTo(buttonContainer.start)
                            width = Dimension.fillToConstraints
                        }
                        .fillMaxWidth()
                ) {
                    Text(
                        text = if (boxTitle.contains("pat ", true)) boxTitle.replace(
                            "pat ",
                            "PAT ",
                            true
                        ) else boxTitle,
                        color = if (isCompleted) greenOnline else textColorDark,
                        modifier = Modifier
                            .padding(
                                top = 10.dp,
                                bottom = if (isCompleted) 0.dp else 10.dp,
                                end = 10.dp
                            )
                            .fillMaxWidth(),
                        softWrap = true,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = buttonTextStyle
                    )
                    if (isCompleted) {
//                        Spacer(modifier = Modifier.height(4.dp))
                        //TODO add string for other steps when steps is complete.
                        val count = viewModel.bpcCompletedDidiCount.value
                        val subText = if (count > 1) stringResource(R.string.ultra_poor_didis_verified_text_plural, count) else stringResource(R.string.ultra_poor_didis_verified_text_singular, count)
                        if (subText != null || subText != "") {
                            Text(
                                text = subText,
                                color = if (isCompleted) greenOnline else textColorDark,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                    .fillMaxWidth(),
                                softWrap = true,
                                textAlign = TextAlign.Start,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 2,
                                style = smallerTextStyle
                            )
                        }
                    }
                }

                if (shouldBeActive) {
                    if (isCompleted) {
                        TextButtonWithIcon(modifier = Modifier
                            .constrainAs(buttonContainer) {
                                bottom.linkTo(textContainer.bottom)
                                top.linkTo(textContainer.top)
                                end.linkTo(parent.end)
                            }

                        )
                        {
                            if (!viewModel.stepList.value.isNullOrEmpty()) {
                                onclick(index)
                            } else {
                                showCustomToast(context, "Something went wrong!")
                            }
                        }
                    } else {
                        IconButtonForward(
                            modifier = Modifier
                                .constrainAs(buttonContainer) {
                                    bottom.linkTo(textContainer.bottom)
                                    top.linkTo(textContainer.top)
                                    end.linkTo(parent.end)
                                }
                                .size(40.dp)
                        ) {
                            if (!viewModel.stepList.value.isNullOrEmpty()) {
                                onclick(index)
                            } else {
                                showCustomToast(context, "Something went wrong!")
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier
                        .width(48.dp)
                        .constrainAs(buttonContainer) {
                            bottom.linkTo(textContainer.bottom)
                            top.linkTo(textContainer.top)
                            end.linkTo(parent.end)
                        }
                    )
                }
            }
        }

        if (isCompleted) {
            Image(
                painter = painterResource(id = R.drawable.icon_check_circle_green),
                contentDescription = null,
                modifier = modifier
                    .border(
                        width = 2.dp,
                        color = Color.Transparent,
                        shape = CircleShape

                    )
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .constrainAs(step_no) {
                        start.linkTo(parent.start, margin = 16.dp)
                        top.linkTo(parent.top)
                    }
            )
        } else {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = greyBorder,
                        shape = CircleShape
                    )
                    .background(Color.White, shape = CircleShape)
                    .padding(6.dp)
                    .size(25.dp)
                    .aspectRatio(1f)
                    .constrainAs(step_no) {
                        start.linkTo(parent.start, margin = 16.dp)
                        top.linkTo(parent.top)
                    }
            ) {
                Text(
                    text = "$stepNo",
                    color = textColorDark,
                    style = smallerTextStyleNormalWeight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)

                )
            }

        }
    }
}