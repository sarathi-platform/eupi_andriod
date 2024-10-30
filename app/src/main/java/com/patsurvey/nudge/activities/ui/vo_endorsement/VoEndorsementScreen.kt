package com.patsurvey.nudge.activities.ui.vo_endorsement

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.CircularDidiImage
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.acceptEndorsementTextColor
import com.patsurvey.nudge.activities.ui.theme.bgGreyLight
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.borderGreyLight
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.rejectEndorsementTextColor
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleMediumWeight
import com.patsurvey.nudge.activities.ui.theme.textColorBlueLight
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.activities.ui.theme.yellowBg
import com.patsurvey.nudge.customviews.SearchWithFilterView
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.data.prefs.SharedPrefs.Companion.PREF_KEY_VO_SUMMARY_OPEN_FROM
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.ButtonPositiveForVo
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.showDidiImageDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.LocalContext
import com.nudge.core.ui.commonUi.BottomSheetScaffoldComponent
import com.nudge.core.ui.commonUi.rememberCustomBottomSheetScaffoldProperties
import com.patsurvey.nudge.utils.NudgeCore.getVoNameForState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VoEndorsementScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: VoEndorsementScreenViewModel,
    stepId: Int
) {
    val showLoader = remember {
        mutableStateOf(false)
    }
    var context = LocalContext.current

    val didis by viewModel.didiList.collectAsState()

    val newFilteredTolaDidiList = viewModel.filterTolaMapList
    val newFilteredDidiList = viewModel.filterDidiList.collectAsState()

//    val _pendingDidiCount = remember {
//        mutableStateOf(newFilteredDidiList.value.size)
//    }
    val coroutineScope = rememberCoroutineScope()

    val localDensity = LocalDensity.current

    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    var filterSelected by remember {
        mutableStateOf(false)
    }
    val customBottomSheetScaffoldProperties = rememberCustomBottomSheetScaffoldProperties()


    LaunchedEffect(true) {
        viewModel.updateFilterDidiList()
    }

    BackHandler {
        coroutineScope.launch {
            delay(100)
            navController.popBackStack()
        }
    }

    if(viewModel.showDidiImageDialog.value){
        viewModel.dialogDidiEntity.value?.let {
            showDidiImageDialog(didi = it){
                viewModel.showDidiImageDialog.value = false
            }
        }
    }

    BottomSheetScaffoldComponent(
        bottomSheetScaffoldProperties = customBottomSheetScaffoldProperties,

        bottomSheetContentItemList = context.resources.getStringArray(R.array.didi_sorted_array).toList(),
        selectedIndex =viewModel.selectedSortIndex.value,
        onBottomSheetItemSelected = {
            viewModel. selectedSortIndex.value=it
            viewModel.didiSortedList(it, filterSelected)
        }
    )
    {
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
                        .fillMaxSize()
                ) {

                    VOAndVillageBoxView(
                        prefRepo = viewModel.repository.prefRepo,
                        modifier = Modifier.fillMaxWidth(),
                        startPadding = 0.dp
                    )

                    if (showLoader.value) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(top = 30.dp)
                        ) {
                            CircularProgressIndicator(
                                color = blueDark,
                                modifier = Modifier
                                    .size(28.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = white)
                                .weight(1f),
                            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 4.dp)
                        ) {

                            item {
                                Text(
                                    getVoNameForState(
                                        context,
                                        viewModel.getStateId(),
                                        R.plurals.vo_endorsement_screen_title
                                    ),
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontFamily = NotoSans,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(vertical = dimensionResource(id = R.dimen.dp_6))
                                )
                            }
                            item {
                                SearchWithFilterView(
                                    placeholderString = stringResource(id = R.string.search_didis),
                                    filterSelected = filterSelected,
                                    onFilterSelected = {
                                        if (newFilteredDidiList.value.isNotEmpty()) {
                                            filterSelected = !it
                                            viewModel.filterList()
                                        }
                                    },
                                    onSearchValueChange = {
                                        viewModel.performQuery(it, filterSelected)
                                    },
                                    onSortedSelected = {
                                        coroutineScope.launch {
                                            customBottomSheetScaffoldProperties.sheetState.show()
                                        }
                                    }

                                )
                            }

                            item {
                                val count =
                                    newFilteredDidiList.value.filter { it.voEndorsementStatus == DidiEndorsementStatus.NOT_STARTED.ordinal }.size
                                Text(
                                    text = if (count <= 1) stringResource(
                                        id = R.string.count_didis_pending_singular,
                                        count
                                    )
                                    else stringResource(
                                        id = R.string.count_didis_pending_plural,
                                        count
                                    ),
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontFamily = NotoSans,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(vertical = dimensionResource(id = R.dimen.dp_6))
                                )
                            }

                            if (filterSelected) {
                                itemsIndexed(
                                    newFilteredTolaDidiList.keys.toList().reversed()
                                ) { index, didiKey ->
                                    ShowDidisFromTolaForVo(
                                        navController = navController,
                                        viewModel = viewModel,
                                        didiTola = didiKey,
                                        didiList = newFilteredTolaDidiList[didiKey]?.filter { it.wealth_ranking == WealthRank.POOR.rank }
                                            ?: emptyList(),
                                        modifier = modifier,
                                        onNavigate = {
                                            viewModel.repository.prefRepo.savePref(
                                                PREF_KEY_VO_SUMMARY_OPEN_FROM,
                                                PageFrom.VO_ENDORSEMENT_LIST_PAGE.ordinal
                                            )
                                            navController.navigate(
                                                "vo_endorsement_summary_screen/${
                                                    newFilteredTolaDidiList[didiKey]?.get(
                                                        index
                                                    )?.id
                                                }/${newFilteredTolaDidiList[didiKey]?.get(index)?.voEndorsementStatus}"
                                            )
                                            viewModel.performQuery(BLANK_STRING, filterSelected)
                                        },
                                        onCircularImageClick = { didiEntity ->
                                            viewModel.showDidiImageDialog.value = true
                                            viewModel.dialogDidiEntity.value = didiEntity
                                        }
                                    )
                                    if (index < newFilteredTolaDidiList.keys.size - 1) {
                                        Divider(
                                            color = borderGreyLight,
                                            thickness = 1.dp,
                                            modifier = Modifier.padding(
                                                top = 22.dp,
                                                bottom = 1.dp
                                            )
                                        )
                                    }
                                }
                            } else {
                                itemsIndexed(newFilteredDidiList.value) { index, didi ->
                                    DidiItemCardForVo(
                                        navController = navController,
                                        didi = didi,
                                        modifier = modifier,
                                        onItemClick = {
                                            viewModel.repository.prefRepo.savePref(
                                                PREF_KEY_VO_SUMMARY_OPEN_FROM,
                                                PageFrom.VO_ENDORSEMENT_LIST_PAGE.ordinal
                                            )
                                            navController.navigate("vo_endorsement_summary_screen/${didi.id}/${didi.voEndorsementStatus}")
                                            viewModel.performQuery(BLANK_STRING, filterSelected)
                                        },
                                        onCircularImageClick = { didiEntity ->
                                            viewModel.showDidiImageDialog.value = true
                                            viewModel.dialogDidiEntity.value = didiEntity
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                }
            }
            if (didis.filter { it.voEndorsementStatus == DidiEndorsementStatus.NOT_STARTED.ordinal }
                    .isEmpty()) {
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
                        NudgeLogger.d("VoEndorsementScreen", "Next Button Clicked")
                        viewModel.performQuery(BLANK_STRING, filterSelected)
                        val stepStatus = false
                        navController.navigate("vo_endorsement_survey_summary/$stepId/$stepStatus")
                    },
                    negativeButtonOnClick = {/*Nothing to do here*/ }
                )
            }
        }
    }
}

