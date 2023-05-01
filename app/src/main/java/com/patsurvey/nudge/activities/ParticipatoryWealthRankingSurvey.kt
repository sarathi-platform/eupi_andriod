package com.patsurvey.nudge.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.R
import com.patsurvey.nudge.utils.DoubleButtonBox

@Preview
@Composable
fun ParticipatoryWealthRankingSurvey(
    modifier: Modifier = Modifier,
//    navController: NavController
) {

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

        Column(
            modifier = Modifier
                .constrainAs(mainBox) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .then(modifier),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.particaptory_wealth_ranking_survey_text),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(top = 28.dp),
                    style = largeTextStyle,
                    color = textColorDark,
                    textAlign = TextAlign.Center
                )
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.didis_item_text),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 16.dp),
                    style = mediumTextStyle,
                    fontSize = 16.sp,
                    color = textColorDark80,
                    textAlign = TextAlign.Start
                )
            }

            WealthRankingBox(
                count = 40,
                wealthRank = WealthRank.POOR,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp)
            )
            WealthRankingBox(
                count = 5,
                wealthRank = WealthRank.MEDIUM,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp)
            )
            WealthRankingBox(
                count = 54,
                wealthRank = WealthRank.RICH,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp)
            )
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
            positiveButtonText = stringResource(id = R.string.complete_wealth_ranking_btn_text),
            negativeButtonRequired = false,
            positiveButtonOnClick = {

            },
            negativeButtonOnClick = { /*No Back Button*/ }
        )
    }
}


@Composable
fun WealthRankingBox(
    modifier: Modifier = Modifier,
    count: Int,
    wealthRank: WealthRank
) {
    val boxColor = when (wealthRank) {
        WealthRank.RICH -> brownLoght
        WealthRank.MEDIUM -> yellowLight
        WealthRank.POOR -> blueLighter
    }
    val boxTitle = when (wealthRank) {
        WealthRank.RICH -> "Rich"
        WealthRank.MEDIUM -> "Medium"
        WealthRank.POOR -> "Poor"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(boxColor, shape = RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
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
            modifier = Modifier.align(Alignment.CenterEnd),
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