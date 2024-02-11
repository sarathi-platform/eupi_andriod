package com.nrlm.baselinesurvey.ui.mission_screen.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.black100Percent
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.greenLight
import com.nrlm.baselinesurvey.ui.theme.greenOnline
import com.nrlm.baselinesurvey.ui.theme.greyLightColor
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleMediumWeight
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyle
import com.nrlm.baselinesurvey.ui.theme.trackColor
import com.nrlm.baselinesurvey.ui.theme.white


@Composable
fun MissonRowScreen_1(
    modifier: Modifier = Modifier,
    mission: MissionEntity,
    missionDueDate: String = BLANK_STRING,
    onViewStatusClick: () -> Unit,
    onStartClick: () -> Unit
) {
    val missionTask = mission.activityTaskSize
    val curPercentage = animateFloatAsState(
        targetValue = mission.activityComplete / mission.activityTaskSize.toFloat(),
        label = "",
        animationSpec = tween()
    )

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                color = if (mission.missionStatus == 2) greenOnline else greyLightColor,
                shape = RoundedCornerShape(6.dp)
            )
            .background(Color.Transparent)
    ) {

        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(if (mission.missionStatus == 2) greenLight else white)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(if (mission.missionStatus == 2) greenOnline else greyLightColor)
                    .padding(horizontal = 16.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (mission.missionStatus != 2) stringResource(
                        id = R.string.start_by_x_date,
                        missionDueDate
                    ) else "Completed",
                    style = smallerTextStyle,
                    color = if (mission.missionStatus == 2) white else black100Percent,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_group_icon),
                    contentDescription = "more action button",
                    tint = blueDark,
                )
                Text(
                    text = mission.missionName,
                    fontFamily = NotoSans,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    fontWeight = FontWeight.SemiBold,
                    color = blueDark,
                    fontSize = 20.sp
                )
            }
            Text(
                text = "Task Pending - ${mission.activityComplete}/${mission.activityTaskSize}",
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = blueDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            )
            LinearProgressIndicator(
                progress = curPercentage.value,
                color = greenOnline,
                trackColor = trackColor,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_18_dp),
                horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))

//                Button(
//                    onClick = {},
//                    enabled = true,
//                    shape = RoundedCornerShape(roundedCornerRadiusDefault),
//                    border = BorderStroke(dimen_1_dp, borderGreyLight),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = languageItemActiveBg, contentColor = blueDark
//                    ),
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text(
//                        text = "View status", style = smallTextStyleNormalWeight
//                    )
//                }


                Button(
                    onClick = {
                        onStartClick()
                    },
                    enabled = true,
                    shape = RoundedCornerShape(roundedCornerRadiusDefault),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueDark, contentColor = white
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (mission.missionStatus == 2) "View" else "Start",
                        style = smallTextStyleMediumWeight,
                        color = white
                    )
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Positive Button",
                        tint = white,
                        modifier = Modifier.absolutePadding(top = 2.dp, left = 2.dp)
                    )
                }
            }

        }

    }

}

@Preview(showBackground = true)
@Composable
fun MissonRowScreenPreview() {
//    MissonRowScreen_1(onStartClick = {},
//        onViewStatusClick = {}
//    )
}