package com.patsurvey.nudge.activities.ui.bpc.bpc_add_more_did_screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.CircularDidiImage
import com.patsurvey.nudge.activities.circleLayout
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.black2
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.borderGreyLight
import com.patsurvey.nudge.activities.ui.theme.checkBoxUncheckedColor
import com.patsurvey.nudge.activities.ui.theme.didiDetailItemStyle
import com.patsurvey.nudge.activities.ui.theme.didiDetailLabelStyle
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.largeTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorBlueLight
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.activities.ui.theme.yellowBg
import com.patsurvey.nudge.customviews.CardArrow
import com.patsurvey.nudge.customviews.SearchWithFilterView
import com.patsurvey.nudge.database.BpcNonSelectedDidiEntity
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.EXPANSTION_TRANSITION_DURATION
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.WealthRank

@Composable
fun BpcAddMoreDidiScreen(
    modifier: Modifier = Modifier,
    bpcAddMoreDidiViewModel: BpcAddMoreDidiViewModel,
    navController: NavHostController,
    forReplace: Boolean = false
) {
    val didis by bpcAddMoreDidiViewModel.nonSelectedDidiList.collectAsState()

    val newFilteredTolaDidiList = bpcAddMoreDidiViewModel.filterTolaMapList
    val newFilteredDidiList = bpcAddMoreDidiViewModel.filterDidiList

    val coroutineScope = rememberCoroutineScope()

    val localDensity = LocalDensity.current

    val focusManager = LocalFocusManager.current

    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    var filterSelected by remember {
        mutableStateOf(false)
    }

    val expandedIds = remember {
        mutableStateListOf<Int>()
    }

    val isCheckedIds = remember {
        mutableStateListOf<Int>()
    }

    val isSelectedCount = remember {
        mutableStateOf<Int>(0)
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    LaunchedEffect(key1 = Unit) {
        bpcAddMoreDidiViewModel.fetchDidiFromDb()
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
                modifier = Modifier
                    .align(Alignment.TopCenter)
            ) {

                Text(
                    text = stringResource(id = R.string.bpc_add_more_didi_screen_title),
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(
                            vertical = dimensionResource(id = R.dimen.dp_6),
                            horizontal = 32.dp
                        )
                        .fillMaxWidth()
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = white)
                        .pointerInput(true) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                            })
                        }
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 10.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    item {
                        Spacer(
                            modifier = Modifier
                                .height(14.dp)
                                .fillMaxWidth()
                        )
                    }

                    item {
                        SearchWithFilterView(
                            placeholderString = stringResource(id = R.string.search_didis),
                            filterSelected = filterSelected,
                            onFilterSelected = {
                                if (didis.isNotEmpty()) {
                                    filterSelected = !it
                                    bpcAddMoreDidiViewModel.filterList()
                                }
                            },
                            onSearchValueChange = {
                                bpcAddMoreDidiViewModel.performQuery(it, filterSelected)
                            }
                        )
                    }

                    if (newFilteredDidiList.isEmpty()) {
                        item {
                            Text(
                                text = "No more Didi's available to add or replace.",
                                textAlign = TextAlign.Center,
                                style = largeTextStyle,
                                color = textColorDark,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = (screenHeight / 4).dp)
                            )
                        }
                    } else {

                        item {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val count = isSelectedCount.value
                                Text(
                                    text = stringResource(
                                        id = if (count > 1) R.string.didi_selected_text_plural else R.string.didi_selected_text_singular,
                                        count
                                    ),
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontFamily = NotoSans,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(vertical = dimensionResource(id = R.dimen.dp_6))
                                        .padding(start = 4.dp)
                                        .weight(0.75f)
                                )
                            }
                        }

                        if (filterSelected) {
                            itemsIndexed(
                                newFilteredTolaDidiList.keys.toList().reversed()
                            ) { index, didiKey ->

                                ShowDidisFromTolaForBpcAddMoreScreen(
                                    didiTola = didiKey,
                                    didiList = newFilteredTolaDidiList[didiKey] ?: emptyList(),
                                    modifier = modifier,
                                    isForReplace = forReplace,
                                    expandedIds = expandedIds,
                                    isCheckedIds = isCheckedIds,
                                    onExpendClick = { expand, didiDetailModel ->
                                        if (expandedIds.contains(didiDetailModel.id)) {
                                            expandedIds.remove(didiDetailModel.id)
                                        } else {
                                            expandedIds.add(didiDetailModel.id)
                                        }
                                    },
                                    onItemClick = { isChecked, didi ->
                                        if (forReplace) {
                                            isCheckedIds.clear()
                                            isCheckedIds.add(didi.id)
                                            isSelectedCount.value = 1
                                        } else {
                                            if (isCheckedIds.contains(didi.id)) {
                                                isCheckedIds.remove(didi.id)
                                                isSelectedCount.value = --isSelectedCount.value
                                            } else {
                                                isCheckedIds.add(didi.id)
                                                isSelectedCount.value = ++isSelectedCount.value
                                            }
                                        }
                                    }
                                )

                                if (index < newFilteredTolaDidiList.keys.size - 1) {
                                    Divider(
                                        color = borderGreyLight,
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = 22.dp,
                                            bottom = 1.dp
                                        )
                                    )
                                }
                            }
                        } else {
                            itemsIndexed(newFilteredDidiList) { index, didi ->
                                ExpandableDidiItemCardForBpc(
                                    didi = didi,
                                    expanded = expandedIds.contains(didi.id),
                                    modifier = modifier,
                                    isChecked = isCheckedIds.contains(didi.id),
                                    isForReplace = forReplace,
                                    onExpendClick = { expand, didiDetailModel ->
                                        if (expandedIds.contains(didiDetailModel.id)) {
                                            expandedIds.remove(didiDetailModel.id)
                                        } else {
                                            expandedIds.add(didiDetailModel.id)
                                        }
                                    },
                                    onItemClick = { isChecked, didi ->
                                        if (forReplace) {
                                            isCheckedIds.clear()
                                            isCheckedIds.add(didi.id)
                                        } else {
                                            if (isCheckedIds.contains(didi.id)) {
                                                isCheckedIds.remove(didi.id)
                                                isSelectedCount.value = --isSelectedCount.value
                                            } else {
                                                isCheckedIds.add(didi.id)
                                                isSelectedCount.value = ++isSelectedCount.value
                                            }
                                        }

                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isCheckedIds.isNotEmpty()) {
            DoubleButtonBox(
                modifier = Modifier
                    .shadow(10.dp)
                    .constrainAs(bottomActionBox) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .onGloballyPositioned { coordinates ->
                        bottomPadding = with(localDensity) {
                            coordinates.size.height.toDp()
                        }
                    },
                negativeButtonRequired = false,
                positiveButtonText = stringResource(id = R.string.confirm_text),
                positiveButtonOnClick = {
                    if (forReplace) {
                        bpcAddMoreDidiViewModel.replaceDidi(isCheckedIds)
                    } else {
                        bpcAddMoreDidiViewModel.markCheckedDidisSelected(isCheckedIds)
                    }
                    navController.popBackStack()
                },
                negativeButtonOnClick = {}
            )
        }
    }
}

@Composable
fun ExpandableDidiItemCardForBpc(
    didi: BpcNonSelectedDidiEntity,
    expanded: Boolean,
    modifier: Modifier,
    isChecked: Boolean,
    isForReplace: Boolean,
    onExpendClick: (Boolean, BpcNonSelectedDidiEntity) -> Unit,
    onItemClick: (Boolean, BpcNonSelectedDidiEntity) -> Unit
) {

    val mIsChecked = remember { mutableStateOf(isChecked) }


    val transition = updateTransition(expanded, label = "transition")

    val animateColor by transition.animateColor({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "animate color") {
        if (it) {
            greenOnline
        } else {
            textColorDark
        }
    }

    val animateInt by transition.animateInt({
        tween(durationMillis = 10)
    }, label = "animate float") {
        if (it) 1 else 0
    }

    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "rotationDegreeTransition") {
        if (it) 180f else 0f
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            elevation = 10.dp,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable {
                    onExpendClick(expanded, didi)
                }
                .then(modifier)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BoxWithConstraints {
                    val constraintSet = decoupledConstraints()
                    ConstraintLayout(constraintSet, modifier = Modifier.fillMaxWidth()) {
                        CircularDidiImage(
                            modifier = Modifier.layoutId("didiImage")
                        )
                        Text(
                            text = didi.name,
                            style = TextStyle(
                                color = animateColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = NotoSans
                            ),
                            modifier = Modifier.layoutId("didiName")
                        )

                        Image(
                            painter = painterResource(id = R.drawable.home_icn),
                            contentDescription = "home image",
                            modifier = Modifier
                                .width(18.dp)
                                .height(14.dp)
                                .layoutId("homeImage"),
                            colorFilter = ColorFilter.tint(textColorBlueLight)
                        )

                        Text(
                            text = didi.cohortName,
                            style = TextStyle(
                                color = textColorBlueLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = NotoSans
                            ),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.layoutId("village")
                        )
                        CardArrow(
                            modifier = Modifier.layoutId("expendArrowImage"),
                            degrees = arrowRotationDegree,
                            iconColor = animateColor,
                            onClick = { onExpendClick(expanded, didi) }
                        )

                        DidiDetailExpendableContentForBpc(
                            modifier = Modifier.layoutId("didiDetailLayout"),
                            didi,
                            animateInt == 1
                        )
                    }
                }
            }
        }

        if (isForReplace) {
            RadioButton(
                selected = isChecked,
                onClick = {
                    onItemClick(isChecked, didi)
                },
                enabled = true,
                colors = RadioButtonDefaults.colors(
                    selectedColor = blueDark,
                    unselectedColor = checkBoxUncheckedColor
                )
            )
        } else {
            Checkbox(
                modifier = Modifier
                    .size(48.dp),
                checked = mIsChecked.value,
                onCheckedChange = {
                    mIsChecked.value = it
                    onItemClick(it, didi)
                },
                enabled = true,
                colors = CheckboxDefaults.colors(
                    checkedColor = blueDark,
                    checkmarkColor = Color.White,
                    uncheckedColor = checkBoxUncheckedColor
                )
            )
        }
    }
}

