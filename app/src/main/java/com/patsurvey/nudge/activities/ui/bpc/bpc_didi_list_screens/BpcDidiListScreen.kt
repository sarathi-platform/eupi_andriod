package com.patsurvey.nudge.activities.ui.bpc.bpc_didi_list_screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
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
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
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
import com.patsurvey.nudge.activities.ui.theme.inprogressYellow
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.smallTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleMediumWeight
import com.patsurvey.nudge.activities.ui.theme.textColorBlueLight
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.activities.ui.theme.yellowBg
import com.patsurvey.nudge.customviews.SearchWithFilterView
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.utils.ARG_FROM_PAT_DIDI_LIST_SCREEN
import com.patsurvey.nudge.utils.BlueButtonWithIconWithFixedWidth
import com.patsurvey.nudge.utils.ButtonNegativeForPAT
import com.patsurvey.nudge.utils.ButtonPositiveForPAT
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.TYPE_INCLUSION
import com.patsurvey.nudge.utils.VoEndorsementStatus
import com.patsurvey.nudge.utils.WealthRank

@Composable
fun BpcDidiListScreen(
    modifier: Modifier = Modifier,
    bpcDidiListViewModel: BpcDidiListViewModel,
    navController: NavHostController,
    villageId: Int,
    stepId: Int
) {

    val didis by bpcDidiListViewModel.didiList.collectAsState()

    val newFilteredTolaDidiList = bpcDidiListViewModel.filterTolaMapList
    val newFilteredDidiList = bpcDidiListViewModel.filterDidiList.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val localDensity = LocalDensity.current

    val focusManager = LocalFocusManager.current

    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    var filterSelected by remember {
        mutableStateOf(false)
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
            .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
            ) {

                Text(
                    text = stringResource(id = R.string.bpc_didi_screen_title),
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen.dp_6), horizontal = 32.dp)
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
                ) {

                    item {
                        Spacer(modifier = Modifier.height(14.dp).fillMaxWidth())
                    }

                    item {
                        SearchWithFilterView(
                            placeholderString = stringResource(id = R.string.search_didis),
                            filterSelected = filterSelected,
                            onFilterSelected = {
                                if (didis.isNotEmpty()) {
                                    filterSelected = !it
                                    bpcDidiListViewModel.filterList()
                                }
                            },
                            onSearchValueChange = {
                                bpcDidiListViewModel.performQuery(it, filterSelected)
                            }
                        )
                    }
                    
                    item { 
                        Spacer(modifier = Modifier.height(14.dp).fillMaxWidth())
                    }

                    item {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val count = bpcDidiListViewModel.pendingDidiCount.value
                            Text(
                                text = stringResource(
                                    id = if (count > 1) R.string.count_didis_pending_plural else R.string.count_didis_pending_singular,
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

                            BlueButtonWithIconWithFixedWidth(
                                modifier = Modifier
                                    .weight(0.5f),
                                buttonText = stringResource(R.string.add_more),
                                icon = Icons.Default.Add
                            ) {

                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(14.dp).fillMaxWidth())
                    }

                    if (filterSelected) {
                        itemsIndexed(
                            newFilteredTolaDidiList.keys.toList().reversed()
                        ) { index, didiKey ->
                            if (newFilteredTolaDidiList[didiKey]?.get(index)?.voEndorsementStatus == VoEndorsementStatus.NOT_STARTED.ordinal) {
                                bpcDidiListViewModel.pendingDidiCount.value++
                            }
                            ShowDidisFromTolaForBpc(
                                navController = navController,
                                viewModel = bpcDidiListViewModel,
                                didiTola = didiKey,
                                didiList = newFilteredTolaDidiList[didiKey]?.filter { it.wealth_ranking == WealthRank.POOR.rank }
                                    ?: emptyList(),
                                modifier = modifier,
                                onNavigate = {

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
                        itemsIndexed(newFilteredDidiList.value) { index, didi ->
                            if (didi.voEndorsementStatus == DidiEndorsementStatus.NOT_STARTED.ordinal) {
                                bpcDidiListViewModel.pendingDidiCount.value++
                            }
                            DidiItemCardForBpc(
                                navController = navController,
                                didi = didi,
                                modifier = Modifier,
                                viewModel = bpcDidiListViewModel
                            ) {

                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
        if (didis.isNotEmpty()) {
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

                positiveButtonText = stringResource(id = R.string.next),
                negativeButtonRequired = false,
                positiveButtonOnClick = {

                },
                negativeButtonOnClick = {/*Nothing to do here*/ }
            )
        }
    }
}

@Composable
fun DidiItemCardForBpc(
    navController: NavHostController,
    didi: DidiEntity,
    modifier: Modifier,
    viewModel: BpcDidiListViewModel,
    onItemClick: (DidiEntity) -> Unit
) {

    val didiMarkedNotAvailable = remember {
        mutableStateOf(didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal)
    }

    Card(
        elevation = 10.dp,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
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
                    Row(
                        modifier = Modifier
                            .layoutId("didiRow")
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = didi.name,
                            style = TextStyle(
                                color = textColorDark,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = NotoSans,
                                textAlign = TextAlign.Start
                            ),
                        )

                        if (didi.patSurveyStatus.equals(PatSurveyStatus.COMPLETED.ordinal)) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_completed_tick),
                                contentDescription = "home image",
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(30.dp)
                                    .padding(5.dp)
                                    .layoutId("successImage")
                            )
                        }

                        if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                            Text(
                                text = stringResource(R.string.pat_inprogresee_status_text),
                                style = smallTextStyle,
                                color = inprogressYellow,
                                modifier = Modifier
                                    .padding(5.dp)
                                    .layoutId("successImage")
                            )
                        }
                    }


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
                }
            }

            if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal ||
                didi.patSurveyStatus == PatSurveyStatus.NOT_STARTED.ordinal ||
                didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal ||
                didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal
            ) {
                Divider(
                    color = borderGreyLight,
                    thickness = 1.dp,
                    modifier = Modifier
                        .layoutId("divider")
                        .padding(vertical = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 16.dp)
                ) {
                    ButtonNegativeForPAT(
                        buttonTitle = stringResource(id = R.string.not_avaliable),
                        isArrowRequired = false,
                        color = if (didiMarkedNotAvailable.value) blueDark else languageItemActiveBg,
                        textColor = if (didiMarkedNotAvailable.value) white else blueDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .weight(1f)
                            .background(
                                if (didiMarkedNotAvailable.value
                                ) blueDark else languageItemActiveBg
                            )
                    ) {
                        didiMarkedNotAvailable.value = true
                        viewModel.setDidiAsUnavailable(didi.id)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    ButtonPositiveForPAT(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .weight(1f)
                            .background(
                                if (didiMarkedNotAvailable.value
                                ) languageItemActiveBg else blueDark
                            ),
                        buttonTitle = if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal
                            || didi.patSurveyStatus == PatSurveyStatus.NOT_STARTED.ordinal
                        )
                            stringResource(id = R.string.start_pat)
                        else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal
                            || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal
                        )
                            stringResource(id = R.string.continue_pat)
                        else "",
                        true,
                        color = if (!didiMarkedNotAvailable.value) blueDark else languageItemActiveBg,
                        textColor = if (!didiMarkedNotAvailable.value) white else blueDark,
                        iconTintColor = if (!didiMarkedNotAvailable.value) white else blueDark
                    ) {

                        if (didi.patSurveyStatus == PatSurveyStatus.NOT_STARTED.ordinal
                            || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal
                        ) {
                            if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal) {
                                didiMarkedNotAvailable.value = false
                            }
                            navController.navigate("didi_pat_summary/${didi.id}")

                        } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal) {
                            if (didi.section1Status == 0 || didi.section1Status == 1)
                                navController.navigate("yes_no_question_screen/${didi.id}/$TYPE_EXCLUSION")
                            else if (didi.section2Status == 0 || didi.section2Status == 1) navController.navigate(
                                "yes_no_question_screen/${didi.id}/$TYPE_INCLUSION"
                            )
                        }
                    }
                }
            } else {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .padding(horizontal = 20.dp)
                    .clickable {
                        navController.navigate("pat_complete_didi_summary_screen/${didi.id}/$ARG_FROM_PAT_DIDI_LIST_SCREEN")
                    }
                    .then(modifier),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.show),
                        style = smallTextStyleMediumWeight,
                        color = textColorDark,
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = blueDark,
                        modifier = Modifier
                            .absolutePadding(top = 4.dp, left = 2.dp)
                            .size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ShowDidisFromTolaForBpc(
    navController: NavHostController,
    viewModel: BpcDidiListViewModel,
    didiTola: String,
    didiList: List<DidiEntity>,
    modifier: Modifier,
    onNavigate: (String) -> Unit
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
                DidiItemCardForBpc(
                    navController = navController,
                    didi = didi,
                    modifier = modifier,
                    viewModel = viewModel,
                    onItemClick = {
                        //TODO navigate to summary screen for Endorsement
                    }
                )
            }

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



        constrain(didiImage) {
            top.linkTo(parent.top, margin = 12.dp)
            start.linkTo(parent.start, margin = 10.dp)
        }
        constrain(didiName) {
            start.linkTo(didiImage.end, 10.dp)
            top.linkTo(parent.top, 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(didiRow) {
            start.linkTo(didiImage.end, 10.dp)
            top.linkTo(parent.top, 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(village) {
            start.linkTo(homeImage.end, margin = 10.dp)
            top.linkTo(didiName.bottom)
            width = Dimension.fillToConstraints
        }
        constrain(homeImage) {
            top.linkTo(village.top)
            bottom.linkTo(village.bottom)
            start.linkTo(didiName.start)
        }
    }
}