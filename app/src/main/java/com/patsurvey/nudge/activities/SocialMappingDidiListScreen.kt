package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.CardArrow
import com.patsurvey.nudge.customviews.ModuleAddedSuccessView
import com.patsurvey.nudge.customviews.SearchWithFilterView
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.utils.*

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SocialMappingDidiListScreen(
    navController: NavHostController, modifier: Modifier,
    didiViewModel: AddDidiViewModel, villageId: Int, stepId: Int
) {
    val didiList = didiViewModel.didiList
    val newFilteredDidiList = didiViewModel.filterDidiList
    val newFilteredTolaDidiList = didiViewModel.filterTolaMapList
    val localDensity = LocalDensity.current
    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    val expandedIds = remember {
        mutableStateListOf<Int>()
    }
    var filterSelected by remember {
        mutableStateOf(false)
    }


    LaunchedEffect(key1 = true) {
        if (didiViewModel.prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)) {
            didiViewModel.getPatStepStatus(stepId) {
                if (it)
                    navController.navigate("pat_survey_summary/$stepId/$it")
            }
        }
    }

    LaunchedEffect(key1 = true) {
        didiViewModel.isSocialMappingComplete(stepId)
        didiViewModel.isVoEndorsementCompleteForVillage(villageId)
    }
    var completeTolaAdditionClicked by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    val focusManager = LocalFocusManager.current
    BackHandler() {
        if (completeTolaAdditionClicked)
            completeTolaAdditionClicked = false
        else {
            navController.popBackStack()
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(true) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .then(modifier)
    ) {
        val (bottomActionBox, mainBox) = createRefs()
        Box(modifier = Modifier
            .constrainAs(mainBox) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
            .padding(top = 14.dp)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                VOAndVillageBoxView(
                    prefRepo = didiViewModel.prefRepo,
                    modifier = Modifier.fillMaxWidth(),
                )

                val count = didiList.value.filter { it.needsToPost }.size
                ModuleAddedSuccessView(
                    completeAdditionClicked = completeTolaAdditionClicked,
                    message = stringResource(
                        if (count < 2) R.string.didi_conirmation_text_singular else R.string.didi_conirmation_text_plural,
                        didiList.value.filter { it.needsToPost }.size
                    ),
                    modifier = Modifier.padding(vertical = (screenHeight / 4).dp)
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = white)
                        .weight(1f)
                        .padding(
                            bottom =
                            if (!didiViewModel.prefRepo
                                    .getFromPage()
                                    .equals(ARG_FROM_HOME, true)
                            ) {
                                0.dp
                            } else {
                                50.dp
                            }
                        ),
                    contentPadding = PaddingValues(bottom = 10.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                        ) {
                            val title = if (didiViewModel.prefRepo.getFromPage()
                                    .equals(ARG_FROM_PAT_SURVEY, true))
                                stringResource(R.string.pat_survey_title)
                            else if (!didiViewModel.prefRepo.getFromPage()
                                    .equals(ARG_FROM_HOME, true))
                                stringResource(R.string.social_mapping)
                            else
                                stringResource(R.string.didis_item_text_plural)
                            MainTitle(title, Modifier.weight(0.5f))
                            if (!didiViewModel.prefRepo.getFromPage().equals(ARG_FROM_HOME, true)
                                && !didiViewModel.prefRepo.getFromPage()
                                    .equals(ARG_FROM_PAT_SURVEY, true)
                            ) {
                                if (!didiViewModel.isVoEndorsementComplete.value) {
                                    BlueButtonWithIconWithFixedWidth(
                                        modifier = Modifier
                                            .weight(0.5f),
                                        buttonText = stringResource(id = R.string.add_didi),
                                        icon = Icons.Default.Add
                                    ) {
                                        didiViewModel.resetAllFields()
                                        navController.navigate("add_didi_graph/$ADD_DIDI_BLANK_ID") {
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item {
                        SearchWithFilterView(placeholderString = stringResource(id = R.string.search_didis),
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                            ,
                            filterSelected = filterSelected,
                            onFilterSelected = {
                                if (didiList.value.isNotEmpty()) {
                                    filterSelected = !it
                                    didiViewModel.filterList()
                                }
                            }, onSearchValueChange = {
                                didiViewModel.performQuery(it, filterSelected)

                            })
                    }

                    item {
                        AnimatedVisibility(
                            visible = !filterSelected,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            color = greenOnline,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = NotoSans
                                        )
                                    ) {
                                        append(if (!didiViewModel.prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true))
                                            "${newFilteredDidiList.size}"
                                        else
                                            "${didiViewModel.pendingDidiCount.value}")
                                    }
                                    append(
                                        " ${
                                            if (!didiViewModel.prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true))
                                                pluralStringResource(
                                                    id = R.plurals.didis_added,
                                                    newFilteredDidiList.size
                                                )
                                            else{
                                                pluralStringResource(id =  R.plurals.poor_didis_pending_text, count = didiViewModel.pendingDidiCount.value)
                                            }
                                        }"
                                    )
                                },
                                style = TextStyle(
                                    color = textColorDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = NotoSans
                                ),
                                modifier = Modifier
                                    .align(Alignment.Start)
                            )
                        }
                    }
                    if (filterSelected) {
                        itemsIndexed(
                            newFilteredTolaDidiList.keys.toList()
                        ) { index, didiKey ->
                            ShowDidisFromTola(navController,didiViewModel,
                                didiTola = didiKey,
                                didiList = if (didiViewModel.prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true))
                                    newFilteredTolaDidiList[didiKey]?.filter { it.wealth_ranking == WealthRank.POOR.rank } ?: emptyList()
                                else  newFilteredTolaDidiList[didiKey] ?: emptyList(),
                                modifier = modifier,
                                expandedIds = expandedIds,
                                onExpendClick = { expand, didiDetailModel ->
                                    if (expandedIds.contains(didiDetailModel.id)) {
                                        expandedIds.remove(didiDetailModel.id)
                                    } else {
                                        expandedIds.add(didiDetailModel.id)
                                    }
                                },
                                onNavigate = {
                                    if(!didiViewModel.prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true) && !didiViewModel.isSocialMappingComplete.value) {
                                        navController.navigate("add_didi_graph/${it.id}") {
                                            launchSingleTop = true
                                        }
                                    }else if(didiViewModel.prefRepo.getFromPage().equals(
                                            ARG_FROM_HOME, true)){
                                        navController.navigate("add_didi_graph/${it.id}") {
                                            launchSingleTop = true
                                        }
                                    }
                                },
                                onDeleteClicked = { didi ->
                                    didiViewModel.deleteDidiOffline(didi, object : NetworkCallbackListener{
                                        override fun onSuccess() {
                                            showCustomToast(context, "Didi Deleted Successfully")
                                        }

                                        override fun onFailed() {
                                            TODO("Not yet implemented")
                                        }

                                    })
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
                        itemsIndexed(if (didiViewModel.prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)) newFilteredDidiList.filter { it.wealth_ranking == WealthRank.POOR.rank } else newFilteredDidiList) { index, didi ->
                            DidiItemCard(navController, didiViewModel, didi, expandedIds.contains(didi.id), modifier,
                                onExpendClick = { expand, didiDetailModel ->
                                    if (expandedIds.contains(didiDetailModel.id)) {
                                        expandedIds.remove(didiDetailModel.id)
                                    } else {
                                        expandedIds.add(didiDetailModel.id)
                                    }
                                },
                                onItemClick = { didi ->
                                    if(!didiViewModel.prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true) && !didiViewModel.isSocialMappingComplete.value) {
                                        navController.navigate("add_didi_graph/${didi.id}") {
                                            launchSingleTop = true
                                        }
                                    }else if(didiViewModel.prefRepo.getFromPage().equals(
                                            ARG_FROM_HOME, true)){
                                        navController.navigate("add_didi_graph/${didi.id}") {
                                            launchSingleTop = true
                                        }
                                    }
                                },
                                onDeleteClicked = { didi ->
                                    didiViewModel.deleteDidiOffline(didi, object : NetworkCallbackListener{
                                        override fun onSuccess() {
                                            showCustomToast(context, "Didi Deleted Successfully")
                                        }

                                        override fun onFailed() {
                                            TODO("Not yet implemented")
                                        }

                                    })
                                }
                            )
                        }
                        if (!didiViewModel.isSocialMappingComplete.value)
                            item { Spacer(modifier = Modifier.height(bottomPadding)) }
                    }
                }
            }
        }

       // Didi Add\Edit and Wealth Ranking
        if (didiList.value.isNotEmpty() && !didiViewModel.isSocialMappingComplete.value) {
            if (!didiViewModel.prefRepo.getFromPage().equals(ARG_FROM_HOME, true)
                && !didiViewModel.prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)) {
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
                    positiveButtonText = if (completeTolaAdditionClicked) stringResource(id = R.string.complete_social_walk_text) else stringResource(
                        id = R.string.complete_didi_addition
                    ),
                    positiveButtonOnClick = {
                        if (completeTolaAdditionClicked) {
                            //TODO Integrate Api when backend fixes the response.
                            if ((context as MainActivity).isOnline.value ?: false) {
                                didiViewModel.addDidisToNetwork( object : NetworkCallbackListener {
                                    override fun onSuccess() {
                                    }

                                    override fun onFailed() {
                                        showCustomToast(context, SYNC_FAILED)
                                    }
                                })
                                didiViewModel.deleteDidiFromNetwork(object : NetworkCallbackListener {
                                    override fun onSuccess() {
                                        showCustomToast(context, "Didi Deleted")
                                    }

                                    override fun onFailed() {
                                        showCustomToast(context, SYNC_FAILED)
                                    }
                                })
                                didiViewModel.callWorkFlowAPI(villageId, stepId,  object : NetworkCallbackListener{
                                    override fun onSuccess() {
                                    }

                                    override fun onFailed() {
                                        showCustomToast(context, SYNC_FAILED)
                                    }
                                })
                            }
                            /*if ((context as MainActivity).isOnline.value ?: false) {
                                didiViewModel.addDidisToNetwork()
                                didiViewModel.callWorkFlowAPI(villageId, stepId)
                            }*/
//                            didiViewModel.updateDidisNeedTOPostList(villageId)
                            didiViewModel.markSocialMappingComplete(villageId, stepId)
                            navController.navigate(
                                "sm_step_completion_screen/${
                                    context.getString(R.string.social_mapping_completed_message)
                                        .replace(
                                            "{VILLAGE_NAME}",
                                            didiViewModel.prefRepo.getSelectedVillage().name ?: ""
                                        )
                                }"
                            )

                        } else {
                            completeTolaAdditionClicked = true
                        }
                    },
                    negativeButtonOnClick = {}
                )
            }

        }

        // Didi PAT Survey

        if (didiList.value.isNotEmpty() && didiViewModel.pendingDidiCount.value == 0) {
            if (didiViewModel.prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true) && didiViewModel.pendingDidiCount.value == 0) {
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
                    positiveButtonText = stringResource(
                        id = R.string.complete
                    ),
                    positiveButtonOnClick = {
                        didiViewModel.getPatStepStatus(stepId) {
                            Log.d(TAG, "SocialMappingDidiListScreen: ${it}")
                            navController.navigate("pat_survey_summary/$stepId/$it")
                        }
                    },
                    negativeButtonOnClick = {}
                )
            }

        }
    }
}

