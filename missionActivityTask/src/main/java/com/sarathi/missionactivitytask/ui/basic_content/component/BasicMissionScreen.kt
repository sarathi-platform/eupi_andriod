package com.sarathi.missionactivitytask.ui.basic_content.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_18_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_24_dp
import com.nudge.core.ui.theme.dimen_30_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.largeTextStyle
import com.nudge.core.ui.theme.smallTextStyleMediumWeight2
import com.nudge.core.ui.theme.smallerTextStyle
import com.nudge.core.ui.theme.weight_100_percent
import com.nudge.core.ui.theme.white
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.components.LinearProgressBarComponent
import com.sarathi.missionactivitytask.ui.components.PrimaryButton
import com.sarathi.missionactivitytask.ui.components.SecondaryButton
import com.sarathi.missionactivitytask.utils.StatusEnum
import com.sarathi.missionactivitytask.utils.statusColor

@Composable
fun BasicMissionCard(
    title: String = BLANK_STRING,
    needToShowProgressBar: Boolean = false,
    status: String = StatusEnum.PENDING.name,
    pendingCount: Int = 0,
    totalCount: Int = 0,
    countStatusText: String = BLANK_STRING,
    primaryButtonText: String = BLANK_STRING,
    secondaryButtonText: String = BLANK_STRING,
    topHeaderText: String = BLANK_STRING,
    prefixIcon: Int = R.drawable.ic_group_icon,
    onPrimaryClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = dimen_10_dp),
        modifier = Modifier
            .clickable {
                onPrimaryClick()
            }
            .fillMaxWidth()
            .padding(dimen_16_dp)
            .clip(RoundedCornerShape(dimen_6_dp))
            .border(
                width = dimen_1_dp,
                color = statusColor(status),
                shape = RoundedCornerShape(dimen_6_dp)
            )
            .background(Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(white)
        ) {
            if (topHeaderText.isNotEmpty()) {
                TopHeader(
                    topHeaderText, modifier = Modifier
                        .background(statusColor(status))
                        .fillMaxWidth()
                        .padding(horizontal = dimen_16_dp)
                        .height(dimen_24_dp)
                )
            }
            ContentBody(
                title = title,
                prefixIcon = prefixIcon
            )
            if (needToShowProgressBar && totalCount > 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimen_30_dp, vertical = dimen_16_dp)
                ) {
                    LinearProgressBarComponent(
                        progress = pendingCount.toFloat() / totalCount,
                    )
                    Text(
                        text = "$pendingCount / $totalCount $countStatusText",
                        style = smallTextStyleMediumWeight2.copy(color = blueDark),
                    )
                }
            }
            if (status != StatusEnum.COMPLETED.name) {
                ActionButtons(
                    primaryButtonText,
                    secondaryButtonText,
                    onPrimaryClick = {
                        onPrimaryClick()
                    }
                )
            }
        }
    }
}

@Composable
fun TopHeader(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = text,
            style = smallerTextStyle.copy(color = Color.White)
        )
    }
}

@Composable
fun ContentBody(title: String, prefixIcon: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = prefixIcon),
            contentDescription = null,  // Ideally, provide meaningful descriptions
            modifier = Modifier.size(dimen_24_dp)
        )

        Text(
            text = title,
            style = largeTextStyle.copy(color = blueDark),
            modifier = Modifier
                .padding(start = dimen_5_dp)
                .weight(weight_100_percent),
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(dimen_5_dp))

    }
}


@Composable
fun ActionButtons(
    primaryButtonText: String,
    secondaryButtonText: String,
    onPrimaryClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen_18_dp),
        horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)
    ) {
        Spacer(modifier = Modifier.weight(weight_100_percent))

        if (secondaryButtonText.isNotEmpty()) {
            SecondaryButton(
                text = secondaryButtonText,
                onClick = { /*TODO*/ },
                modifier = Modifier.weight(weight_100_percent)
            )
        }
        if (primaryButtonText.isNotEmpty()) {
            PrimaryButton(
                text = primaryButtonText,
                onClick = { onPrimaryClick() },
                modifier = Modifier.weight(weight_100_percent)
            )
        }
    }
}


