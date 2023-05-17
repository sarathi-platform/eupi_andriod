package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
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
import androidx.compose.material.Card
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.gson.Gson
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.socialmapping.*
import com.patsurvey.nudge.activities.ui.socialmapping.ExpandableCard
import com.patsurvey.nudge.activities.ui.transect_walk.VillageDetailView
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.EXPANSTION_TRANSITION_DURATION

@Composable
fun ParticipatoryWealthRankingSurvey(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: WealthRankingSurveyViewModel
) {

    val didids = viewModel.didiList.collectAsState()
    var showDidiListForRank by remember { mutableStateOf(Pair(false, WealthRank.NOT_RANKED)) }
    val expandedCardIds by viewModel.expandedCardIdsList.collectAsState()

    val localDensity = LocalDensity.current
    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier)
    ) {
        val (bottomActionBox, mainBox) = createRefs()

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
                    .padding(horizontal = 16.dp)
            ) {

                VillageDetailView(
                    villageName = viewModel.selectedVillage?.name ?: "",
                    voName = (viewModel.selectedVillage?.name + " Mandal") ?: "",
                    modifier = Modifier
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.particaptory_wealth_ranking_survey_text),
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
                    Text(
                        text = stringResource(id = R.string.didis_item_text),
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

                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp)) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            item { Spacer(modifier = Modifier.height(4.dp)) }
                            itemsIndexed(didids.value.filter { it.wealth_ranking == showDidiListForRank.second.rank }) { index, didi ->
                                DidiItemCard(didi, expandedCardIds.contains(didi.id), modifier,
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
            positiveButtonText = if (showDidiListForRank.first) stringResource(id = R.string.done_text) else stringResource(
                id = R.string.complete_wealth_ranking_btn_text
            ),
            negativeButtonRequired = false,
            positiveButtonOnClick = {
                if (showDidiListForRank.first)
                    showDidiListForRank = Pair(!showDidiListForRank.first, WealthRank.NOT_RANKED)
                else {
                    //TODO Show Dialog
                }
            },
            negativeButtonOnClick = { /*No Back Button*/ }
        )
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