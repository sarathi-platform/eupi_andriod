package com.patsurvey.nudge.activities

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.RetryHelper
import com.patsurvey.nudge.activities.ui.progress.ProgressScreenViewModel
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.buttonTextStyle
import com.patsurvey.nudge.activities.ui.theme.dropDownBg
import com.patsurvey.nudge.activities.ui.theme.greenLight
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.greyBorder
import com.patsurvey.nudge.activities.ui.theme.largeTextStyle
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallerTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallerTextStyleNormalWeight
import com.patsurvey.nudge.activities.ui.theme.stepBoxActiveColor
import com.patsurvey.nudge.activities.ui.theme.stepIconCompleted
import com.patsurvey.nudge.activities.ui.theme.stepIconDisableColor
import com.patsurvey.nudge.activities.ui.theme.stepIconEnableColor
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.customviews.CustomSnackBarShow
import com.patsurvey.nudge.customviews.CustomSnackBarViewPosition
import com.patsurvey.nudge.customviews.CustomSnackBarViewState
import com.patsurvey.nudge.customviews.rememberSnackBarState
import com.patsurvey.nudge.utils.ARG_FROM_PAT_SURVEY
import com.patsurvey.nudge.utils.ARG_FROM_PROGRESS
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.IconButtonForward
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_KEY_IDENTITY_NUMBER
import com.patsurvey.nudge.utils.PREF_KEY_NAME
import com.patsurvey.nudge.utils.PREF_OPEN_FROM_HOME
import com.patsurvey.nudge.utils.PREF_PROGRAM_NAME
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TextButtonWithIcon
import com.patsurvey.nudge.utils.findStepNameForSelectedLanguage
import com.patsurvey.nudge.utils.setKeyboardToPan
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProgressScreen(
    modifier: Modifier = Modifier,
    viewModel: ProgressScreenViewModel,
    stepsNavHostController: NavHostController,
    onNavigateToStep:(Int, Int, Int, Boolean) ->Unit,
    onNavigateToSetting:()->Unit,
    onBackClick:()->Unit
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.init()
        viewModel.setVoEndorsementCompleteForVillages()
        delay(1000)
        viewModel.showLoader.value = false
    }

    val scaffoldState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = false)
    val scope = rememberCoroutineScope()

    val snackState = rememberSnackBarState()

    val steps by viewModel.stepList.collectAsState()
    val villages by viewModel.villageList.collectAsState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    val mainActivity = LocalContext.current as? MainActivity
    mainActivity?.isLoggedInLive?.postValue(viewModel.isLoggedIn())

    setKeyboardToPan(mainActivity!!)

   val tolaCount = remember {
       viewModel.tolaCount
   }
    val didiCount = remember {
        viewModel.didiCount
    }
    val poorDidiCount = remember {
        viewModel.poorDidiCount
    }
    val ultraPoorDidiCount = remember {
        viewModel.ultrPoorDidiCount
    }
    val endorsedDidiCount = remember {
        viewModel.endorsedDidiCount
    }

    BackHandler {
            onBackClick()
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
                        NudgeLogger.d("ProgressScreen","BottomSheet : $villages :: size ${villages.size}")
                        itemsIndexed(villages.distinctBy { it.id }) { index, village ->
                            VillageAndVoBoxForBottomSheet(
                                tolaName = village.name,
                                voName = village.federationName,
                                index = index,
                                selectedIndex = viewModel.villageSelected.value,
                                stepId = village.stepId,
                                statusId = village.statusId,
                                isVoEndorsementComplete = viewModel.isVoEndorsementComplete.value[village.id] ?: false,
                                context = context,
                                isUserBPC = viewModel.isUserBPC()
                            ) {
                                viewModel.showLoader.value = true
                                viewModel.villageSelected.value = it
                                viewModel.getStepsList(village.id)
                                viewModel.updateSelectedVillage(village)
                                viewModel.findInProgressStep(villageId = village.id)
                                viewModel.selectedText.value = village.name
                                scope.launch {
                                    scaffoldState.hide()
                                    delay(1000)
                                    viewModel.showLoader.value = false
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
                                viewModel.savePref(PREF_OPEN_FROM_HOME,true)
                                viewModel.saveSettingOpenFrom(PageFrom.HOME_PAGE.ordinal)
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
                        backgroundColor = Color.White,
                        elevation = 10.dp
                    )
                }
            ) { it ->
                if (viewModel.showLoader.value) {
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
                    Column(modifier = Modifier) {
                        if (/*viewModel.tokenExpired.value*/false) {
                            ShowOptDialog(
                                modifier = Modifier,
                                context = LocalContext.current,
                                viewModel = viewModel,
                                snackState = snackState,
                                setShowDialog = {
                                    viewModel.tokenExpired.value = false
                                },
                                positiveButtonClicked = {
                                    RetryHelper.updateOtp(viewModel.baseOtpNumber) { success, message ->
                                        if (success){
                                            viewModel.tokenExpired.value = false
                                        }
                                        else {
                                            snackState.addMessage(message = message, isSuccess = false, isCustomIcon = false)
                                        }
                                    }
                                }
                            )
                        }

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
                                name = viewModel.getPref(
                                    PREF_KEY_NAME,
                                    BLANK_STRING
                                ) ?: "",
                                identity = viewModel.getPref(
                                    PREF_KEY_IDENTITY_NUMBER,
                                    BLANK_STRING
                                ) ?: "",
                                isBackButtonShow = true,
                                isBPCUser = false,
                                onBackClick = {
                                    onBackClick()
                                }
                            )
                        }

                        item {
                            VillageSelectorDropDown(selectedText = viewModel.selectedText.value) {
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
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        itemsIndexed(items = steps.sortedBy { it.orderNumber }) { index, step ->
                            if ((viewModel.getPref(PREF_PROGRAM_NAME, "")
                                    ?: "").equals("CRP Program", true) && index < 5
                            ) {
                                val villageId=villages[viewModel.villageSelected.value].id
                                var isStepCompleted =
                                    viewModel.isStepComplete(steps[index].id,villageId).observeAsState().value
                                        ?: 0
                                if (index == 4){
                                    viewModel.isVoEndorsementComplete.value[villageId] = isStepCompleted == StepStatus.COMPLETED.ordinal
                                }
                                if(steps[index].orderNumber==1 && isStepCompleted==0){
                                    isStepCompleted=StepStatus.INPROGRESS.ordinal
                                }
                                if (isStepCompleted == StepStatus.COMPLETED.ordinal) {
                                    viewModel.updateSelectedStep(steps[index].stepId)
                                }
                                val subText = when(step.orderNumber) {
                                    1 -> tolaCount.value.let {
                                        if (it > 1)
                                            stringResource(id = R.string.transect_walk_sub_text_plural, it)
                                        else
                                            stringResource(id = R.string.transect_walk_sub_text_singular, it)
                                    }
                                    2 -> didiCount.value.let {
                                        if (it > 1)
                                            stringResource(id = R.string.social_mapping_sub_text_plural, it)
                                        else
                                            stringResource(id = R.string.social_mapping_sub_text_singular, it)
                                    }
                                    3 -> poorDidiCount.value.let {
                                        if (it > 1)
                                            stringResource(id = R.string.wealth_ranking_sub_text_plural, it)
                                        else
                                            stringResource(id = R.string.wealth_ranking_sub_text_singular, it)
                                    }
                                    4 -> ultraPoorDidiCount.value.let {
                                        if (it > 1)
                                            stringResource(id = R.string.pat_sub_text_plural, it)
                                        else
                                            stringResource(id = R.string.pat_sub_text_singular, it)
                                    }
                                    5 -> endorsedDidiCount.value.let {
                                        if (it > 1)
                                            stringResource(id = R.string.vo_endorsement_sub_text_plural, it)
                                        else
                                            stringResource(id = R.string.vo_endorsement_sub_text_singular, it)
                                    }
                                    else -> ""
                                }
                                StepsBox(
                                    boxTitle = findStepNameForSelectedLanguage(context,step.id),
                                    subTitle = subText,
                                    stepNo = step.orderNumber,
                                    index = index,
                                    iconId = step.orderNumber,
                                    viewModel = viewModel,
                                    shouldBeActive = isStepCompleted == StepStatus.INPROGRESS.ordinal || isStepCompleted == StepStatus.COMPLETED.ordinal,
                                    isCompleted = isStepCompleted == StepStatus.COMPLETED.ordinal
                                ) { index ->
                                    viewModel.stepSelected.value = index
                                    mainActivity?.isFilterApplied?.value=false
                                    val step=viewModel.stepList.value[index]
                                    viewModel.saveFromPage(ARG_FROM_PROGRESS)
                                    if (mainActivity?.isOnline?.value == true) {
                                       viewModel.callWorkFlowAPI(villageId,step.id,step.programId)
                                    }
                                    if (step.isComplete != StepStatus.COMPLETED.ordinal) {
                                    viewModel.updateWorkflowStatusInEvent(
                                        stepStatus = StepStatus.INPROGRESS,
                                        stepId = step.id,
                                        villageId = villageId
                                    )
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
                                            if (isStepCompleted == StepStatus.INPROGRESS.ordinal || isStepCompleted == StepStatus.COMPLETED.ordinal)
                                                viewModel.saveFromPage(ARG_FROM_PAT_SURVEY)
                                        }
                                        4 -> {}
                                        5 -> {}
                                    }
                                    if (isStepCompleted == StepStatus.INPROGRESS.ordinal || isStepCompleted == StepStatus.COMPLETED.ordinal)
                                        onNavigateToStep(villageId,step.id,index,(viewModel.stepList.value[index].isComplete == StepStatus.COMPLETED.ordinal))
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                        Spacer(modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth())
                    }
                    CustomSnackBarShow(state = snackState, position = CustomSnackBarViewPosition.Bottom)
                }
            }
        }
    }
}

