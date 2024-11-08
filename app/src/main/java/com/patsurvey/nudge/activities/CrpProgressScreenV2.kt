package com.patsurvey.nudge.activities

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.nrlm.baselinesurvey.utils.numberInEnglishFormat
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.progress.ProgressScreenViewModelV2
import com.patsurvey.nudge.activities.ui.progress.events.SelectionEvents
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.buttonTextStyle
import com.patsurvey.nudge.activities.ui.theme.greenLight
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.greyBorder
import com.patsurvey.nudge.activities.ui.theme.smallerTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallerTextStyleNormalWeight
import com.patsurvey.nudge.activities.ui.theme.stepBoxActiveColor
import com.patsurvey.nudge.activities.ui.theme.stepIconCompleted
import com.patsurvey.nudge.activities.ui.theme.stepIconDisableColor
import com.patsurvey.nudge.activities.ui.theme.stepIconEnableColor
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.customviews.CustomSnackBarShow
import com.patsurvey.nudge.customviews.CustomSnackBarViewPosition
import com.patsurvey.nudge.customviews.rememberSnackBarState
import com.patsurvey.nudge.data.prefs.SharedPrefs.Companion.PREF_KEY_PAGE_FROM
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.utils.ARG_FROM_PAT_SURVEY
import com.patsurvey.nudge.utils.ARG_FROM_PROGRESS
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.IconButtonForward
import com.patsurvey.nudge.utils.NudgeCore.getVoNameForState
import com.patsurvey.nudge.utils.PREF_KEY_IDENTITY_NUMBER
import com.patsurvey.nudge.utils.PREF_KEY_NAME
import com.patsurvey.nudge.utils.PREF_OPEN_FROM_HOME
import com.patsurvey.nudge.utils.PREF_PROGRAM_NAME
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TextButtonWithIcon
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CrpProgressScreenV2(
    modifier: Modifier = Modifier,
    viewModel: ProgressScreenViewModelV2 = hiltViewModel(),
    onNavigateToSetting: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToStep: (Int, Int, Int, Boolean) -> Unit
) {

    val scaffoldState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = false)
    val scope = rememberCoroutineScope()

    val snackState = rememberSnackBarState()

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    LaunchedEffect(Unit) {
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        viewModel.onEvent(InitDataEvent.InitDataState)
    }

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
                    getVoNameForState(
                        context,
                        viewModel.getStateId(),
                        R.plurals.seletc_village_screen_text
                    ),
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = textColorDark,
                    modifier = Modifier.padding(top = 12.dp)
                )
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    itemsIndexed(viewModel.villageList) { index, village ->
                        VillageAndVoBoxForBottomSheet(
                            stateId = viewModel.getStateId(),
                            tolaName = village.name,
                            voName = village.federationName,
                            index = index,
                            selectedIndex = viewModel.villageSelected.value,
                            stepId = village.stepId,
                            statusId = village.statusId,
                            isVoEndorsementComplete = viewModel.isVoEndorsementComplete.value[village.id]
                                ?: false,
                            context = context,
                            isUserBPC = viewModel.isUserBPC()
                        ) {
                            viewModel.onEvent(SelectionEvents.UpdateSelectedVillage(it))

                            /*viewModel.getStepsList(village.id)
                            viewModel.updateSelectedVillage(village)
                            viewModel.findInProgressStep(villageId = village.id)
                            viewModel.selectedText.value = village.name*/
                            scope.launch {
                                scaffoldState.hide()
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
            modifier = Modifier
                .then(modifier),
            backgroundColor = white,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.sarathi_logo_mini),
                                contentDescription = "app bar icon",
                                tint = textColorDark,
                                modifier = Modifier.size(26.dp)
                            )
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
                            viewModel.preferenceProviderUseCase.savePref(PREF_OPEN_FROM_HOME, true)
                            viewModel.preferenceProviderUseCase.saveSettingOpenFrom(PageFrom.HOME_PAGE.ordinal)
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

            if (viewModel.loaderState.value.isLoaderVisible) {
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
                                name = viewModel.preferenceProviderUseCase.getPref(
                                    PREF_KEY_NAME,
                                    BLANK_STRING
                                ) ?: "",
                                identity = viewModel.preferenceProviderUseCase.getPref(
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                VillageSelectorDropDown(selectedText = viewModel.villageSelectionDropDownTitle.value) {
                                    scope.launch {
                                        if (!scaffoldState.isVisible) {
                                            scaffoldState.show()
                                        } else {
                                            scaffoldState.hide()
                                        }
                                    }
                                }

                                /*IconButton(
                                    onClick = {

                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh Data button",
                                        tint = blueDark
                                    )
                                }*/
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        itemsIndexed(items = viewModel.stepList) { index, step ->
                            if ((viewModel.preferenceProviderUseCase.getPref(PREF_PROGRAM_NAME, "")
                                    ?: "").equals("CRP Program", true) && index < 5
                            ) {

                                val subText = getSubTitleText(
                                    context,
                                    orderNumber = step.orderNumber,
                                    stateId = viewModel.getStateId(),
                                    subTitleCount = viewModel.getSubTitleCount(step.orderNumber)
                                )

                                StepBoxV2(
                                    index = index,
                                    stepListEntity = step,
                                    title = getFinalTitleForStep(step.name),
                                    subTitle = subText
                                ) { clickedIndex ->

                                    when (clickedIndex) {
                                        3 -> {
                                            if (step.isComplete == StepStatus.INPROGRESS.ordinal || step.isComplete == StepStatus.COMPLETED.ordinal)
                                                viewModel.preferenceProviderUseCase.savePref(
                                                    PREF_KEY_PAGE_FROM,
                                                    ARG_FROM_PAT_SURVEY
                                                )
                                        }

                                        else -> {
                                            viewModel.preferenceProviderUseCase.savePref(
                                                PREF_KEY_PAGE_FROM,
                                                ARG_FROM_PROGRESS
                                            )
                                        }
                                    }
                                    if (step.isComplete == StepStatus.INPROGRESS.ordinal || step.isComplete == StepStatus.COMPLETED.ordinal)
                                        onNavigateToStep(
                                            viewModel.selectedVillageId.value,
                                            step.id,
                                            clickedIndex,
                                            (viewModel.stepList[clickedIndex].isComplete == StepStatus.COMPLETED.ordinal)
                                        )
                                }

                                /*StepsBox(
                                    boxTitle = findStepNameForSelectedLanguage(context,step.id,viewModel.getStateId()),
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
                                        3 -> {
                                            if (step == StepStatus.INPROGRESS.ordinal || isStepCompleted == StepStatus.COMPLETED.ordinal)
                                                viewModel.saveFromPage(ARG_FROM_PAT_SURVEY)
                                        }
                                    }
                                    if (isStepCompleted == StepStatus.INPROGRESS.ordinal || isStepCompleted == StepStatus.COMPLETED.ordinal)
                                        onNavigateToStep(villageId,step.id,index,(viewModel.stepList.value[index].isComplete == StepStatus.COMPLETED.ordinal))
                                }*/
                            }
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                    Spacer(
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                    )
                }
                CustomSnackBarShow(state = snackState, position = CustomSnackBarViewPosition.Bottom)


            }

        }
    }
}