@Composable
fun ShowFilteredList(
    filteredDidiList: Map<String, List<DidiEntity>>,
    expandedIds: List<Int>,
    modifier: Modifier,
    onExpendClick: (Boolean, DidiEntity) -> Unit
) {
    Log.i("ShowFilteredList", "show tolaa :")
    filteredDidiList.entries.forEach {
        Log.i("ShowFilteredList", "show tola : ${it.key}")
//        ShowDidisFromTola(it.key, it.value, modifier, expandedIds, onExpendClick)
    }
}

@Composable
fun ShowDidisFromTola(
    navController:NavHostController,
    didiViewModel: AddDidiViewModel,
    didiTola: String,
    didiList: List<DidiEntity>,
    modifier: Modifier,
    expandedIds: List<Int>,
    onExpendClick: (Boolean, DidiEntity) -> Unit,
    onNavigate: (DidiEntity) -> Unit,
    onDeleteClicked: (DidiEntity) -> Unit
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
                DidiItemCard(navController,didiViewModel,didi, expandedIds.contains(didi.id), modifier,
                    onExpendClick = { expand, didiDetailModel ->
                        onExpendClick(expand, didiDetailModel)
                    },
                    onItemClick = { didi ->
                        onNavigate(didi)
                    },
                    onDeleteClicked = { didi ->
                        onDeleteClicked (didi)
                    })
            }

        }
    }
}