@Composable
fun StepsBox(
    modifier: Modifier = Modifier,
    boxTitle: String,
    subTitle: String,
    stepNo: Int,
    index: Int,
    iconId: Int,
    viewModel: ProgressScreenViewModel?=null,
    isCompleted: Boolean = false,
    shouldBeActive: Boolean = false,
    onclick: (Int) -> Unit
) {
    val dividerMargins = 32.dp
    if (stepNo == 6)
        Spacer(modifier = Modifier.height(20.dp))

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
                    onclick(index)
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
                val iconResourceId = when (iconId) {
                    1 -> R.drawable.transect_walk_icon
                    2 -> R.drawable.social_maping_icon
                    3 -> R.drawable.wealth_raking_icon
                    4 -> R.drawable.pat_icon
                    5 -> R.drawable.vo_endorsement_icon
                    else -> null
                }
                if (iconResourceId != null) {
                    Icon(
                        painter = painterResource(id = iconResourceId),
                        contentDescription = null,
                        tint = if (shouldBeActive) { if (isCompleted) stepIconCompleted else stepIconEnableColor } else stepIconDisableColor,
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
                        text = if (boxTitle.contains("pat ", true)) boxTitle.replace("pat ", "PAT ", true) else boxTitle,
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
                        if (subTitle != "") {
                            Text(
                                text = subTitle,
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
                            onclick(index)
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
                            onclick(index)
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
                    .constrainAs(step_no) {
                        start.linkTo(parent.start, margin = 16.dp)
                        top.linkTo(parent.top)
                    }
            ) {
                Text(
                    text = "$stepNo",
                    color = textColorDark,
                    style = smallerTextStyleNormalWeight,
                    modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)

                )
            }

        }

        if (stepNo < 5) {
            Divider(
                color = greyBorder,
                modifier = Modifier
                    .height(8.dp)  //fill the max height
                    .width(1.dp)
                    .constrainAs(divider1) {
                        start.linkTo(parent.start, margin = dividerMargins)
                        top.linkTo(stepBox.bottom)
                    }
                    .padding(vertical = 2.dp)
            )

            Divider(
                color = greyBorder,
                modifier = Modifier
                    .height(8.dp)  //fill the max height
                    .width(1.dp)
                    .constrainAs(divider2) {
                        start.linkTo(parent.start, margin = dividerMargins)
                        top.linkTo(divider1.bottom)
                    }
                    .padding(vertical = 2.dp)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun StepBoxPreview(){

    StepsBox(boxTitle = "TransectBox", subTitle = "10 Poor didis identified", stepNo = 1, index = 1, iconId = 1, onclick = {})
}

@Preview(showBackground = true)
@Composable
fun UserDataViewPreview(){
    UserDataView(name = "Sarathi BPC", identity = "1212", isBackButtonShow = true, isBPCUser = true,onBackClick = {})
}
@Composable
fun UserDataView(
    modifier: Modifier = Modifier,
    name: String,
    identity: String,
    isBPCUser:Boolean,
    isBackButtonShow:Boolean,
    onBackClick:()->Unit
) {
    ConstraintLayout() {
        val (userDetail, moreMenu) = createRefs()
        Column(
            modifier = Modifier
                .constrainAs(userDetail) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .then(modifier)
        ) {
            Row {
                if(isBackButtonShow) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Negative Button",
                        tint = blueDark,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                onBackClick()
                            }
                    )
                }
                Text(
                    text = name,
                    color = textColorDark,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    style = largeTextStyle
                )
            }

            if(!isBPCUser) {
                Text(
                    text = stringResource(R.string.user_id_text) + identity,
                    color = textColorDark,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    style = smallTextStyle
                )
            }

        }
    }
}

@Composable
fun VillageSelectorDropDown(
    modifier: Modifier = Modifier,
    selectedText: String,
    showCarrotIcon: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(dropDownBg)
            .clip(RoundedCornerShape(6.dp))
            .height(40.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Color.Black
                )
            ) {
                onClick()

            }
            .then(modifier),
    ) {
        Row(
            Modifier
                .padding(horizontal = 14.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedText,
                color = blueDark,
                style = mediumTextStyle,
            )
            if (showCarrotIcon) {
                Icon(
                    painterResource(id = R.drawable.baseline_keyboard_arrow_down),
                    contentDescription = "drop down menu icon",
                    tint = blueDark
                )
            }
        }
    }
}