@Composable
fun getSubTitleText(context: Context, orderNumber: Int, subTitleCount: Int, stateId: Int): String {

    val subText = when (orderNumber) {
        1 -> subTitleCount.let {
            if (it > 1)
                stringResource(
                    R.string.transect_walk_sub_text_plural,
                    numberInEnglishFormat(subTitleCount, null)
                )
            else
                stringResource(
                    id = R.string.transect_walk_sub_text_singular,
                    numberInEnglishFormat(it, null)
                )
        }

        2 -> subTitleCount.let {
            if (it > 1)
                stringResource(
                    id = R.string.social_mapping_sub_text_plural,
                    numberInEnglishFormat(it, null)
                )
            else
                stringResource(
                    id = R.string.social_mapping_sub_text_singular,
                    numberInEnglishFormat(it, null)
                )
        }

        3 -> subTitleCount.let {
            if (it > 1)
                stringResource(
                    id = R.string.wealth_ranking_sub_text_plural,
                    numberInEnglishFormat(it, null)
                )
            else
                stringResource(
                    id = R.string.wealth_ranking_sub_text_singular,
                    numberInEnglishFormat(it, null)
                )
        }

        4 -> subTitleCount.let {
            if (it > 1)
                getVoNameForState(context, stateId, R.plurals.pat_sub_text_plural, it)
            else
                getVoNameForState(context, stateId, R.plurals.pat_sub_text_singular, it)
        }

        5 -> subTitleCount.let {
            if (it > 1)
                stringResource(
                    id = R.string.vo_endorsement_sub_text_plural,
                    numberInEnglishFormat(it, null)
                )
            else
                stringResource(
                    id = R.string.vo_endorsement_sub_text_singular,
                    numberInEnglishFormat(it, null)
                )
        }

        else -> ""
    }

    return subText
}

