package com.patsurvey.nudge.customviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.data.prefs.PrefRepo

@Composable
fun VOAndVillageBoxView(
 prefRepo: PrefRepo,
 modifier: Modifier,
 startPadding: Dp?=16.dp
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .then(modifier),
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
        ) {
            Row(modifier = Modifier.padding(start = startPadding?:16.dp, end = 16.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.home_icn),
                    contentDescription = null,
                    tint =textColorDark,
                )
                Text(
                    text = prefRepo.getSelectedVillage().name,
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = textColorDark,
                    fontSize = 14.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(
                modifier = Modifier
                    .absolutePadding(left = 4.dp)
                    .padding(start = startPadding?:16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "VO: ",
                    modifier = Modifier,
                    color = textColorDark,
                    fontSize = 14.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = prefRepo.getSelectedVillage().federationName,
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = textColorDark,
                    fontSize = 14.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
