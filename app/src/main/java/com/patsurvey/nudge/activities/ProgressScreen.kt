package com.patsurvey.nudge.activities

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.patsurvey.nudge.activities.ui.theme.*

@Composable
fun StepsBox(
    boxTitle: String,
    stepNo: Int,
    isCompleted: Boolean = false,
    shouldBeActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val dividerMargins = 32.dp
    ConstraintLayout(
        modifier = Modifier
            .background(Color.White)
            .border(
                width = 0.dp,
                color = Color.Transparent,
            )
            .then(modifier)
    ) {
        val (step_no, stepBox, divider1, divider2) = createRefs()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = if (isCompleted) green else greyBorder,
                    shape = RoundedCornerShape(6.dp)
                )
                .background(Color.White)
                .constrainAs(stepBox) {
                    start.linkTo(parent.start)
                    top.linkTo(step_no.bottom, margin = -16.dp)
                }

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isCompleted) greenLight else Color.White)
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(modifier = Modifier.absolutePadding(left = 10.dp)) {
                    Text(
                        text = boxTitle/* "Transect Walk"*/,
                        color = textColorDark,
                        fontSize = 18.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 16.dp, end = 48.dp),
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis


                    )
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(size = 10.dp)
                        ) {
                            drawCircle(
                                color = if (isCompleted) greenDark else greyIndicator,
                            )
                        }
                        Text(
                            text = if (isCompleted) "Completed" else "Not Started",
                            color = if (isCompleted) greenDark else textColorDark,
                            fontSize = 12.sp,
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 6.dp, bottom = 4.dp)

                        )
                    }
                    if (isCompleted)
                        Spacer(modifier = Modifier.height(20.dp))
                }

                if (!isCompleted) {
                    BlueButton(
                        buttonText = "Start Now",
                        isArrowRequired = true,
                        shouldBeActive = shouldBeActive,
                        modifier = Modifier.padding(end = 14.dp),
                        onClick = {

                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = greyBorder,
                    shape = RoundedCornerShape(100.dp)
                )
                .background(Color.White)
                .padding(6.dp)
                .constrainAs(step_no) {
                    start.linkTo(parent.start, margin = 16.dp)
                    top.linkTo(parent.top)
                }
        ) {
            Text(
                text = "Step $stepNo",
                color = textColorDark,
                fontSize = 12.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 16.dp)

            )
        }

        if (stepNo < 5) {
            Divider(
                color = greyBorder,
                modifier = Modifier
                    .height(10.dp)  //fill the max height
                    .width(1.dp)
                    .constrainAs(divider1) {
                        start.linkTo(parent.start, margin = dividerMargins)
                        top.linkTo(stepBox.bottom)
                    }
                    .padding(vertical = 2.dp)
            )

            Divider(
                color = greyBorder,
                modifier = Modifier
                    .height(10.dp)  //fill the max height
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