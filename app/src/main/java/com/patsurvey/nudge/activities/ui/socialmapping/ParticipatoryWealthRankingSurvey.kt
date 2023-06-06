package com.patsurvey.nudge.activities.ui.socialmapping

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.DidiItemCard
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.WealthRankingSurveyViewModel
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.activities.ui.transect_walk.VillageDetailView
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.navigation.home.HomeScreens
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.*

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
                    didids.value.filter { it.wealth_ranking == WealthRank.POOR.rank }.size.toString()
                ),
                setShowDialog = {
                    showDialog.value = it
                }) {
                viewModel.checkIfLastStepIsComplete(stepId) { isPreviousStepComplete ->
                    if (isPreviousStepComplete) {
                        if ((context as MainActivity).isOnline.value ?: false) {
                            viewModel.updateWealthRankingToNetwork(object :
                                NetworkCallbackListener {
                                override fun onSuccess() {
                                }

                                override fun onFailed() {
                                    showCustomToast(context, SYNC_FAILED)
                                }
                            })
                            viewModel.callWorkFlowAPI(viewModel.villageId, stepId, object :
                                NetworkCallbackListener {
                                override fun onSuccess() {
                                }

                                override fun onFailed() {
                                    showCustomToast(context, SYNC_FAILED)
                                }
                            })
                        }

                        viewModel.markWealthRakningComplete(viewModel.villageId, stepId)
                        viewModel.saveWealthRankingCompletionDate()
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
                        showToast(context, "Previous Step Not Complete.")
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
                        .padding(vertical = 2.dp)
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
                        .padding(vertical = 2.dp)
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

                AnimatedVisibility(visible = showDidiListForRank.first) {

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
                                DidiItemCard(didi,
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
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

@Composable
fun ShowDialog(
    title: String,
    message: String,
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
                    Text(
                        text = message,
                        textAlign = TextAlign.Start,
                        style = smallTextStyleMediumWeight,
                        maxLines = 2,
                        color = textColorDark,
                        modifier = Modifier.fillMaxWidth()
                    )
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
        WealthRank.RICH -> "Rich"
        WealthRank.MEDIUM -> "Medium"
        WealthRank.POOR -> "Poor"
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