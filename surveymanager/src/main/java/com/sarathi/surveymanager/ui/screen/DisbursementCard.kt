package com.sarathi.surveymanager.ui.screen

import androidx.compose.foundation.Image
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.events.theme.black1
import com.nudge.core.ui.events.theme.blueDark
import com.nudge.core.ui.events.theme.borderGreyLight
import com.nudge.core.ui.events.theme.defaultTextStyle
import com.nudge.core.ui.events.theme.dimen_10_dp
import com.nudge.core.ui.events.theme.dimen_16_dp
import com.nudge.core.ui.events.theme.dimen_1_dp
import com.nudge.core.ui.events.theme.dimen_4_dp
import com.nudge.core.ui.events.theme.dimen_50_dp
import com.nudge.core.ui.events.theme.dimen_5_dp
import com.nudge.core.ui.events.theme.greyColor
import com.nudge.core.ui.events.theme.white
import com.sarathi.surveymanager.R

@Composable
fun DisbursementCard(
    subTitle1: String = BLANK_STRING,
    subTitle2: String = BLANK_STRING,
    subTitle3: String = BLANK_STRING,
    subTitle4: String = BLANK_STRING,
    subTitle5: String = BLANK_STRING,
    onEditSurvey: () -> Unit,
    onDeleteSurvey: () -> Unit

) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimen_10_dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimen_10_dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextRow(text1 = stringResource(R.string.date), text2 = subTitle1)
            TextRow(text1 = stringResource(R.string.amount), text2 = subTitle2)
        }
        if (subTitle3.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_10_dp)
            ) {
                TextRow(text1 = stringResource(R.string.nature), text2 = subTitle3)
            }
        }
        if (subTitle4.isNotBlank()) {
            Row(
                modifier = Modifier
                    .padding(dimen_10_dp)
            ) {
                TextRow(text1 = stringResource(R.string.mode), text2 = subTitle4)
            }
        }
        if (subTitle5.isNotBlank()) {
            Row(
                modifier = Modifier
                    .padding(dimen_10_dp)
            ) {
                TextRow(text1 = stringResource(R.string.no_of_didi_s), text2 = subTitle5)
            }
        }
        Row {
            Row(
                modifier = Modifier
                    .weight(0.4f)
                    .height(dimen_50_dp)
                    .padding(dimen_5_dp)
                    .clickable { onEditSurvey() }
                    .background(color = white, shape = RoundedCornerShape(dimen_1_dp))
                    .border(
                        width = 0.5.dp,
                        color = borderGreyLight,
                        shape = RoundedCornerShape(dimen_1_dp)
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_edit_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = dimen_4_dp)
                        .size(dimen_16_dp)
                        .align(Alignment.CenterVertically),
                    colorFilter = ColorFilter.tint(blueDark)
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(R.string.edit),
                    style = defaultTextStyle,
                    color = blueDark,
                )
            }
            Row(
                modifier = Modifier
                    .weight(0.4f)
                    .height(dimen_50_dp)
                    .padding(dimen_5_dp)
                    .clickable { onDeleteSurvey() }
                    .background(color = white, shape = RoundedCornerShape(dimen_1_dp))
                    .border(
                        width = 0.5.dp,
                        color = borderGreyLight,
                        shape = RoundedCornerShape(dimen_1_dp)
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_delete_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = dimen_4_dp)
                        .size(dimen_16_dp)
                        .align(Alignment.CenterVertically),
                    colorFilter = ColorFilter.tint(blueDark)
                )
                Text(
                    text = stringResource(R.string.delete),
                    style = defaultTextStyle,
                    color = blueDark,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
private fun TextRow(text1: String, text2: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (text1.isNotBlank()) {
            Text(
                modifier = Modifier.padding(end = dimen_5_dp),
                text = text1,
                style = defaultTextStyle.copy(color = greyColor)
            )
        }
        if (text2.isNotBlank()) {
            Text(text = text2, style = defaultTextStyle.copy(color = black1))
        }
    }
}