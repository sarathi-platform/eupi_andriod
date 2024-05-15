package com.sarathi.missionactivitytask.ui.basic.mission

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.constants.MissionActivityConstants.BLANK_STRING
import com.sarathi.missionactivitytask.ui.components.LinearProgressBar
import com.sarathi.missionactivitytask.ui.components.PrimaryButton
import com.sarathi.missionactivitytask.ui.components.SecondaryButton
import com.sarathi.missionactivitytask.ui.theme.*
import com.sarathi.missionactivitytask.ui.utils.*

@Composable
fun BasicActivityCard(
    title: String = BLANK_STRING,
    needToShowProgressBar: Boolean = false,
    status: StatusEnum = StatusEnum.Pending,
    pendingCount: Int = 0,
    totalCount: Int = 0,
    countStatusText: String = BLANK_STRING,
    primaryButtonText: String = BLANK_STRING,
    secondaryButtonText: String = BLANK_STRING,
    topHeaderText: String = BLANK_STRING,
    prefixIcon: Int = R.drawable.ic_group_icon,
    suffixIcon: Int = R.drawable.ic_arrow_forward_ios_24,
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = dimen_10_dp),
        modifier = Modifier
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
        Column(modifier = Modifier.fillMaxWidth()) {
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
                prefixIcon = prefixIcon,
                suffixIcon = suffixIcon
            )
            if (needToShowProgressBar) {
                if (totalCount > 0) {
                    val progress = pendingCount.toFloat() / totalCount
                    LinearProgressBar(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimen_30_dp, vertical = dimen_16_dp)
                    )
                }
            }
            if (totalCount > 0) {
                InfoText(pendingCount, totalCount, countStatusText)
            }
            ActionButtons(
                primaryButtonText,
                secondaryButtonText
            )
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
fun ContentBody(title: String, prefixIcon: Int, suffixIcon: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
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

        Icon(
            painter = painterResource(id = suffixIcon),
            contentDescription = null,  // Provide descriptions
            modifier = Modifier.size(dimen_24_dp)
        )
    }
}

@Composable
fun InfoText(pendingCount: Int, totalCount: Int, countStatusText: String) {
    Text(
        text = "$pendingCount / $totalCount $countStatusText",
        style = smallTextStyleMediumWeight2.copy(color = blueDark),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = dimen_24_dp)
    )
}

@Composable
fun ActionButtons(primaryButtonText: String, secondaryButtonText: String) {
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
                onClick = { /*TODO*/ },
                modifier = Modifier.weight(weight_100_percent)
            )
        }
    }
}


