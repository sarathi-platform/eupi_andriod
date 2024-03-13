package com.patsurvey.nudge.activities.ui.socialmapping

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
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
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.CircularDidiImage
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.decoupledConstraintsForPatCard
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.borderGreyLight
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.mediumRankColor
import com.patsurvey.nudge.activities.ui.theme.poorRankColor
import com.patsurvey.nudge.activities.ui.theme.rankItemBorder
import com.patsurvey.nudge.activities.ui.theme.richRankColor
import com.patsurvey.nudge.activities.ui.theme.textColorBlueLight
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.activities.ui.theme.yellowBg
import com.patsurvey.nudge.activities.ui.transect_walk.VillageDetailView
import com.patsurvey.nudge.customviews.SearchWithFilterView
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.EXPANSTION_TRANSITION_DURATION
import com.patsurvey.nudge.utils.POOR_STRING
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.showDidiImageDialog
import kotlinx.coroutines.CoroutineScope

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

    val newFilteredTolaDidiList = viewModel.filterTolaMapList
    val newFilteredDidiList = viewModel.filterDidiList.collectAsState()

    val _pendingDidiCount = remember {
        mutableStateOf(newFilteredDidiList.value.size)
    }

    val localDensity = LocalDensity.current

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = true) {
        viewModel.getWealthRankingStepStatus(stepId) {
            if (it)
                navController.navigate("wealth_ranking_survey/$stepId/$it")
        }

        if(didis.isNotEmpty()) {
            viewModel.onCardArrowClicked(didis[0].id, coroutineScope, listState,0)
        }
    }

    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    var filterSelected by remember {
        mutableStateOf(false)
    }

    if(viewModel.showDidiImageDialog.value){
        viewModel.dialogDidiEntity.value?.let {
            showDidiImageDialog(didi = it){
                viewModel.showDidiImageDialog.value = false
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
            .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                VillageDetailView(
                    villageName = viewModel.prefRepo.getSelectedVillage().name ?: "",
                    voName = (viewModel.prefRepo.getSelectedVillage().federationName) ?: "",
                    modifier = Modifier
                )
                LazyColumn(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(color = white)
                        .padding(horizontal = 4.dp)
                        .weight(1f),
                    state = listState,
                    contentPadding = PaddingValues(vertical = 10.dp),
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
                            modifier = Modifier,
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
                        Log.d(
                            "WealthRankingScreen",
                            "pendingDidiCount.value: ${_pendingDidiCount.value}"
                        )
                        val count = newFilteredDidiList.value.filter { it.wealth_ranking == WealthRank.NOT_RANKED.rank }.size
                        Text(
                            text = if (count <= 1) stringResource(id = R.string.count_didis_pending_singular, count) else stringResource(
                                id = R.string.count_didis_pending_plural, count
                            ),
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(vertical = dimensionResource(id = R.dimen.dp_6))
                                .padding(start = 8.dp)
                        )
                    }
                    if (filterSelected) {
                        itemsIndexed(
                            newFilteredTolaDidiList.keys.toList()
                        ) { index, item ->
                             ShowDidisFromTola(
                                didiTola = item,
                                 didiList = newFilteredTolaDidiList[item] ?: emptyList(),
                                viewModel = viewModel,
                                expandedIds = expandedCardIds,
                                modifier = Modifier,
                                 coroutineScope = coroutineScope,
                                 listState = listState
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
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                ExpandableCard(
                                    didiEntity = didi,
                                    viewModel = viewModel,
                                    onCardArrowClick = {
                                        if (it)
                                            viewModel.onCardArrowClicked(didi.id,coroutineScope, listState,index)
                                        else {
                                            viewModel.onCardArrowClicked(didi.id,coroutineScope, listState,index)
                                            val nextIndex = index + 1
                                            if (nextIndex < didis.size) {
                                                viewModel.onCardArrowClicked(didis[nextIndex].id,coroutineScope, listState,nextIndex)
                                            } else if (nextIndex == didis.size){
                                                viewModel.closeLastCard(didi.id)
                                            }
                                            _pendingDidiCount.value = newFilteredDidiList.value.size - index
                                            if (!didis.any { it.wealth_ranking == WealthRank.NOT_RANKED.rank })
                                                viewModel.shouldShowBottomButton.value = true
                                            Log.d(
                                                "WealthRankingScreen",
                                                "pendingDidiCount.value: ${_pendingDidiCount.value}"
                                            )
                                        }
                                    },
                                    expanded = expandedCardIds.contains(didi.id),
                                    onCircularImageClick = { didi->
                                        viewModel.dialogDidiEntity.value = didi
                                        viewModel.showDidiImageDialog.value = true
                                    }
                                )
                            }
                        }
                    }
                }

            }
        }

        if (viewModel.shouldShowBottomButton.value || didis.filter { it.wealth_ranking == WealthRank.NOT_RANKED.rank }.isEmpty()) {
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

                positiveButtonText = stringResource(id = R.string.review_wealth_ranking),
                negativeButtonRequired = false,
                positiveButtonOnClick = {
                    val stepStatus = false
                    navController.navigate("wealth_ranking_survey/$stepId/$stepStatus")
                },
                negativeButtonOnClick = {/*Nothing to do here*/ }
            )
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ExpandableCard(
    didiEntity: DidiEntity,
    viewModel: WealthRankingViewModel,
    onCardArrowClick: (fromArrow: Boolean) -> Unit,
    expanded: Boolean,
    onCircularImageClick:(DidiEntity) -> Unit
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
            .clickable {
                if (didiEntity.rankingEdit)
                    onCardArrowClick(true)
            }
            .padding(
                horizontal = cardPaddingHorizontal,
                /*vertical = dimensionResource(id = R.dimen.dp_8)*/
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

                    BoxWithConstraints {
                        val constraintSet = decoupledConstraintsForPatCard()
                        ConstraintLayout(constraintSet, modifier = Modifier.fillMaxWidth()) {
                            CircularDidiImage(
                                didi = didiEntity,
                                modifier = Modifier.layoutId("didiImage")
                            ) {
                                onCircularImageClick(didiEntity)
                            }
                            Row(
                                modifier = Modifier
                                    .layoutId("didiRow")
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = didiEntity.name,
                                    style = TextStyle(
                                        color = textColorDark,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = NotoSans,
                                        textAlign = TextAlign.Start
                                    ),
                                )
                            }

                            Text(
                                text = didiEntity.address,
                                style = TextStyle(
                                    color = textColorBlueLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = NotoSans
                                ),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.layoutId("houseNumber_1")
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimensionResource(id = R.dimen.dp_13)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {

                        Column {
                            if (didiEntity.wealth_ranking != WealthRank.NOT_RANKED.rank) {
                                val wealthRankTextColor = when (didiEntity.wealth_ranking) {
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
                                        text = getRankInLanguage(context, didiEntity.wealth_ranking),
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
                if (didiEntity.rankingEdit) {
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
            }
            //Expandable Content
            if (didiEntity.rankingEdit) {
                Column {
                    Spacer(
                        modifier = Modifier
                            .height(10.dp)
                            .fillMaxWidth()
                    )
                    ExpandableContent(visible = expanded, didiEntity = didiEntity) {
                        didiEntity.wealth_ranking = it.rank
                        viewModel.updateDidiRankInDb(
                            didiEntity,
                            it.rank,
                            (context as MainActivity).isOnline.value ?: false,
                            object : NetworkCallbackListener {
                                override fun onSuccess() {

                                }

                                override fun onFailed() {
//                                    showCustomToast(context, SYNC_FAILED)
                                }
                            })
                        onCardArrowClick(false)
                    }
                }
            } else {
                Spacer(
                    modifier = Modifier
                        .height(14.dp)
                        .fillMaxWidth()
                )
            }
        }

    }
}

fun getRankInLanguage(context: Context, wealthRanking: String): String {
    return when (wealthRanking) {
        WealthRank.RICH.rank -> context.getString(R.string.ranking_text_rich)
        WealthRank.MEDIUM.rank -> context.getString(R.string.ranking_text_medium)
        WealthRank.POOR.rank -> context.getString(R.string.ranking_text_poor)
        else -> BLANK_STRING
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
    coroutineScope: CoroutineScope,
    listState: LazyListState
) {
    Column(modifier = Modifier.then(modifier)) {
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
                    .size(28.dp)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${didiList.size}",
                    color = greenOnline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .absolutePadding(bottom = 3.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = NotoSans,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            didiList.forEachIndexed { index, didi ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    ExpandableCard(
                        didiEntity = didi,
                        viewModel = viewModel,
                        onCardArrowClick = {
                            if (it)
                                viewModel.onCardArrowClicked(didi.id,coroutineScope, listState,index)
                            else {
                                viewModel.onCardArrowClicked(didi.id,coroutineScope, listState,index)
                                val nextIndex = index + 1
                                if (nextIndex < didiList.size) {
                                    viewModel.onCardArrowClicked(didiList[nextIndex].id,coroutineScope, listState,nextIndex)
                                } else if (nextIndex == didiList.size){
                                    viewModel.closeLastCard(didi.id)
//                                    viewModel.onCardArrowClicked(didi.id)
                                }
                                if (!didiList.any { it.wealth_ranking == WealthRank.NOT_RANKED.rank })
                                    viewModel.shouldShowBottomButton.value = true
                            }
                        },
                        expanded = expandedIds.contains(didi.id),
                        onCircularImageClick = { didi->
                            viewModel.dialogDidiEntity.value = didi
                            viewModel.showDidiImageDialog.value = true
                        }
                    )
                }
            }
        }
    }
}