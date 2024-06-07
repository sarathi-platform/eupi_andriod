package com.sarathi.surveymanager.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nudge.core.ui.events.theme.black100Percent
import com.nudge.core.ui.events.theme.borderGreyLight
import com.nudge.core.ui.events.theme.defaultTextStyle
import com.nudge.core.ui.events.theme.dimen_10_dp
import com.nudge.core.ui.events.theme.dimen_1_dp
import com.nudge.core.ui.events.theme.dimen_20_dp
import com.nudge.core.ui.events.theme.dimen_2_dp
import com.nudge.core.ui.events.theme.dimen_40_dp
import com.nudge.core.ui.events.theme.greyColor
import com.nudge.core.ui.events.theme.quesOptionTextStyle
import com.nudge.core.ui.events.theme.white

@Composable
fun DisbursementCard(
    subTitle1: String = "15 Jan",
    subTitle2: String = "₹ 500",
    subTitle3: String = "Backyard Poultry",
    subTitle4: String = "Direct Bank Transfer",
    onEditSurvey: () -> Unit,
    onDeleteSurvey: () -> Unit

) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimen_10_dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextRow(text1 = "Date:", text2 = subTitle1)
            TextRow(text1 = "Amount:", text2 = subTitle2)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimen_10_dp)
        ) {
            TextRow(text1 = "Nature:", text2 = subTitle3)
        }
        Row(
            modifier = Modifier
                .padding(dimen_10_dp)
        ) {
            TextRow(text1 = "Mode:", text2 = subTitle4)
        }
        Row {
            Row(
                modifier = Modifier
                    .weight(1.0f)
                    .height(dimen_40_dp)
                    .clickable {
                        onEditSurvey()
                    }
                    .background(
                        color = white, shape = RoundedCornerShape(dimen_1_dp)
                    )
                    .border(
                        width = 0.5.dp,
                        color = borderGreyLight,
                        shape = RoundedCornerShape(1.dp)
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .size(dimen_20_dp)
                        .padding(dimen_2_dp),
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Forward",
                    tint = Color.Black
                )
                Text(
                    text = "Edit", style = defaultTextStyle,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Row(
                modifier = Modifier
                    .weight(1.0f)
                    .height(dimen_40_dp)
                    .clickable {
                        onDeleteSurvey()
                    }
                    .background(
                        color = white, shape = RoundedCornerShape(dimen_1_dp)
                    )
                    .border(
                        width = 0.5.dp,
                        color = borderGreyLight,
                        shape = RoundedCornerShape(dimen_1_dp)
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .size(dimen_20_dp)
                        .padding(dimen_2_dp),
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Forward",
                    tint = Color.Black
                )
                Text(
                    text = "Delete", style = defaultTextStyle,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
private fun TextRow(text1: String, text2: String) {
    Row() {
        Text(text = text1, style = quesOptionTextStyle.copy(color = greyColor))
        Text(text = text2, style = quesOptionTextStyle.copy(color = black100Percent))
    }
}