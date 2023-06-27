package com.patsurvey.nudge.activities.ui.bpc.score_comparision

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.brownDark
import com.patsurvey.nudge.activities.ui.theme.comparisonCardDividerColor
import com.patsurvey.nudge.activities.ui.theme.greenBgLight
import com.patsurvey.nudge.activities.ui.theme.greenOnline
import com.patsurvey.nudge.activities.ui.theme.greyLightBgColor
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.redOffline
import com.patsurvey.nudge.activities.ui.theme.smallerTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.unmatchedOrangeColor
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.navigation.home.HomeScreens
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.ARG_FROM_PAT_DIDI_LIST_SCREEN
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.CRP_USER_TYPE
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.EXPANSTION_TRANSITION_DURATION
import com.patsurvey.nudge.utils.MATCH_PERCENTAGE
import java.io.File

@Composable
fun ScoreComparisionScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: ScoreComparisonViewModel
) {

    val filterdDidiList = viewModel.filterDidiList.collectAsState()

    val passPercentage = viewModel.passPercentage.collectAsState()

    val localDensity = LocalDensity.current

    val focusManager = LocalFocusManager.current

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    val expandBox = remember {
        mutableStateOf(false)
    }

    BackHandler() {
        navController.navigate(Graph.HOME){
            popUpTo(HomeScreens.BPC_PROGRESS_SCREEN.route){
                inclusive = true
            }
        }
    }

    val transition = updateTransition(expandBox.value, label = "transition")
    val colorTransistion = updateTransition(targetState = passPercentage.value > MATCH_PERCENTAGE, label = "colorTransistion")

    val animateColor by colorTransistion.animateColor({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "animate color") {
        if (it) {
            greenOnline
        } else {
            redOffline
        }
    }

    val animatedBoxBgColor by colorTransistion.animateColor({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "animate color") {
        if (it) {
            greenBgLight
        } else {
            greyLightBgColor
        }
    }


    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "rotationDegreeTransition") {
        if (it) 180f else 0f
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

                val count = filterdDidiList.value.size
                Text(
                    text = stringResource(id = if (count > 1) R.string.comparison_screen_heading_plural else R.string.comparison_screen_heading_singular).replace("{COUNT}", count.toString(), true),
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(
                            vertical = dimensionResource(id = R.dimen.dp_6),
                            horizontal = 32.dp
                        )
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
                        .weight(1f)
                ) {

                    item {
                        Spacer(
                            modifier = Modifier
                                .height(14.dp)
                                .fillMaxWidth()
                        )
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = animateColor,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .background(animatedBoxBgColor, shape = RoundedCornerShape(6.dp))
                                .fillMaxWidth()
                        ) {
                            Column(Modifier) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .clickable {
                                            if (passPercentage.value < MATCH_PERCENTAGE)
                                                expandBox.value = !expandBox.value
                                        },
                                ) {
                                    Text(
                                        text = stringResource(R.string.match_percentage_box_text)
                                            .replace("{PERCENTAGE}", passPercentage.value.toString(), true),
                                        color = animateColor,
                                        fontFamily = NotoSans,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 2,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (passPercentage.value < MATCH_PERCENTAGE) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24),
                                            contentDescription = "Expandable Arrow",
                                            modifier = Modifier
                                                .rotate(degrees = arrowRotationDegree),
                                            tint = textColorDark
                                        )
                                    }
                                }
                                ExpandableSummaryBox(expanded = expandBox.value)
                            }
                        }
                    }

                    item {
                        Spacer(
                            modifier = Modifier
                                .height(10.dp)
                                .fillMaxWidth()
                        )
                    }

                    item {
                        Text(
                            text = buildAnnotatedString
                            {
                                withStyle(
                                    style = SpanStyle(
                                        color = textColorDark,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = NotoSans
                                    )
                                ) {
                                    append(stringResource(R.string.showing_text))
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = greenOnline,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = NotoSans
                                    )
                                ) {
                                    append("${filterdDidiList.value.size}")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = textColorDark,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = NotoSans
                                    )
                                ) {
                                    append(if (filterdDidiList.value.size > 1) stringResource(R.string.result_text_plural) else  stringResource(R.string.result_text_singular))
                                }
                            }
                        )
                    }

                    item {
                        Spacer(
                            modifier = Modifier
                                .height(10.dp)
                                .fillMaxWidth()
                        )
                    }

                    itemsIndexed(filterdDidiList.value) { index, didi ->
                        ScoreComparisonDidiCard(modifier = Modifier, didiEntity = didi, passingScore = viewModel.questionPassingScore.collectAsState().value) { didiEntity ->
                            navController.navigate("bpc_pat_complete_didi_summary_screen/${didiEntity.id}/$ARG_FROM_PAT_DIDI_LIST_SCREEN")
                        }
                        Spacer(modifier = Modifier.height(10.dp))
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

            positiveButtonText = stringResource(id = R.string.done_text),
            negativeButtonRequired = false,
            positiveButtonOnClick = {
                navController.navigate(Graph.HOME) {
                    popUpTo(HomeScreens.BPC_PROGRESS_SCREEN.route) {
                        inclusive = true
                    }
                }
            },
            negativeButtonOnClick = {/*Nothing to do here*/ }
        )
    }
}

