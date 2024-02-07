package com.nrlm.baselinesurvey.ui.mission_screen.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.borderGreyLight
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.greenOnline
import com.nrlm.baselinesurvey.ui.theme.greyColor
import com.nrlm.baselinesurvey.ui.theme.greyLightColor
import com.nrlm.baselinesurvey.ui.theme.languageItemActiveBg
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleMediumWeight
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleNormalWeight
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyle
import com.nrlm.baselinesurvey.ui.theme.white

@Composable
fun MissonRowScreen_1(
    modifier: Modifier = Modifier,
    missionName: String = BLANK_STRING,
    missionDueDate: String = BLANK_STRING,
    onViewStatusClick: () -> Unit,
    onStartClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(white)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(greyLightColor)
                    .padding(5.dp)
            ) {
                Text(text = stringResource(id = R.string.start_by_x_date,missionDueDate), style = smallerTextStyle)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp,),
                verticalAlignment =Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_group_icon),
                    contentDescription = "more action button",
                    tint = blueDark,
                )
                Text(
                    text = missionName,
                    fontFamily = NotoSans,
                    color = blueDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            }
            Text(
                text = "Task Pending - 40",
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            )

//            LinearProgressIndicator(
//                progress = 0.5f,
//                color = greenOnline,
//                backgroundColor = white,
//                strokeCap = StrokeCap.Round,
//                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
//            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_18_dp),
                horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)
            ) {
                Button(
                    onClick = {},
                    enabled = true,
                    shape = RoundedCornerShape(roundedCornerRadiusDefault),
                    border = BorderStroke(dimen_1_dp, borderGreyLight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = languageItemActiveBg, contentColor = blueDark
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    androidx.compose.material3.Text(
                        text = "View status", style = smallTextStyleNormalWeight
                    )
                }


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
                        text = "Start", style = smallTextStyleMediumWeight, color = white
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
    MissonRowScreen_1(onStartClick = {},
        onViewStatusClick = {}
    )
}