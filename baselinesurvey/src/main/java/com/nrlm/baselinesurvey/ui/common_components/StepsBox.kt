package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.black1
import com.nrlm.baselinesurvey.ui.theme.black100Percent
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.buttonTextStyle
import com.nrlm.baselinesurvey.ui.theme.greenOnline
import com.nrlm.baselinesurvey.ui.theme.greyBorder
import com.nrlm.baselinesurvey.ui.theme.inprogressYellow
import com.nrlm.baselinesurvey.ui.theme.mediumBoldTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleMediumWeight
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleMediumWeight2
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyleNormalWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white

@Composable
fun StepsBox(
    modifier: Modifier = Modifier,
    boxTitle: String,
    subTitle: String,
    stepNo: Int,
    index: Int,
    iconResourceId: Int,
    isCompleted: Boolean = false,
    backgroundColor:Color?= white,
    onclick: (Int) -> Unit
) {
    if (stepNo == 6)
        Spacer(modifier = Modifier.height(20.dp))

    ConstraintLayout(
        modifier = Modifier
            .background(Color.White)
            .border(
                width = 0.dp,
                color = Color.Transparent,
            )
            .then(modifier)
    ) {
        val (step_no, stepBox) = createRefs()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = if (isCompleted) greenOnline else greyBorder,
                    shape = RoundedCornerShape(6.dp)
                )
                .background(backgroundColor?: white)
                .clickable {
                    onclick(index)
                }
                .constrainAs(stepBox) {
                    start.linkTo(parent.start)
                    top.linkTo(step_no.bottom, margin = -16.dp)
                }

        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top =8.dp, bottom = 10.dp)
                    .padding(end = 16.dp, start = 8.dp),
            ) {
                val (textContainer, buttonContainer, iconContainer) = createRefs()
                Icon(
                    painter = painterResource(id = iconResourceId),
                    contentDescription = null,
                    tint = Color.Unspecified,

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
                        text = if (boxTitle.contains("pat ", true)) boxTitle.replace(
                            "pat ",
                            "PAT ",
                            true
                        ) else boxTitle,
                        modifier = Modifier
                            .padding(
                                top = 10.dp,
                                end = 10.dp
                            )
                            .fillMaxWidth(),
                        softWrap = true,
                        color = black100Percent,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = mediumBoldTextStyle
                    )
                    //TODO add string for other steps when steps is complete.
                    if (subTitle != "") {
                        Text(
                            text = subTitle,
                            color = black100Percent,
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
                        color = greyBorder,
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
    StepsBox(
        boxTitle = "TransectBox",
        subTitle = "10 Poor didis identified",
        stepNo = 1,
        index = 1,
        iconResourceId = R.drawable.ic_mission_inprogress,
        onclick = {})
}

@Preview(showBackground = true)
@Composable
fun IconButtonForwardPreview() {
    IconButtonForward(
        modifier = Modifier
            .size(80.dp)
    ) {}
}

@Composable
fun TextButtonWithIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(modifier = Modifier
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
        Text(
            text = stringResource(id = R.string.show),
            style = smallTextStyleMediumWeight,
            color = textColorDark,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
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
