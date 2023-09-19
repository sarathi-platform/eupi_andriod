package com.patsurvey.nudge.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layoutId
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
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.rejectEndorsementTextColor
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleMediumWeight
import com.patsurvey.nudge.activities.ui.theme.textColorBlueLight
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.veryLargeTextStyle
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.database.DidiEntity



@Composable
fun SummaryBox(
    modifier: Modifier = Modifier,
    count: Int,
    boxTitle: String,
    boxColor: Color,
    onSummaryBoxClicked: () -> Unit
) {
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
                onSummaryBoxClicked()
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
fun DidiItemCardForPatSummary(
    didi: DidiEntity,
    modifier: Modifier,
    onItemClick: (DidiEntity) -> Unit
) {
    Card(
        elevation = 10.dp,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = white, shape = RoundedCornerShape(6.dp))
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
                        didi = didi,
                        modifier = Modifier.layoutId("didiImage")
                    )
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

                        /*Image(
                            painter = painterResource(id = R.drawable.ic_completed_tick),
                            contentDescription = "home image",
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .padding(5.dp)
                                .layoutId("successImage")
                        )*/
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
                }
            }
            if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal) {
                Divider(
                    color = borderGreyLight,
                    thickness = 1.dp,
                    modifier = Modifier
                )
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp, top = 4.dp)
                    .padding(horizontal = 20.dp)
                    .clickable {
                        onItemClick(didi)
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

@Composable
fun DidiItemCardForVoForSummary(
    navController: NavHostController,
    didi: DidiEntity,
    modifier: Modifier,
    onItemClick: (DidiEntity) -> Unit
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
                        didi = didi,
                        modifier = Modifier.layoutId("didiImage")
                    )
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


                }
            }
            Divider(
                color = borderGreyLight,
                thickness = 1.dp,
                modifier = Modifier
                    .layoutId("divider")
                    .padding(bottom = 14.dp, top = 14.dp)
            )
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

private fun decoupledConstraints(): ConstraintSet {
    return ConstraintSet {
        val didiImage = createRefFor("didiImage")
        val didiName = createRefFor("didiName")
        val didiRow = createRefFor("didiRow")
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
        constrain(homeImage) {
            top.linkTo(village.top, margin = 3.dp)
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


@Composable
fun RowScope.TableCell(
    text: String,
    style: TextStyle,
    alignment: TextAlign,
    weight: Float,
    modifier: Modifier
) {
    Text(
        text = text,
        style = style,
        textAlign = alignment,
        modifier = Modifier.weight(weight).then(modifier)
    )
}