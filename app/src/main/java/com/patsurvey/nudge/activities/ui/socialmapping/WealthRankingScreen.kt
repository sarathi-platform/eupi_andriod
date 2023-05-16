package com.patsurvey.nudge.activities.ui.socialmapping

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.google.gson.Gson
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.DidiItemCard
import com.patsurvey.nudge.activities.circleLayout
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.SearchWithFilterView
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.intefaces.LocalDbListener
import com.patsurvey.nudge.utils.*

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun WealthRankingScreen(
    navController: NavController,
    viewModel: WealthRankingViewModel,
    modifier: Modifier,
    villageId: Int,
    stepId: Int,
) {
    val didis by viewModel.didiList.collectAsState()
    val expandedCardIds by viewModel.expandedCardIdsList.collectAsState()

    var completeStepAdditionClicked by remember { mutableStateOf(false) }

    val newFilteredTolaDidiList = viewModel.filterTolaMapList
    val newFilteredDidiList = viewModel.filterDidiList

    val localDensity = LocalDensity.current

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
            .padding(horizontal = 16.dp)
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
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                VOAndVillageBoxView(
                    prefRepo = viewModel.prefRepo,
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(
                    modifier =
                    Modifier
                        .padding(bottom = bottomPadding)
                        .fillMaxWidth()
                        .background(color = white)
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 10.dp),/*
                    verticalArrangement = Arrangement.spacedBy(10.dp)*/
                ) {
                    item {
                        Text(
                            text = stringResource(id = R.string.particaptory_wealth_ranking_text),
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
                        SearchWithFilterView(placeholderString = stringResource(id = R.string.search_didis),
                            modifier = Modifier.padding(vertical = 10.dp),
                            filterSelected = filterSelected,
                            onFilterSelected = {
                                if (didis.isNotEmpty()) {
                                    filterSelected = !it
                                    viewModel.filterList()
                                }
                            }, onSearchValueChange = {
                                viewModel.performQuery(it, filterSelected)
                            }
                        )
                    }
                    item {
                        Text(
                            text = stringResource(id = R.string.count_didis_pending, didis.filter { it.wealth_ranking != WealthRank.NOT_RANKED.rank }.size),
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(vertical = dimensionResource(id = R.dimen.dp_6))
                        )
                    }
                    if (filterSelected){
                        itemsIndexed(newFilteredTolaDidiList.keys.toList().reversed()) { index, item ->
                            ShowDidisFromTola(
                                didiTola = item,
                                didiList = newFilteredTolaDidiList[item]?.reversed() ?: emptyList(),
                                viewModel = viewModel,
                                expandedIds = expandedCardIds,
                                modifier = Modifier
                            )
                        }
                    }
                    else {
                        itemsIndexed(didis) { index, didi ->
                            ExpandableCard(
                                didiEntity = didi,
                                viewModel = viewModel,
                                onCardArrowClick = {
                                    if (it)
                                        viewModel.onCardArrowClicked(didi.id)
                                    else {
                                        viewModel.onCardArrowClicked(didi.id)
                                        val nextIndex = index + 1
                                        if (nextIndex < didis.size) {
                                            viewModel.onCardArrowClicked(didis[nextIndex].id)
                                        } else {
                                            viewModel.onCardArrowClicked(didi.id)
                                        }
                                    }
                                },
                                expanded = expandedCardIds.contains(didi.id),
                            )
                        }
                    }
                }

            }
        }

        if (didis.any { it.wealth_ranking != WealthRank.NOT_RANKED.rank }) {

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

                positiveButtonText = if (completeStepAdditionClicked) stringResource(id = R.string.complete_transect_walk_text) else stringResource(
                    id = R.string.mark_complete_text
                ),
                negativeButtonRequired = false,
                positiveButtonOnClick = {
                    /*if (completeStepAdditionClicked) {
                        //TODO Integrate Api when backend fixes the response.
                        if ((context as MainActivity).isOnline.value ?: false) {
                            viewModel.addTolasToNetwork()
                        }
                        viewModel.markTransectWalkComplete(villageId, stepId)
                        navController.navigate(
                            "step_completion_screen/${
                                context.getString(R.string.transect_walk_completed_message).replace(
                                    "{VILLAGE_NAME}",
                                    viewModel.villageEntity.value?.name ?: ""
                                )
                            }"
                        )

                    } else {
                        completeStepAdditionClicked = true
                    }*/
                },
                negativeButtonOnClick = {/*Nothing to do here*/ }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ExpandableCard(
    didiEntity: DidiEntity,
    viewModel: WealthRankingViewModel,
    onCardArrowClick: (fromArrow: Boolean) -> Unit,
    expanded: Boolean,
) {
    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }
    val transition = updateTransition(transitionState, label = "transition")
    val cardBgColor by transition.animateColor({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "bgColorTransition") {
        Color.White
    }
    val cardPaddingHorizontal by transition.animateDp({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "paddingTransition") {
        dimensionResource(id = R.dimen.dp_5)
    }
    val cardElevation by transition.animateDp({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "elevationTransition") {
        dimensionResource(id = R.dimen.dp_5)
    }
    val cardRoundedCorners by transition.animateDp({
        tween(
            durationMillis = EXPANSTION_TRANSITION_DURATION,
            easing = FastOutSlowInEasing
        )
    }, label = "cornersTransition") {
        dimensionResource(id = R.dimen.dp_6)
    }
    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "rotationDegreeTransition") {
        if (expanded) 180f else 0f
    }
    val context = LocalContext.current
    val contentColour = remember {
        Color(ContextCompat.getColor(context, R.color.placeholder_color))
    }

    Card(
        backgroundColor = cardBgColor,
        contentColor = contentColour,
        elevation = cardElevation,
        shape = RoundedCornerShape(cardRoundedCorners),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = cardPaddingHorizontal,
                vertical = dimensionResource(id = R.dimen.dp_8)
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                color = Color.White,
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimensionResource(id = R.dimen.dp_13))
                            .padding(top = dimensionResource(id = R.dimen.dp_10)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Card(
                            modifier = Modifier
                                .width(dimensionResource(id = R.dimen.dp_44))
                                .height(dimensionResource(id = R.dimen.dp_44)),
                            backgroundColor = colorResource(id = R.color.placeholder_color),
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_50)),
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.didi_icon),
                                contentDescription = "Female PlaceHolder Icon",
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.dp_7)),
                            )
                        }

                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.dp_10)))

                        Column {
                            Text(
                                text = didiEntity.name,
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontFamily = NotoSans,
                                textAlign = TextAlign.Start,
                            )
                            if (didiEntity.wealth_ranking != WealthRank.NOT_RANKED.rank) {
                                val wealthRankTextColor = when(didiEntity.wealth_ranking){
                                    WealthRank.NOT_RANKED.rank -> textColorDark
                                    WealthRank.POOR.rank -> poorRankColor
                                    WealthRank.MEDIUM.rank -> mediumRankColor
                                    WealthRank.RICH.rank -> richRankColor
                                    else -> textColorDark
                                }
                                Row {
                                    Text(
                                        text = stringResource(id = R.string.ranking_text),
                                        color = Color.Black,
                                        fontSize = 14.sp,
                                        fontFamily = NotoSans,
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        text = didiEntity.wealth_ranking,
                                        color = wealthRankTextColor,
                                        fontSize = 14.sp,
                                        fontFamily = NotoSans,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.padding(
                                            horizontal = dimensionResource(
                                                id = R.dimen.dp_5
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                // Arrow Icon
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.End
                ) {
                    CardArrow(
                        degrees = arrowRotationDegree,
                        onClick = { onCardArrowClick(true) }
                    )
                }

            }
            //Expandable Content
            Column {
                Box {
                    CardTitle(title = didiEntity.name)
                }
                ExpandableContent(visible = expanded, didiEntity = didiEntity) {
                    didiEntity.wealth_ranking = it.rank
                    viewModel.updateDidiRankInDb(didiEntity.id, it.rank)
                    onCardArrowClick(false)
                }
            }
        }

    }
}

@Composable
fun CardArrow(
    degrees: Float,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        content = {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24),
                contentDescription = "Expandable Arrow",
                modifier = Modifier.rotate(degrees),
                tint = blueDark

            )
        }
    )
}

