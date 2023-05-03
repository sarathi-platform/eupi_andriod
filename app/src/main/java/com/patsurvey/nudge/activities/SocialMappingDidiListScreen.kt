package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layoutId
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
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.CardArrow
import com.patsurvey.nudge.customviews.SearchWithFilterView
import com.patsurvey.nudge.model.dataModel.DidiDetailsModel
import com.patsurvey.nudge.utils.BlueButtonWithIcon
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.EXPANSTION_TRANSITION_DURATION

@Composable
fun SocialMappingDidiListScreen(modifier: Modifier, isOnline: Boolean = true) {
    val didiList = listOf(
        DidiDetailsModel(1, "didi 1", "sundar pahar", "sundar pahar", "Kahar", "12", "Rajesh"),
        DidiDetailsModel(2, "didi 2", "sundar pahar2", "sundar pahar2", "Kahar", "131", "Rajesh"),
        DidiDetailsModel(3, "didi 3", "sundar pahar3", "sundar pahar3", "Kahar", "14", "Rajesh"),
        DidiDetailsModel(4, "didi 4", "sundar pahar4", "sundar pahar4", "Kahar", "15", "Rajesh"),
        DidiDetailsModel(5, "didi 5", "sundar pahar5", "sundar pahar5", "Kahar", "16", "Rajesh")
    )

    val expandedIds = remember {
        mutableStateListOf<Int>()
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NetworkBanner(
            modifier = Modifier,
            isOnline = isOnline
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.padding(top = 30.dp, start = 16.dp, end = 16.dp)
        ) {
            MainTitle(
                title = stringResource(id = R.string.social_mapping),
                modifier = Modifier.weight(0.5f)
            )
            BlueButtonWithIcon(
                modifier = Modifier.weight(0.5f),
                buttonText = stringResource(id = R.string.add_didi),
                icon = Icons.Default.Add
            ) {

            }
        }

        SearchWithFilterView(
            stringResource(id = R.string.search_didis),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 20.dp)
        )

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
                    append("${didiList.size}")
                }
                append(" ${pluralStringResource(id = R.plurals.didis_added, didiList.size)}")
            },
            style = TextStyle(
                color = textColorDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = NotoSans
            ),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = white)
                .weight(1f),
            contentPadding = PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(didiList) { index, didi ->
                DidiItemCard(didi, expandedIds.contains(didi.id), modifier){
                    if(expandedIds.contains(didi.id)){
                        expandedIds.remove(didi.id)
                    } else {
                        expandedIds.add(didi.id)
                    }
                }
            }

        }

        DoubleButtonBox(
            modifier = Modifier.shadow(10.dp),
            negativeButtonRequired = false,
            positiveButtonText = stringResource(id = R.string.complete_didi_addition),
            positiveButtonOnClick = {

            },
            negativeButtonOnClick = {

            }
        )

    }
}

private fun decoupledConstraints(): ConstraintSet {
    return ConstraintSet {
        val didiImage = createRefFor("didiImage")
        val didiName = createRefFor("didiName")
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

@Composable
fun DidiItemCard(didi: DidiDetailsModel, expanded: Boolean, modifier: Modifier, onExpendClick: (Boolean)-> Unit) {

    val transition = updateTransition(expanded, label = "transition")

    val animateColor by transition.animateColor({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "animate color") {
        if (it) { greenOnline} else {textColorDark}
    }

    val animateInt by transition.animateInt({
        tween(durationMillis = 10)
    }, label = "animate float") {
        if(it) 1 else 0
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
            .padding(start = 16.dp, end = 16.dp)
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
                text = didi.tola,
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
                onClick = {onExpendClick(expanded)}
            )

            DidiDetailExpendableContent(modifier = Modifier.layoutId("didiDetailLayout"), didi, animateInt == 1)
        }
    }
    }
}

@Composable
fun DidiDetailExpendableContent(modifier: Modifier, didi: DidiDetailsModel, expended: Boolean) {
    val constraintSet = didiDetailConstraints()
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
                text = didi.houseNumber,
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
                text = didi.dadaName,
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
                text = didi.caste,
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
                text = didi.tola,
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
                text = "Wealth Ranking Not started",
                style = didiDetailItemStyle,
                textAlign = TextAlign.Start,
                modifier = Modifier.layoutId("latestStatus")
            )
            
            Spacer(modifier = Modifier.layoutId("bottomPadding").height(30.dp))
        }
    }
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
    Box(modifier = modifier
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
    SocialMappingDidiListScreen(modifier = Modifier, isOnline = true)
}