@Composable
fun DidiDetailExpendableContentForBpc(
    modifier: Modifier,
    didi: BpcNonSelectedDidiEntity,
    expended: Boolean
) {
    val constraintSet = didiDetailConstraints()

    val context = LocalContext.current

    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            // Expand from the top.
            shrinkTowards = Alignment.Top,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        ) + fadeOut(
            // Fade in with the initial alpha of 0.3f.
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        )
    }

    AnimatedVisibility(
        visible = expended,
        enter = enterTransition,
        exit = exitTransition,
        modifier = Modifier.then(modifier)
    ) {
        ConstraintLayout(constraintSet, modifier = Modifier.fillMaxWidth()) {
            Divider(
                color = borderGreyLight,
                thickness = 1.dp,
                modifier = Modifier.layoutId("divider")
            )

            Text(
                text = stringResource(id = R.string.house_number) + ":",
                style = didiDetailLabelStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("houseNumberLabel")
            )

            Text(
                text = didi.address,
                style = didiDetailItemStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("houseNumber")
            )

            Text(
                text = stringResource(id = R.string.dada_name) + ":",
                style = didiDetailLabelStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("dadaNameLabel")
            )

            Text(
                text = didi.guardianName,
                style = didiDetailItemStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("dadaName")
            )

            Text(
                text = stringResource(id = R.string.caste) + ":",
                style = didiDetailLabelStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("casteLabel")
            )

            Text(
                text = didi.castName ?: BLANK_STRING,
                style = didiDetailItemStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("caste")
            )

            Text(
                text = stringResource(id = R.string.tola) + ":",
                style = didiDetailLabelStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("tolaLabel")
            )

            Text(
                text = didi.cohortName,
                style = didiDetailItemStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("tola")
            )

            Text(
                text = "Latest Status:",
                style = didiDetailLabelStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("latestStatusLabel")
            )

            Text(
                text = getLatestStatusTextForBpc(context, didi),
                style = didiDetailItemStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("latestStatus")
            )

            Spacer(
                modifier = Modifier
                    .layoutId("bottomPadding")
                    .height(30.dp)
            )
        }
    }
}