@Composable
fun ScoreComparisonDidiCard(
    modifier: Modifier = Modifier,
    didiEntity: DidiEntity,
    passingScore: Int,
    onScoreCardClicked: (didiEntity: DidiEntity) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        elevation = 10.dp,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(color = languageItemActiveBg, shape = RoundedCornerShape(6.dp))
            .border(1.dp, color = languageItemActiveBg, shape = RoundedCornerShape(6.dp))
            .indication(
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = true,
                    color = Color.White
                )
            )
            .clickable {
                onScoreCardClicked(didiEntity)
            }
            .then(modifier)
    ) {
        Column {

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = if (didiEntity.localPath.isNotEmpty()) rememberImagePainter(
                        Uri.fromFile(
                            File(
                                didiEntity.localPath.split("|")[0]
                            )
                        )
                    ) else painterResource(id = R.drawable.didi_icon),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .height(40.dp)
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .border(
                            width = 2.dp,
                            color = brownDark,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(languageItemActiveBg)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = didiEntity.name,
                        color = textColorDark,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.home_icn),
                            contentDescription = "home image",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = didiEntity.cohortName,
                            style = TextStyle(
                                color = textColorDark,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = NotoSans
                            ),
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }

            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = comparisonCardDividerColor,
                thickness = 1.dp
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {

                ScoreItem(didiEntity = didiEntity, itemName = stringResource(R.string.crp_score_text), itemType = CRP_USER_TYPE)

                ScoreItem(didiEntity = didiEntity, itemName = stringResource(R.string.bpc_score_text), itemType = BPC_USER_TYPE)

            }

            Row(
                Modifier
                    .background(
                        if ((didiEntity.crpScore
                                ?: 0.0) >= passingScore.toDouble() && (didiEntity.score
                                ?: 0.0) >= passingScore.toDouble()
                        ) greenOnline else unmatchedOrangeColor,
                        shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = if ((didiEntity.crpScore
                            ?: 0.0) >= passingScore.toDouble() && (didiEntity.score
                            ?: 0.0) >= passingScore.toDouble()
                    ) painterResource(id = R.drawable.icon_feather_check_circle_white)
                    else painterResource(
                        id = R.drawable.ic_cross_circle_white
                    ),
                    contentDescription = null,
                    tint = white
                )
                Text(
                    text = if ((didiEntity.crpScore
                            ?: 0.0) >= passingScore.toDouble() && (didiEntity.score
                            ?: 0.0) >= passingScore.toDouble()
                    ) stringResource(R.string.matched_text) else stringResource(R.string.unmatched_text),
                    color = white,
                    style = smallerTextStyle,
                    modifier = Modifier.absolutePadding(bottom = 3.dp)
                )
            }

        }
    }
}

@Composable
fun ExpandableSummaryBox(
    modifier: Modifier = Modifier,
    expanded: Boolean
) {

    AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
        Column() {

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.info_icn),
                    contentDescription = null,
                    tint = textColorDark,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "The Match % should be more than 70%",
                    color = textColorDark,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier.absolutePadding(bottom = 3.dp)
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
        }
    }

}

@Composable
fun ScoreItem(
    modifier: Modifier = Modifier,
    didiEntity: DidiEntity,
    itemName: String,
    itemType: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = itemName,
            color = textColorDark,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = NotoSans
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = blueDark,
                    shape = CircleShape
                )
                .background(
                    Color.White,
                    shape = CircleShape
                )
                .padding(6.dp)
                .size(18.dp)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (itemType.equals(CRP_USER_TYPE, true)) (didiEntity.crpScore ?: 0.0).toInt().toString() else (didiEntity.score ?: 0.0).toInt().toString(),
                color = textColorDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                style = TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    lineHeight = 12.sp
                )
            )
        }
    }
}



