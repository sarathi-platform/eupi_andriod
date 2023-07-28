package com.patsurvey.nudge.activities.ui.socialmapping

import android.content.Context
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.CircularDidiImage
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.WealthRankingSurveyViewModel
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueLighter
import com.patsurvey.nudge.activities.ui.theme.borderGreyLight
import com.patsurvey.nudge.activities.ui.theme.brownLoght
import com.patsurvey.nudge.activities.ui.theme.buttonTextStyle
import com.patsurvey.nudge.activities.ui.theme.didiDetailItemStyle
import com.patsurvey.nudge.activities.ui.theme.didiDetailLabelStyle
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleMediumWeight
import com.patsurvey.nudge.activities.ui.theme.textColorBlueLight
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.textColorDark80
import com.patsurvey.nudge.activities.ui.theme.veryLargeTextStyle
import com.patsurvey.nudge.activities.ui.theme.yellowBg
import com.patsurvey.nudge.activities.ui.theme.yellowLight
import com.patsurvey.nudge.activities.ui.transect_walk.VillageDetailView
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.navigation.home.HomeScreens
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BottomButtonBox
import com.patsurvey.nudge.utils.BulletList
import com.patsurvey.nudge.utils.ButtonNegative
import com.patsurvey.nudge.utils.ButtonPositive
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.EXPANSTION_TRANSITION_DURATION
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.showToast