@Composable
fun DidiItemCardForVo(
    navController: NavHostController,
    didi: DidiEntity,
    modifier: Modifier,
    onItemClick: (DidiEntity) -> Unit,
    onCircularImageClick:(DidiEntity) ->Unit
) {

    Card(
        elevation = 10.dp,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(bgGreyLight, RoundedCornerShape(6.dp))
            .border(width = 1.dp, color = bgGreyLight, shape = RoundedCornerShape(6.dp))
            .clickable {
                if (didi.voEndorsementStatus != DidiEndorsementStatus.NOT_STARTED.ordinal)
                    onItemClick(didi)
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
                        didi,
                        modifier = Modifier.layoutId("didiImage")
                    ){
                        onCircularImageClick(didi)
                    }
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

                        //TODO figureout a way to save voendorsement status.
                        if (didi.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal || didi.voEndorsementStatus == DidiEndorsementStatus.REJECTED.ordinal) {
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
                    }

                    Text(
                        text = didi.guardianName,
                        style = TextStyle(
                            color = textColorBlueLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.layoutId("homeImage")
                    )

                    Text(
                        text = didi.address,
                        style = TextStyle(
                            color = textColorBlueLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSans
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.layoutId("houseNumber_1")
                    )


                    Divider(
                        color = borderGreyLight,
                        thickness = 1.dp,
                        modifier = Modifier
                            .layoutId("divider")
                            .padding(bottom = 14.dp, top = 14.dp)
                    )
                }
            }
            if (didi.voEndorsementStatus == DidiEndorsementStatus.NOT_STARTED.ordinal) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 10.dp)
                ) {
                    ButtonPositiveForVo(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .weight(1f)
                            .background(blueDark),
                        buttonTitle = stringResource(id = R.string.start_endorsement),
                        true,
                        color = blueDark,
                        textColor = white,
                        iconTintColor = white
                    ) {
                        onItemClick(didi)
                    }
                }
            } else {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 10.dp)
                    .clickable {
                        onItemClick(didi)
                    }
                    .then(modifier),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = stringResource(id = if (didi.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal) R.string.endorsed else R.string.rejected),
                        style = smallTextStyleMediumWeight,
                        color = if (didi.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal) acceptEndorsementTextColor else rejectEndorsementTextColor,
                    )

                    Row() {
                        Text(
                            text = stringResource(id = R.string.show),
                            style = smallTextStyleMediumWeight,
                            color = textColorDark,
                        )
                        Spacer(modifier = Modifier.width(2.dp))
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
}