@Composable
fun StepBoxV2(
    modifier: Modifier = Modifier,
    index: Int,
    stepListEntity: StepListEntity,
    title: String,
    subTitle: String,
    onStepBoxClick: (index: Int) -> Unit
) {

    val isStepCompleted = remember(stepListEntity.isComplete) {
        derivedStateOf {
            stepListEntity.isComplete == StepStatus.COMPLETED.ordinal
        }
    }

    val shouldBeActive = remember(stepListEntity.isComplete) {
        derivedStateOf {
            stepListEntity.isComplete != StepStatus.NOT_STARTED.ordinal
        }
    }

    val dividerMargins = 32.dp
    if (stepListEntity.orderNumber == 6)
        Spacer(modifier = Modifier.height(20.dp))

    ConstraintLayout(
        modifier = Modifier
            .background(Color.White)
            .border(
                width = 0.dp,
                color = Color.Transparent,
            )
            .clickable {
                onStepBoxClick(index)
            }
            .then(modifier)
    ) {
        val (step_no, stepBox, divider1, divider2) = createRefs()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = if (isStepCompleted.value) greenOnline else greyBorder,
                    shape = RoundedCornerShape(6.dp)
                )
                .background(Color.White)

                .constrainAs(stepBox) {
                    start.linkTo(parent.start)
                    top.linkTo(step_no.bottom, margin = -16.dp)
                }

        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(getCardBackgroundColor(isStepCompleted, shouldBeActive))
                    .padding(vertical = 14.dp)
                    .padding(end = 16.dp, start = 8.dp),
            ) {
                val (textContainer, buttonContainer, iconContainer) = createRefs()
                val painter = getStepIcon(stepListEntity.orderNumber)
                if (painter != null) {
                    Icon(
                        painter = painter,
                        contentDescription = null,
                        tint = getStepBoxIconTint(shouldBeActive, isStepCompleted),
                        modifier = Modifier
                            .constrainAs(iconContainer) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                            .size(48.dp)
                            .padding(
                                top = if (isStepCompleted.value) 0.dp else 6.dp,
                                start = if (isStepCompleted.value) 0.dp else 4.dp
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
                        text = title,
                        color = if (isStepCompleted.value) greenOnline else textColorDark,
                        modifier = Modifier
                            .padding(
                                top = 10.dp,
                                bottom = if (isStepCompleted.value) 0.dp else 10.dp,
                                end = 10.dp
                            )
                            .fillMaxWidth(),
                        softWrap = true,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = buttonTextStyle
                    )
                    if (isStepCompleted.value) {
                        if (subTitle != "") {
                            Text(
                                text = subTitle,
                                color = if (isStepCompleted.value) greenOnline else textColorDark,
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

                if (shouldBeActive.value) {
                    if (isStepCompleted.value) {
                        TextButtonWithIcon(modifier = Modifier
                            .constrainAs(buttonContainer) {
                                bottom.linkTo(textContainer.bottom)
                                top.linkTo(textContainer.top)
                                end.linkTo(parent.end)
                            }
                        ) {
                            onStepBoxClick(index)
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
                            onStepBoxClick(index)
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




        if (isStepCompleted.value) {
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
                    text = "${stepListEntity.orderNumber}",
                    color = textColorDark,
                    style = smallerTextStyleNormalWeight,
                    modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)

                )
            }

        }

        if (stepListEntity.orderNumber < 5) {
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

@Composable
private fun getStepIcon(iconId: Int): Painter? {
    val iconResourceId = when (iconId) {
        1 -> R.drawable.transect_walk_icon
        2 -> R.drawable.social_maping_icon
        3 -> R.drawable.wealth_raking_icon
        4 -> R.drawable.pat_icon
        5 -> R.drawable.vo_endorsement_icon
        else -> null
    }
    val painter = iconResourceId?.let { painterResource(id = it) }
    return painter
}

@Composable
private fun getFinalTitleForStep(title: String) =
    if (title.contains("pat ", true)) title.replace("pat ", "PAT ", true) else title

@Composable
private fun getStepBoxIconTint(
    shouldBeActive: State<Boolean>,
    isStepCompleted: State<Boolean>
) =
    if (shouldBeActive.value) {
        if (isStepCompleted.value) stepIconCompleted else stepIconEnableColor
    } else stepIconDisableColor

@Composable
private fun getCardBackgroundColor(
    isStepCompleted: State<Boolean>,
    shouldBeActive: State<Boolean>
) =
    if (isStepCompleted.value) greenLight else if (shouldBeActive.value) stepBoxActiveColor else white