@Composable
fun ParticipatoryWealthRankingSurvey(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: WealthRankingSurveyViewModel,
    stepId: Int,
    isStepComplete: Boolean
) {

    val didids = viewModel.didiList.collectAsState()
    var showDidiListForRank by remember { mutableStateOf(Pair(false, WealthRank.NOT_RANKED)) }
    val expandedCardIds by viewModel.expandedCardIdsList.collectAsState()

    val context = LocalContext.current

    val localDensity = LocalDensity.current
    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    LaunchedEffect(key1 = true) {
        viewModel.stepId = stepId
        viewModel.getWealthRankingStepStatus(stepId) {
            viewModel.showBottomButton.value = !it
        }
    }
    BackHandler() {
        if (showDidiListForRank.first) {
            showDidiListForRank = Pair(!showDidiListForRank.first, WealthRank.NOT_RANKED)
        } else {
            if (isStepComplete) {
                navController.navigate(Graph.HOME) {
                    popUpTo(HomeScreens.PROGRESS_SCREEN.route) {
                        inclusive = true
                        saveState = false
                    }
                }
            } else {
                navController.popBackStack()
            }
        }
    }

    val showDialog = remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier)
    ) {
        val (bottomActionBox, mainBox) = createRefs()

        if (showDialog.value) {
            ShowDialog(title = stringResource(id = R.string.are_you_sure),
                message = context.getString(
                    R.string.you_are_submitting_wealth_ranking_for_count_didis,
                    didids.value.filter { it.rankingEdit }.size.toString()
                ),
                setShowDialog = {
                    showDialog.value = it
                }) {
                viewModel.checkIfLastStepIsComplete(stepId) { isPreviousStepComplete ->
                    if (isPreviousStepComplete) {
                        viewModel.markWealthRakningComplete(viewModel.villageId, stepId)
                        viewModel.updateWealthRankingFlagForDidis()
                        viewModel.saveWealthRankingCompletionDate()
                        if ((context as MainActivity).isOnline.value ?: false) {
                            if(viewModel.isTolaSynced.value == 2
                                && viewModel.isDidiSynced.value == 2) {
                                viewModel.updateWealthRankingToNetwork(object :
                                    NetworkCallbackListener {
                                    override fun onSuccess() {
                                        viewModel.callWorkFlowAPI(viewModel.villageId, stepId, object :
                                        NetworkCallbackListener {
                                        override fun onSuccess() {
                                        }

                                        override fun onFailed() {
//                                            showCustomToast(context, SYNC_FAILED)
                                        }
                                    })
                                    }

                                    override fun onFailed() {
//                                        showCustomToast(context, SYNC_FAILED)
                                    }
                                })

                            }
                        }
                        navController.navigate(
                            "wr_step_completion_screen/${
                                context.getString(R.string.wealth_ranking_completed_message)
                                    .replace(
                                        "{VILLAGE_NAME}",
                                        viewModel.selectedVillage?.name ?: ""
                                    )
                            }"
                        )
                    } else {
                        showToast(context, context.getString(R.string.previous_step_not_complete_messgae_text))
                    }
                }

            }
        }

        Box(
            modifier = Modifier
                .constrainAs(mainBox) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
                .fillMaxWidth()
                .padding(top = 14.dp)
                .then(modifier),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp),
            ) {

                VillageDetailView(
                    villageName = viewModel.selectedVillage?.name ?: "",
                    voName = (viewModel.selectedVillage?.federationName) ?: "",
                    modifier = Modifier
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp, horizontal = 4.dp)
                ) {
                    Text(
                        text =
                        stringResource(id = R.string.particaptory_wealth_ranking_survey_text),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(),
                        style = mediumTextStyle,
                        color = textColorDark,
                        textAlign = TextAlign.Start
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp, horizontal = 4.dp)
                ) {
                    var count: Int = if (showDidiListForRank.first) {
                        when (showDidiListForRank.second) {
                            WealthRank.POOR -> didids.value.filter { it.wealth_ranking == WealthRank.POOR.rank }.size
                            WealthRank.MEDIUM -> didids.value.filter { it.wealth_ranking == WealthRank.MEDIUM.rank }.size
                            else -> didids.value.filter { it.wealth_ranking == WealthRank.RICH.rank }.size
                        }
                    } else {
                        didids.value.size
                    }
                    Text(
                        text = "$count ${
                            stringResource(
                                id = if (showDidiListForRank.first) {
                                    when (showDidiListForRank.second) {
                                        WealthRank.POOR -> if (count > 1) R.string.poor_didi_item_text_plural else R.string.poor_didi_item_text
                                        WealthRank.MEDIUM -> if (count > 1) R.string.medium_didi_item_text_plural else R.string.medium_didi_item_text
                                        else -> if (count > 1) R.string.rich_didi_item_text_plural else R.string.rich_didi_item_text
                                    }
                                } else if (count > 1) R.string.didis_item_text_plural else R.string.didis_item_text_singular
                            )
                        }",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = NotoSans
                        ),
                        color = textColorDark80,
                        textAlign = TextAlign.Start
                    )
                }

                AnimatedVisibility(visible = showDidiListForRank.first, modifier = Modifier.padding(horizontal = 4.dp)) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp)
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            contentPadding = PaddingValues(bottom = bottomPadding),
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(4.dp)) }
                            itemsIndexed(didids.value.filter { it.wealth_ranking == showDidiListForRank.second.rank }) { index, didi ->
                                DidiItemCardForWealthRanking(didi,
                                    expandedCardIds.contains(didi.id),
                                    Modifier.padding(horizontal = 0.dp),
                                    onExpendClick = { expand, didiDetailModel ->
                                        viewModel.onCardArrowClicked(didiDetailModel.id)
                                    },
                                    onItemClick = {}
                                )
                            }
                            item { Spacer(modifier = Modifier.height(6.dp)) }
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    WealthRankingBox(
                        count = didids.value.filter { it.wealth_ranking == WealthRank.POOR.rank }.size,
                        wealthRank = WealthRank.POOR,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
                    ) {
                        showDidiListForRank = Pair(true, WealthRank.POOR)
                    }
                    WealthRankingBox(
                        count = didids.value.filter { it.wealth_ranking == WealthRank.MEDIUM.rank }.size,
                        wealthRank = WealthRank.MEDIUM,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
                    ) {
                        showDidiListForRank = Pair(true, WealthRank.MEDIUM)
                    }
                    WealthRankingBox(
                        count = didids.value.filter { it.wealth_ranking == WealthRank.RICH.rank }.size,
                        wealthRank = WealthRank.RICH,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
                    ) {
                        showDidiListForRank = Pair(true, WealthRank.RICH)
                    }
                }
            }
        }

        if (viewModel.showBottomButton.value || showDidiListForRank.first) {
            BottomButtonBox(
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
                positiveButtonText = if (showDidiListForRank.first) stringResource(id = R.string.done_text) else stringResource(
                    id = R.string.complete_wealth_ranking_btn_text
                ),
                isArrowRequired = !showDidiListForRank.first,
                positiveButtonOnClick = {
                    if (showDidiListForRank.first)
                        showDidiListForRank =
                            Pair(!showDidiListForRank.first, WealthRank.NOT_RANKED)
                    else {
                        showDialog.value = true
                    }
                }
            )
        } else {
            bottomPadding = 0.dp
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowDialogPreview(){
    ShowDialog("Title","New Message", setShowDialog = {}, list = emptyList(), positiveButtonClicked = {})
}
@Composable
fun ShowDialog(
    title: String,
    message: String,
    isBulletShow:Boolean?=false,
    list: List<String> ?= emptyList(),
    setShowDialog: (Boolean) -> Unit,
    positiveButtonClicked: () -> Unit
) {
    Dialog(onDismissRequest = { setShowDialog(false) }) {
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
                        text = title,
                        textAlign = TextAlign.Start,
                        style = buttonTextStyle,
                        maxLines = 1,
                        color = textColorDark,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if(isBulletShow == false){
                        Text(
                            text = message,
                            textAlign = TextAlign.Start,
                            style = smallTextStyleMediumWeight,
                            color = textColorDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }else{
                        if (list != null) {
                            BulletList(items = list)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        ButtonNegative(
                            buttonTitle = stringResource(id = R.string.cancel_tola_text),
                            isArrowRequired = false,
                            modifier = Modifier.weight(1f)
                        ) {
                            setShowDialog(false)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        ButtonPositive(
                            buttonTitle = stringResource(id = R.string.yes_text),
                            isArrowRequired = false,
                            modifier = Modifier.weight(1f)
                        ) {
                            positiveButtonClicked()
                            setShowDialog(false)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun WealthRankingBox(
    modifier: Modifier = Modifier,
    count: Int,
    wealthRank: WealthRank,
    onWealthRankingBoxClicked: (wealthRank: WealthRank) -> Unit
) {
    val boxColor = when (wealthRank) {
        WealthRank.RICH -> brownLoght
        WealthRank.MEDIUM -> yellowLight
        WealthRank.POOR -> blueLighter
        else -> {
            Color.Transparent
        }
    }
    val boxTitle = when (wealthRank) {
        WealthRank.RICH -> stringResource(id = R.string.rich_text)
        WealthRank.MEDIUM -> stringResource(id = R.string.medium_text)
        WealthRank.POOR -> stringResource(id = R.string.poor_text)
        else -> {
            ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(boxColor, shape = RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Color.White
                )
            ) {
                onWealthRankingBoxClicked(wealthRank)
            }
            .then(modifier)
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (count < 10) String.format("%02d", count) else count.toString(),
                style = veryLargeTextStyle,
                color = textColorDark
            )
            ConstraintLayout(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .absolutePadding(top = 4.dp)
            ) {
                val (circle_1, circle_2, circle_3) = createRefs()
                RoundedImage(
                    image = painterResource(id = R.drawable.didi_icon),
                    modifier = Modifier
                        .height(23.dp)
                        .constrainAs(circle_1) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                )
                if (count >= 2) {
                    RoundedImage(
                        image = painterResource(id = R.drawable.didi_icon),
                        modifier = Modifier
                            .height(23.dp)
                            .constrainAs(circle_2) {
                                top.linkTo(parent.top)
                                start.linkTo(circle_1.start, 13.dp)
                            }
                    )
                }
                if (count >= 3) {
                    RoundedImage(
                        image = painterResource(id = R.drawable.didi_icon),
                        modifier = Modifier
                            .height(23.dp)
                            .constrainAs(circle_3) {
                                top.linkTo(parent.top)
                                start.linkTo(circle_2.start, 13.dp)
                            }
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = boxTitle,
                style = mediumTextStyle,
                color = textColorDark
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = textColorDark,
                modifier = Modifier
                    .padding(start = 20.dp)
            )
        }
    }
}

@Composable
fun PATSurveyBox(
    modifier: Modifier = Modifier,
    count: Int,
    isComplete: Boolean = false,
    onPATSurveyBoxClicked: () -> Unit
) {
    val boxColor = if (isComplete) blueLighter else yellowLight
    val boxTitle = if (isComplete) stringResource(id = R.string.pat_completed)
    else stringResource(id = R.string.didi_not_available)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(boxColor, shape = RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Color.White
                )
            ) {
                onPATSurveyBoxClicked()
            }
            .then(modifier)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (count < 10) String.format("%02d", count) else count.toString(),
                style = veryLargeTextStyle,
                color = textColorDark
            )
            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = boxTitle,
                    style = mediumTextStyle,
                    color = textColorDark
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = textColorDark,
                modifier = Modifier
                    .padding(start = 20.dp)
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun WealthRankingBoxPreview() {
    WealthRankingBox(modifier = Modifier, 5, WealthRank.POOR, onWealthRankingBoxClicked = {})
}

@Preview(showBackground = true)
@Composable
fun PATSurveyBoxPreview() {
    PATSurveyBox(modifier = Modifier, 5, true, onPATSurveyBoxClicked = {})
}

@Composable
fun RoundedImage(
    image: Painter,
    modifier: Modifier = Modifier
) {
    Image(
        painter = image,
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .border(
                width = 2.dp,
                color = Color.White,
                shape = CircleShape

            )
            .clip(CircleShape)
            .background(yellowBg)
    )
}

@Composable
fun DidiItemCardForWealthRanking(
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
                val constraintSet = decoupledConstraintsForWealthCard()
                ConstraintLayout(constraintSet, modifier = Modifier.fillMaxWidth()) {
                    CircularDidiImage(
                        didi = didi,
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
                    com.patsurvey.nudge.customviews.CardArrow(
                        modifier = Modifier.layoutId("expendArrowImage"),
                        degrees = arrowRotationDegree,
                        iconColor = animateColor,
                        onClick = { onExpendClick(expanded, didi) }
                    )

                    DidiDetailExpendableContentForWealthRanking(
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
fun DidiDetailExpendableContentForWealthRanking(modifier: Modifier, didi: DidiEntity, expended: Boolean) {
    val constraintSet = didiDetailConstraintsForWealthCard()

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
                text = getLatestStatusTextForWealthRankingCard(context, didi),
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

fun getLatestStatusTextForWealthRankingCard(context: Context, didi: DidiEntity): String {
    var status = BLANK_STRING
    if (didi.wealth_ranking == WealthRank.NOT_RANKED.rank) {
        status = context.getString(R.string.wealth_ranking_status_not_started_text)
    } else {
        if (!didi.rankingEdit) {
            if (!didi.patEdit) {
                status = if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal && didi.forVoEndorsement == 1) {
                    when (didi.voEndorsementStatus) {
                        DidiEndorsementStatus.ENDORSED.ordinal, DidiEndorsementStatus.ACCEPTED.ordinal -> {
                            context.getString(R.string.vo_endorsement_status_text).replace("{VO_STATUS}", context.getString(R.string.vo_selected_status_text))
                        }
                        DidiEndorsementStatus.REJECTED.ordinal -> {
                            context.getString(R.string.vo_endorsement_status_text).replace("{VO_STATUS}", context.getString(R.string.vo_rejected_status_text))
                        }
                        else -> {
                            context.getString(R.string.pat_completed_status_text).replace("{PAT_STATUS}", context.getString(R.string.pat_selected_status_text))
                        }
                    }
                } else {
                    context.getString(R.string.pat_completed_status_text).replace("{PAT_STATUS}", context.getString(R.string.pat_rejected_status_text))
                }
            } else {
                status = context.getString(R.string.wealth_ranking_status_complete_text)
                    .replace("{RANK}", getRankInLanguage(context, didi.wealth_ranking))
            }
        } else {
            status = context.getString(R.string.wealth_ranking_status_not_started_text)
        }
    }

    return status
}

private fun decoupledConstraintsForWealthCard(): ConstraintSet {
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
            start.linkTo(didiImage.end, 6.dp)
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
            top.linkTo(village.top, margin = 3.dp)
            bottom.linkTo(village.bottom)
            start.linkTo(didiName.start, margin = 3.dp)
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

private fun didiDetailConstraintsForWealthCard(): ConstraintSet {
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

        constrain(dadaNameLabel) {
            start.linkTo(parent.start, margin = 15.dp)
            top.linkTo(divider.bottom, margin = 15.dp)
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

        constrain(houseNumberLabel) {
            start.linkTo(dadaNameLabel.start)
            top.linkTo(dadaNameLabel.bottom, margin = 20.dp)
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
        constrain(casteLabel) {
            start.linkTo(dadaNameLabel.start)
            top.linkTo(houseNumberLabel.bottom, margin = 20.dp)
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
            start.linkTo(dadaNameLabel.start)
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
            start.linkTo(dadaNameLabel.start)
            top.linkTo(tolaLabel.bottom, margin = 15.dp)
            end.linkTo(centerGuideline)
            width = Dimension.fillToConstraints
        }

        constrain(latestStatus) {
            start.linkTo(centerGuideline)
            top.linkTo(latestStatusLabel.top)
            end.linkTo(parent.end, margin = 10.dp)
            width = Dimension.fillToConstraints
        }

        constrain(bottomPadding) {
            start.linkTo(parent.start)
            top.linkTo(latestStatus.bottom)
        }
    }
}