@Composable
fun CardTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
    )
}

@Composable
fun ExpandableContent(
    didiEntity: DidiEntity,
    visible: Boolean = true,
    onRankSelected: (wealthRank: WealthRank) -> Unit
) {
    val isPoorSelected = remember {
        mutableStateOf((didiEntity.wealth_ranking.equals(WealthRank.POOR.rank, true)))
    }
    val isMediumSelected = remember {
        mutableStateOf((didiEntity.wealth_ranking.equals(WealthRank.MEDIUM.rank, true)))
    }
    val isRichSelected = remember {
        mutableStateOf((didiEntity.wealth_ranking.equals(WealthRank.RICH.rank, true)))
    }
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
        visible = visible,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.dp_8))) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                val (main_card) = createRefs()
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(main_card) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
                    .padding(
                        start = dimensionResource(id = R.dimen.dp_10),
                        end = dimensionResource(id = R.dimen.dp_10),
                        bottom = dimensionResource(id = R.dimen.dp_10)
                    ),
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(id = R.dimen.height_50dp))
                            .border(
                                width = 0.dp,
                                color = rankItemBorder,
                                shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6))
                            )
                            .clickable {
                                isPoorSelected.value = true
                                isMediumSelected.value = false
                                isRichSelected.value = false
                                onRankSelected(WealthRank.POOR)
                            },
                        backgroundColor = if (isPoorSelected.value) blueDark else white,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.background(Color.Transparent)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_currency_rupee_24),
                                contentDescription = POOR_STRING,
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.dp_7)),
                                colorFilter = ColorFilter.tint(if (isPoorSelected.value) Color.White else Color.Black)
                            )
                            Text(
                                text = stringResource(id = R.string.poor_text),
                                color = if (isPoorSelected.value) Color.White else Color.Black,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.dp_5))
                            )

                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(id = R.dimen.height_50dp))
                            .border(
                                width = 0.dp,
                                color = rankItemBorder,
                                shape = RoundedCornerShape(dimensionResource(id = R.dimen.dp_6))
                            )
                            .clickable {
                                isPoorSelected.value = false
                                isMediumSelected.value = true
                                isRichSelected.value = false
                                onRankSelected(WealthRank.MEDIUM)
                            },
                        backgroundColor = if (isMediumSelected.value) blueDark else white,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.background(Color.Transparent)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_currency_rupee_24),
                                contentDescription = stringResource(id = R.string.medium_text),
                                colorFilter = ColorFilter.tint(if (isMediumSelected.value) Color.White else Color.Black)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_currency_rupee_24),
                                contentDescription = stringResource(id = R.string.medium_text),
                                colorFilter = ColorFilter.tint(if (isMediumSelected.value) Color.White else Color.Black)
                            )
                            Text(
                                text = stringResource(id = R.string.medium_text),
                                color = if (isMediumSelected.value) Color.White else Color.Black,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(horizontal = 5.dp)
                            )

                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(id = R.dimen.height_50dp))
                            .border(
                                width = 0.dp,
                                color = rankItemBorder,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                isPoorSelected.value = false
                                isMediumSelected.value = false
                                isRichSelected.value = true
                                onRankSelected(WealthRank.RICH)
                            },
                        backgroundColor = if (isRichSelected.value) blueDark else white,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.background(Color.Transparent)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_currency_rupee_24),
                                contentDescription = stringResource(id = R.string.rich_text),
                                colorFilter = ColorFilter.tint(if (isRichSelected.value) Color.White else Color.Black)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_currency_rupee_24),
                                contentDescription = stringResource(id = R.string.rich_text),
                                colorFilter = ColorFilter.tint(if (isRichSelected.value) Color.White else Color.Black)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_currency_rupee_24),
                                contentDescription = stringResource(id = R.string.rich_text),
                                colorFilter = ColorFilter.tint(if (isRichSelected.value) Color.White else Color.Black)
                            )
                            Text(
                                text = stringResource(id = R.string.rich_text),
                                color = if (isRichSelected.value) Color.White else Color.Black,
                                fontSize = 14.sp,
                                fontFamily = NotoSans,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(horizontal = 5.dp)
                            )

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowDidisFromTola(
    didiTola: String,
    didiList: List<DidiEntity>,
    viewModel: WealthRankingViewModel,
    expandedIds: List<Int>,
    modifier: Modifier,
) {
    Column(modifier = Modifier.then(modifier)) {
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
                ExpandableCard(
                    didiEntity = didi,
                    viewModel = viewModel,
                    onCardArrowClick = {
                        if (it)
                            viewModel.onCardArrowClicked(didi.id)
                        else {
                            viewModel.onCardArrowClicked(didi.id)
                            val nextIndex = index + 1
                            if (nextIndex < didiList.size) {
                                viewModel.onCardArrowClicked(didiList[nextIndex].id)
                            } else {
                                viewModel.onCardArrowClicked(didi.id)
                            }
                        }
                    },
                    expanded = expandedIds.contains(didi.id),
                )
            }
        }
    }
}