private fun decoupledConstraints(): ConstraintSet {
    return ConstraintSet {
        val didiImage = createRefFor("didiImage")
        val didiName = createRefFor("didiName")
        val didiRow = createRefFor("didiRow")
        val homeImage = createRefFor("homeImage")
        val village = createRefFor("village")
        val expendArrowImage = createRefFor("expendArrowImage")
        val didiDetailLayout = createRefFor("didiDetailLayout")



        constrain(didiImage) {
            top.linkTo(parent.top, margin = 12.dp)
            start.linkTo(parent.start, margin = 10.dp)
        }
        constrain(didiName) {
            start.linkTo(didiImage.end, 10.dp)
            top.linkTo(parent.top, 10.dp)
            end.linkTo(expendArrowImage.start, margin = 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(didiRow) {
            start.linkTo(didiImage.end, 10.dp)
            top.linkTo(parent.top, 10.dp)
            end.linkTo(expendArrowImage.start, margin = 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(village) {
            start.linkTo(homeImage.end, margin = 10.dp)
            top.linkTo(didiName.bottom)
            end.linkTo(expendArrowImage.start, margin = 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(homeImage) {
            top.linkTo(village.top)
            bottom.linkTo(village.bottom)
            start.linkTo(didiName.start)
        }

        constrain(expendArrowImage) {
            top.linkTo(didiName.top)
            bottom.linkTo(village.bottom)
            end.linkTo(parent.end, margin = 10.dp)
        }

        constrain(didiDetailLayout) {
            top.linkTo(village.bottom, margin = 15.dp, goneMargin = 20.dp)
            end.linkTo(parent.end)
            start.linkTo(parent.start)
        }
    }
}

private fun didiDetailConstraints(): ConstraintSet {
    return ConstraintSet {
        val divider = createRefFor("divider")
        val houseNumberLabel = createRefFor("houseNumberLabel")
        val houseNumber = createRefFor("houseNumber")
        val dadaNameLabel = createRefFor("dadaNameLabel")
        val dadaName = createRefFor("dadaName")
        val casteLabel = createRefFor("casteLabel")
        val caste = createRefFor("caste")
        val tolaLabel = createRefFor("tolaLabel")
        val tola = createRefFor("tola")
        val latestStatusLabel = createRefFor("latestStatusLabel")
        val latestStatus = createRefFor("latestStatus")
        val bottomPadding = createRefFor("bottomPadding")

        val centerGuideline = createGuidelineFromStart(0.5f)


        constrain(divider) {
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            start.linkTo(parent.start)
        }

        constrain(houseNumberLabel) {
            start.linkTo(parent.start, margin = 15.dp)
            top.linkTo(divider.bottom, margin = 15.dp)
            end.linkTo(centerGuideline)
            width = Dimension.fillToConstraints
        }

        constrain(houseNumber) {
            start.linkTo(centerGuideline)
            top.linkTo(houseNumberLabel.top)
            bottom.linkTo(houseNumberLabel.bottom)
            end.linkTo(parent.end, margin = 10.dp)
            width = Dimension.fillToConstraints
        }

        constrain(dadaNameLabel) {
            start.linkTo(houseNumberLabel.start)
            top.linkTo(houseNumberLabel.bottom, margin = 20.dp)
            end.linkTo(centerGuideline)
            width = Dimension.fillToConstraints
        }

        constrain(dadaName) {
            start.linkTo(centerGuideline)
            top.linkTo(dadaNameLabel.top)
            bottom.linkTo(dadaNameLabel.bottom)
            end.linkTo(parent.end, margin = 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(casteLabel) {
            start.linkTo(houseNumberLabel.start)
            top.linkTo(dadaNameLabel.bottom, margin = 20.dp)
            end.linkTo(centerGuideline)
            width = Dimension.fillToConstraints
        }

        constrain(caste) {
            start.linkTo(centerGuideline)
            top.linkTo(casteLabel.top)
            bottom.linkTo(casteLabel.bottom)
            end.linkTo(parent.end, margin = 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(tolaLabel) {
            start.linkTo(houseNumberLabel.start)
            top.linkTo(casteLabel.bottom, margin = 15.dp)
            end.linkTo(centerGuideline)
            width = Dimension.fillToConstraints
        }

        constrain(tola) {
            start.linkTo(centerGuideline)
            top.linkTo(tolaLabel.top)
            bottom.linkTo(tolaLabel.bottom)
            end.linkTo(parent.end, margin = 10.dp)
            width = Dimension.fillToConstraints
        }

        constrain(latestStatusLabel) {
            start.linkTo(houseNumberLabel.start)
            top.linkTo(tolaLabel.bottom, margin = 15.dp)
            end.linkTo(centerGuideline)
            width = Dimension.fillToConstraints
        }

        constrain(latestStatus) {
            start.linkTo(centerGuideline)
            top.linkTo(latestStatusLabel.top)
            bottom.linkTo(latestStatusLabel.bottom)
            end.linkTo(parent.end, margin = 10.dp)
            width = Dimension.fillToConstraints
        }

        constrain(bottomPadding) {
            start.linkTo(parent.start)
            top.linkTo(latestStatus.bottom)
        }
    }
}

fun getLatestStatusTextForBpc(context: Context, didi: BpcNonSelectedDidiEntity): String {
    var status = context.getString(R.string.wealth_ranking_status_complete_text)
    if (didi.wealth_ranking == WealthRank.NOT_RANKED.rank) {
        status = context.getString(R.string.wealth_ranking_status_not_started_text)
    } else {
        when (didi.patSurveyStatus) {
            PatSurveyStatus.NOT_STARTED.ordinal -> {
                status = context.getString(R.string.wealth_ranking_status_complete_text)
            }

            PatSurveyStatus.INPROGRESS.ordinal -> {
                status = context.getString(R.string.pat_in_progress_status_text)
            }

            PatSurveyStatus.NOT_AVAILABLE.ordinal, PatSurveyStatus.COMPLETED.ordinal -> {
                status =
                    if (didi.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal || didi.voEndorsementStatus == DidiEndorsementStatus.REJECTED.ordinal) {
                        context.getString(R.string.vo_endorsement_status_text)
                    } else {
                        context.getString(R.string.pat_completed_status_text)
                    }
            }
        }
    }
    return status
}

@Composable
fun ShowDidisFromTolaForBpcAddMoreScreen(
    didiTola: String,
    didiList: List<BpcNonSelectedDidiEntity>,
    modifier: Modifier,
    isForReplace: Boolean,
    expandedIds: List<Int>,
    isCheckedIds: List<Int>,
    onExpendClick: (Boolean, BpcNonSelectedDidiEntity) -> Unit,
    onItemClick: (Boolean, BpcNonSelectedDidiEntity) -> Unit
) {
    Column(modifier = Modifier) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_icn),
                contentDescription = "home image",
                modifier = Modifier
                    .size(18.dp),
                colorFilter = ColorFilter.tint(textColorBlueLight)
            )

            Text(
                text = didiTola,
                style = TextStyle(
                    color = black2,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = NotoSans
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(
                text = "${didiList.size}",
                style = TextStyle(
                    color = black2,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = NotoSans
                ),
                modifier = Modifier
                    .background(yellowBg, shape = CircleShape)
                    .circleLayout()
                    .padding(3.dp),
                textAlign = TextAlign.Start
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            didiList.forEachIndexed { index, didi ->
                ExpandableDidiItemCardForBpc(
                    didi = didi,
                    expanded = expandedIds.contains(didi.id),
                    modifier = modifier,
                    isForReplace = isForReplace,
                    isChecked = isCheckedIds.contains(didi.id),
                    onExpendClick = { expand, didiDetailModel ->
                        onExpendClick(expand, didiDetailModel)
                    },
                    onItemClick = { isChecked, didi ->
                        onItemClick(isChecked, didi)
                    }
                )
            }
        }
    }
}