fun Modifier.circleLayout() =
    layout { measurable, constraints ->
        // Measure the composable
        val placeable = measurable.measure(constraints)

        //get the current max dimension to assign width=height
        val currentHeight = placeable.height
        val currentWidth = placeable.width
        val newDiameter = maxOf(currentHeight, currentWidth)

        //assign the dimension and the center position
        layout(newDiameter, newDiameter) {
            // Where the composable gets placed
            placeable.placeRelative(
                (newDiameter - currentWidth) / 2,
                (newDiameter - currentHeight) / 2
            )
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
        val expendArrowImageEnd = createRefFor("expendArrowImageEnd")
        val moreActionIcon = createRefFor("moreActionIcon")
        val moreDropDown = createRefFor("moreDropDown")
        val didiDetailLayout = createRefFor("didiDetailLayout")



        constrain(didiImage) {
            top.linkTo(parent.top, margin = 12.dp)
            start.linkTo(parent.start, margin = 10.dp)
        }
        constrain(didiName) {
            start.linkTo(didiImage.end, 10.dp)
            top.linkTo(parent.top, 10.dp)
            end.linkTo(moreActionIcon.start, margin = 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(didiRow) {
            start.linkTo(didiImage.end, 10.dp)
            top.linkTo(parent.top, 10.dp)
            end.linkTo(moreActionIcon.start, margin = 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(village) {
            start.linkTo(homeImage.end, margin = 10.dp)
            top.linkTo(didiName.bottom)
            end.linkTo(moreActionIcon.start, margin = 10.dp)
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
            end.linkTo(moreActionIcon.start)
        }

        constrain(expendArrowImageEnd) {
            top.linkTo(didiName.top)
            bottom.linkTo(village.bottom)
            end.linkTo(parent.end, margin = 10.dp)
        }

        constrain(moreActionIcon) {
            top.linkTo(didiName.top)
            bottom.linkTo(village.bottom)
            end.linkTo(parent.end, margin = 10.dp)
        }

        constrain(moreDropDown) {
            top.linkTo(moreActionIcon.bottom)
            end.linkTo(moreActionIcon.end)
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

@Composable
fun DidiItemCard(
    navController:NavHostController,
    didiViewModel: AddDidiViewModel,
    didi: DidiEntity,
    expanded: Boolean,
    modifier: Modifier,
    onExpendClick: (Boolean, DidiEntity) -> Unit,
    onItemClick: (DidiEntity) -> Unit,
    onDeleteClicked: (DidiEntity) -> Unit
) {

    val transition = updateTransition(expanded, label = "transition")

    val didiMarkedNotAvailable  = remember {
        mutableStateOf(didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal)
    }

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

    val showMenu = remember {
        mutableStateOf(false)
    }

    Card(
        elevation = 10.dp,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
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
                    Row(modifier = Modifier
                        .layoutId("didiRow")
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = didi.name,
                            style = TextStyle(
                                color = animateColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = NotoSans,
                                textAlign = TextAlign.Start
                            ),
                        )

                        if(didiViewModel.prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)) {
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
                                Text(text = stringResource(R.string.pat_inprogresee_status_text), style = smallTextStyle, color = inprogressYellow, modifier = Modifier
                                    .padding(5.dp)
                                    .layoutId("successImage"))
                            }
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
                    if (!didiViewModel.prefRepo.getFromPage()
                            .equals(ARG_FROM_PAT_SURVEY, true)
                    ) {

                        IconButton(onClick = {
                                             showMenu.value = !showMenu.value
                        }, modifier = Modifier
                            .layoutId("moreActionIcon")
                            .visible(
                                !didiViewModel.prefRepo
                                    .getFromPage()
                                    .equals(
                                        ARG_FROM_PAT_SURVEY,
                                        true
                                    ) && !didiViewModel.isSocialMappingComplete.value
                            )) {
                            Icon(painter = painterResource(id = R.drawable.baseline_more_icon), contentDescription = "more action", tint = textColorDark)
                        }

                        Box(modifier = Modifier.layoutId("moreDropDown")) {
                            DropdownMenu(
                                expanded = showMenu.value,
                                onDismissRequest = { showMenu.value = false },
                                modifier = Modifier
                            ) {
                                DropdownMenuItem(onClick = { onItemClick(didi) }) {
                                    Text(
                                        text = "Edit",
                                        style = quesOptionTextStyle,
                                        color = textColorDark
                                    )
                                }
                                DropdownMenuItem(onClick = {
                                    onDeleteClicked(didi)
                                }) {
                                    Text(
                                        text = "Delete",
                                        style = quesOptionTextStyle,
                                        color = textColorDark
                                    )
                                }
                            }
                        }

                        CardArrow(
                            modifier = Modifier.layoutId(if (!didiViewModel.prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)
                                && !didiViewModel.isSocialMappingComplete.value)"expendArrowImage" else "expendArrowImageEnd"),
                            degrees = arrowRotationDegree,
                            iconColor = animateColor,
                            onClick = { onExpendClick(expanded, didi) }
                        )

                        DidiDetailExpendableContent(
                            modifier = Modifier.layoutId("didiDetailLayout"),
                            didi,
                            animateInt == 1
                        )
                    }
                }
            }
            if (didiViewModel.prefRepo.getFromPage()
                    .equals(ARG_FROM_PAT_SURVEY, true)
            ) {
                if(didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal ||
                    didi.patSurveyStatus == PatSurveyStatus.NOT_STARTED.ordinal ||
                    didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal ||
                    didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal ) {
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
                        ){
                            didiMarkedNotAvailable.value = true
                            didiViewModel.setDidiAsUnavailable(didi.id)
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
                            buttonTitle = if(didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal
                                || didi.patSurveyStatus == PatSurveyStatus.NOT_STARTED.ordinal)
                                stringResource(id = R.string.start_pat)
                            else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal
                                || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal)
                                stringResource(id = R.string.continue_pat)
                            else "",
                            true,
                            color = if (!didiMarkedNotAvailable.value) blueDark else languageItemActiveBg,
                            textColor = if (!didiMarkedNotAvailable.value) white else blueDark,
                            iconTintColor = if (!didiMarkedNotAvailable.value) white else blueDark
                        ) {

                            if (didi.patSurveyStatus == PatSurveyStatus.NOT_STARTED.ordinal
                                || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal) {
                                if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal) {
                                    didiMarkedNotAvailable.value = false
                                }
                                navController.navigate("didi_pat_summary/${didi.id}")

                            } else if (didi.patSurveyStatus == PatSurveyStatus.INPROGRESS.ordinal || didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE_WITH_CONTINUE.ordinal  ) {
                                if (didi.section1Status == 0 || didi.section1Status == 1)
                                    navController.navigate("yes_no_question_screen/${didi.id}/$TYPE_EXCLUSION")
                                else if (didi.section2Status == 0 || didi.section2Status == 1) navController.navigate("yes_no_question_screen/${didi.id}/$TYPE_INCLUSION")
                            }
                        }
                    }
                }else{
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .padding(horizontal = 20.dp)
                        .clickable {
                            navController.navigate("pat_complete_didi_summary_screen/${didi.id}/${ARG_FROM_PAT_DIDI_LIST_SCREEN}")
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
}

@Composable
fun DidiItemCard(
    didi: DidiEntity,
    expanded: Boolean,
    modifier: Modifier,
    onExpendClick: (Boolean, DidiEntity) -> Unit,
    onItemClick: (DidiEntity) -> Unit
) {

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
    Card(
        elevation = 10.dp,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
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

                        DidiDetailExpendableContent(
                            modifier = Modifier.layoutId("didiDetailLayout"),
                            didi,
                            animateInt == 1
                        )
                }
            }
        }
    }
}

