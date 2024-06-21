package com.sarathi.missionactivitytask.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.nudge.core.ui.theme.black100Percent
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.greenLight
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.greyBorderColor
import com.nudge.core.ui.theme.largeTextStyle
import com.nudge.core.ui.theme.smallTextStyleMediumWeight2
import com.nudge.core.ui.theme.smallerTextStyleNormalWeight
import com.nudge.core.ui.theme.stepIconCompleted
import com.nudge.core.ui.theme.stepIconEnableColor
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.sarathi.missionactivitytask.R


@Composable
fun StepsBoxGrantComponent(
    modifier: Modifier = Modifier,
    boxTitle: String,
    subTitle: String,
    stepNo: Int,
    index: Int,
    painter: Painter,
    totalCount: Int = 0,
    pendingCount: Int = 0,
    isCompleted: Boolean = false,
    isDividerVisible: Boolean = true,
    onclick: (Int) -> Unit
) {
    val curPercentage = animateFloatAsState(
        targetValue =
        (pendingCount / totalCount.toFloat()),
        label = "",
        animationSpec = tween()
    )
    val dividerMargins = 32.dp


    ConstraintLayout(
        modifier = Modifier
            .background(Color.White)
            .padding(5.dp)
            .border(
                width = 0.dp,
                color = Color.Transparent,
            )
            .then(modifier)
    ) {
        val (step_no, stepBox, divider1, divider2) = createRefs()
        BasicCardView(
            colors = CardDefaults.cardColors(containerColor = if (isCompleted) greenLight else white),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = if (isCompleted) greenOnline else greyBorderColor,
                    shape = RoundedCornerShape(6.dp)
                )
                .background(if (isCompleted) greenLight else white)
                .clickable {
                    onclick(index)
                }
                .constrainAs(stepBox) {
                    start.linkTo(parent.start)
                    top.linkTo(step_no.bottom, margin = (-16).dp)
                }

        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 10.dp)
                    .padding(end = 16.dp, start = 8.dp),
            ) {
                val (textContainer, buttonContainer, iconContainer) = createRefs()
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = if (isCompleted) stepIconCompleted else stepIconEnableColor,
                    modifier = Modifier
                        .constrainAs(iconContainer) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .size(48.dp)
                        .padding(
                            top = if (isCompleted) 0.dp else 6.dp,
                            start = if (isCompleted) 0.dp else 4.dp
                        )
                )


                Column(
                    modifier = Modifier
                        .padding(horizontal = 14.dp)
                        .constrainAs(textContainer) {
                            top.linkTo(iconContainer.top)
                            start.linkTo(iconContainer.end)
                            bottom.linkTo(iconContainer.bottom)
                            end.linkTo(buttonContainer.start)
                            width = Dimension.fillToConstraints
                        }
                        .fillMaxWidth()
                ) {
                    Text(
                        text = boxTitle,
                        modifier = Modifier
                            .padding(
                                top = 10.dp,
                                end = 10.dp
                            )
                            .fillMaxWidth(),
                        softWrap = true,
                        color = if (isCompleted) greenOnline else black100Percent,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = largeTextStyle
                    )
                    LinearProgressBarComponent(
                        progress = curPercentage.value
                    )
                    if (subTitle != "") {
                        Text(
                            text = subTitle,
                            color = if (isCompleted) greenOnline else black100Percent,
                            modifier = Modifier
                                .fillMaxWidth(),
                            softWrap = true,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2,
                            fontWeight = FontWeight.Medium,
                            style = smallTextStyleMediumWeight2
                        )
                    }
                }

                IconButtonForward(
                    modifier = Modifier
                        .constrainAs(buttonContainer) {
                            bottom.linkTo(textContainer.bottom)
                            top.linkTo(textContainer.top)
                            end.linkTo(parent.end)
                        }
                        .size(40.dp)
                ) {
                    onclick(index)
                }

            }
        }
        if (isCompleted) {
            Image(
                painter = painterResource(id = R.drawable.icon_check_circle_green),
                contentDescription = null,
                modifier = modifier
                    .border(
                        width = 2.dp,
                        color = Color.Transparent,
                        shape = CircleShape

                    )
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .constrainAs(step_no) {
                        start.linkTo(parent.start, margin = 16.dp)
                        top.linkTo(parent.top)
                    }
            )
        } else {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = greyBorderColor,
                        shape = CircleShape
                    )
                    .background(Color.White, shape = CircleShape)
                    .padding(6.dp)
                    .constrainAs(step_no) {
                        start.linkTo(parent.start, margin = 16.dp)
                        top.linkTo(parent.top)
                    }
            ) {
                Text(
                    text = "$stepNo",
                    color = textColorDark,
                    style = smallerTextStyleNormalWeight,
                    modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)

                )
            }

        }
        if (isDividerVisible) {
            Divider(
                color = greyBorderColor,
                modifier = Modifier
                    .height(8.dp)  //fill the max height
                    .width(1.dp)
                    .constrainAs(divider1) {
                        start.linkTo(parent.start, margin = dividerMargins)
                        top.linkTo(stepBox.bottom)
                    }
                    .padding(vertical = 2.dp)
            )

            Divider(
                color = greyBorderColor,
                modifier = Modifier
                    .height(8.dp)  //fill the max height
                    .width(1.dp)
                    .constrainAs(divider2) {
                        start.linkTo(parent.start, margin = dividerMargins)
                        top.linkTo(divider1.bottom)
                    }
                    .padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
fun IconButtonForward(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(blueDark)
            .clickable {
                onClick()
            }
            .indication(
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = true,
                    color = Color.White
                )
            )
            .then(modifier)
    ) {
        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun StepBoxPreview() {
    StepsBoxGrantComponent(
        boxTitle = "TransectBox",
        subTitle = "10 Poor didis identified",
        stepNo = 1,
        index = 1,
        isCompleted = false,
        painter = painterResource(id = R.drawable.ic_mission_inprogress),
        onclick = {})
}