private fun decoupledConstraints(): ConstraintSet {
    return ConstraintSet {
        val divider = createRefFor("divider")
        val didiImage = createRefFor("didiImage")
        val didiName = createRefFor("didiName")
        val didiRow = createRefFor("didiRow")
        val homeImage = createRefFor("homeImage")
        val village = createRefFor("village")
        val houseNumber_1 = createRefFor("houseNumber_1")
        val expendArrowImage = createRefFor("expendArrowImage")
        val didiDetailLayout = createRefFor("didiDetailLayout")

        constrain(divider) {
            top.linkTo(houseNumber_1.bottom)
            end.linkTo(parent.end)
            start.linkTo(parent.start)
        }

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
            start.linkTo(didiImage.end, 6.dp)
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
        constrain(houseNumber_1) {
            start.linkTo(didiImage.end,10.dp)
            top.linkTo(village.bottom)
        }
        constrain(homeImage) {
            start.linkTo(didiImage.end, margin = 10.dp)
            top.linkTo(didiName.bottom)
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

@Composable
fun ShowDidisFromTolaForVo(
    navController: NavHostController,
    viewModel: VoEndorsementScreenViewModel,
    didiTola: String,
    didiList: List<DidiEntity>,
    modifier: Modifier,
    onNavigate: (String) -> Unit,
    onCircularImageClick: (DidiEntity) -> Unit
) {
    Column(modifier = Modifier) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 16.dp, bottom = 10.dp, top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_icn),
                contentDescription = "home image",
                modifier = Modifier
                    .size(18.dp),
                colorFilter = ColorFilter.tint(textColorBlueLight)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = didiTola,
                style = TextStyle(
                    color = textColorDark,
                    fontSize = 16.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(end = 10.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = yellowBg,
                        shape = CircleShape
                    )
                    .background(
                        yellowBg,
                        shape = CircleShape
                    )
                    .padding(6.dp)
                    .size(20.dp)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${didiList.size}",
                    color = greenOnline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .absolutePadding(bottom = 2.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = NotoSans,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            didiList.forEachIndexed { index, didi ->
                DidiItemCardForVo(
                    navController = navController,
                    didi = didi,
                    modifier = modifier,
                    onItemClick = {
                        onNavigate("")
                    },
                    onCircularImageClick = {
                        onCircularImageClick(didi)
                    }
                )
            }
        }
    }
}