@Composable
fun DidiDetailExpendableContent(modifier: Modifier, didi: DidiEntity, expended: Boolean) {
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
                text = didi.castName?: BLANK_STRING,
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
                text = getLatestStatusText(context, didi),
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

fun getLatestStatusText(context: Context, didi: DidiEntity): String {
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
                status = if (didi.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal || didi.voEndorsementStatus == DidiEndorsementStatus.REJECTED.ordinal) {
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
fun TolaWithImage(toal: String, modifier: Modifier) {
    Row(verticalAlignment = CenterVertically, modifier = modifier.padding(top = 3.dp)) {
        Image(
            painter = painterResource(id = R.drawable.home_icn),
            contentDescription = "home image",
            modifier = Modifier
                .width(18.dp)
                .height(14.dp),
            colorFilter = ColorFilter.tint(textColorBlueLight)
        )

        Text(
            text = toal,
            style = TextStyle(
                color = textColorBlueLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = NotoSans
            ),
            modifier = Modifier.padding(start = 5.dp)
        )

    }
}

@Composable
fun CircularDidiImage(modifier: Modifier) {
    Box(
        modifier = modifier
            .then(modifier)
            .clip(CircleShape)
            .width(44.dp)
            .height(44.dp)
            .background(color = yellowBg),
    ) {
        Image(
            painter = painterResource(id = R.drawable.didi_icon),
            contentDescription = "didi image",
            modifier = Modifier
                .align(Alignment.Center)
                .width(25.dp)
                .height(28.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SocialMappingDidiListPreview() {
    SocialMappingDidiListScreen(
        navController = rememberNavController(),
        modifier = Modifier,
        didiViewModel = viewModel(),
        -1,
        -1
    )
}