@Composable
fun ProgressScreenTopBar(
    modifier: Modifier = Modifier,
    onHamburgerClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        ConstraintLayout(
            Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(40.dp)
                .align(Alignment.Center),
        ) {
            val (titleItem, moreMenu) = createRefs()
            Text(
                text = "Sarathi",
                color = textColorDark,
                fontFamily = NotoSans,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .constrainAs(titleItem)
                    {
                        top.linkTo(parent.top, margin = 8.dp)
                        start.linkTo(parent.start)
                    }
            )

            Icon(
                painter = painterResource(id = R.drawable.more_icon),
                contentDescription = "more action button",
                tint = blueDark,
                modifier = Modifier
                    .constrainAs(moreMenu) {
                        top.linkTo(titleItem.top)
                        end.linkTo(parent.end)
                    }
                    .padding(10.dp)
                    .clickable {
                        onHamburgerClick()
                    }
            )
        }
    }
}

@Composable
fun ShowOptDialog(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: BaseViewModel,
    snackState: CustomSnackBarViewState,
    setShowDialog: (Boolean) -> Unit,
    positiveButtonClicked: () -> Unit,
    /*isResendOTPEnable: MutableState<Boolean>,
    formattedTime: MutableState<String>,
    isResendOTPVisible: MutableState<Boolean>*/
) {
    /*var otpValue by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = Unit) {
        if (RetryHelper.tokenExpired.value) {
            viewModel.tokenExpired.value = true
            RetryHelper.generateOtp() { success, message, mobileNumber ->
                if (success) {
                    snackState.addMessage(
                        message = context.getString(R.string.otp_send_to_mobile_number_message_for_relogin)
                            .replace("{MOBILE_NUMBER}", mobileNumber, true),
                        isSuccess = true, isCustomIcon = false
                    )
                } else {
                    snackState.addMessage(
                        message = message,
                        isSuccess = false,
                        isCustomIcon = false
                    )
                }
            }
        }
    }

    Dialog(onDismissRequest = { setShowDialog(false) }, DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
    )) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Session Expired",
                        textAlign = TextAlign.Center,
                        style = buttonTextStyle,
                        maxLines = 1,
                        color = textColorDark,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Please enter OTP to relogin",
                        textAlign = TextAlign.Start,
                        style = smallTextStyleMediumWeight,
                        maxLines = 2,
                        color = textColorDark,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OtpInputFieldForDialog(otpLength = 6, onOtpChanged = { otp ->
                        otpValue = otp
                        viewModel.baseOtpNumber.value = otpValue
                    })

                *//*    AnimatedVisibility(visible = !isResendOTPEnable.value, exit = fadeOut(), enter = fadeIn()) {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth(),

                            ) {
                            val countDownTimer =
                                object : CountDownTimer(OTP_RESEND_DURATION, 1000) {
                                    @SuppressLint("SimpleDateFormat")
                                    override fun onTick(millisUntilFinished: Long) {
                                        val dateTimeFormat= SimpleDateFormat("00:ss")
                                        formattedTime.value=dateTimeFormat.format(Date(millisUntilFinished))

                                    }

                                    override fun onFinish() {
                                        isResendOTPEnable.value = true
                                        isResendOTPVisible = !isResendOTPVisible
                                    }

                                }
                            DisposableEffect(key1 = !isResendOTPEnable.value) {
                                countDownTimer.start()
                                onDispose {
                                    countDownTimer.cancel()
                                }
                            }
                            Text(
                                text = stringResource(
                                    id = R.string.expiry_login_verify_otp,
                                    formattedTime.value
                                ),
                                color = textColorDark,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = dimensionResource(id = R.dimen.dp_8))
                                    .background(Color.Transparent)
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = stringResource(id = R.string.resend_otp),
                            color = if (isResendOTPEnable.value) greenOnline else placeholderGrey,
                            fontSize = 14.sp,
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable(enabled = isResendOTPEnable.value) {
                                RetryHelper.generateOtp() { success, message, mobileNumber ->
                                    snackState.addMessage(
                                        message = context.getString(R.string.otp_resend_to_mobile_number_message_for_relogin).replace("{MOBILE_NUMBER}", mobileNumber, true),
                                        isSuccess = true, isCustomIcon = false)
                                }
                                formattedTime.value = SEC_30_STRING
                                isResendOTPEnable.value = false
                            }
                        )
                    }*//*


                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        ButtonPositive(
                            buttonTitle = stringResource(id = R.string.submit),
                            isArrowRequired = false,
                            isActive = otpValue.length == OTP_LENGTH,
                            modifier = Modifier.weight(1f)
                        ) {
                            positiveButtonClicked()
                            setShowDialog(false)
                        }
                    }
                }
            }
        }
    }*/
}