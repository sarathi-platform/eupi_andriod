package com.sarathi.missionactivitytask.ui.basic_content.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.greyBorderColor
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.unmatchedOrangeColor
import com.nudge.core.ui.theme.white
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.components.PrimaryButton
import com.sarathi.missionactivitytask.utils.StatusEnum

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun GrantTaskCard(
    title: String,
    subTitle: String,
    primaryButtonText: String,
    secondaryButtonText: String,
    status: String,
    modifier: Modifier = Modifier,
    isHamletIcon: Boolean = false,
) {
    androidx.compose.material3.Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 30.dp
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                color = if (status == StatusEnum.COMPLETED.name) greenOnline else greyBorderColor,
                shape = RoundedCornerShape(6.dp)
            )
            .background(Color.Transparent)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(white)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isHamletIcon) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_vo_name_icon),
                        contentDescription = "more action button",
                        modifier = Modifier.size(30.dp),
                    )
                }

                Text(
                    text = title,
                    style = defaultTextStyle,
                    modifier = Modifier
                        .padding(horizontal = 5.dp),
                    color = blueDark
                )
                Spacer(modifier = Modifier.weight(1f))
                if (status == (StatusEnum.COMPLETED.name)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check_circle),
                        contentDescription = "more action button",
                        modifier = Modifier.size(20.dp),
                        tint = greenOnline,
                    )
                } else if (status == StatusEnum.INPROGRESS.name) {
                    Text(
                        text = "In Progress",
                        style = defaultTextStyle,
                        modifier = Modifier
                            .padding(horizontal = 5.dp),
                        color = unmatchedOrangeColor
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (subTitle.isNotBlank()) {
                    Icon(
                        painter = painterResource(id = R.drawable.home_icn),
                        contentDescription = "more action button",
                        tint = blueDark,
                    )
                    Text(
                        text = subTitle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp),
                        color = blueDark,
                        style = newMediumTextStyle
                    )
                }
            }
            if (status == (StatusEnum.COMPLETED.name) || status == (StatusEnum.INPROGRESS.name)) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "10",
                        color = blueDark,
                        style = newMediumTextStyle,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(dimen_2_dp)
                            .drawBehind {
                                drawCircle(
                                    color = greyBorderColor,
                                    radius = this.size.maxDimension / 2.0f
                                )
                            },
                    )
                    Spacer(modifier = Modifier.width(dimen_8_dp))
                    Text(
                        text = "Didis",
                        modifier = Modifier.weight(.4f),
                        color = blueDark,
                        style = newMediumTextStyle
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_received_money),
                        contentDescription = "Received: ₹40,000",
                        tint = blueDark,
                    )
                    Text(
                        text = "Received: ₹40,000",
                        modifier = Modifier
                            .padding(horizontal = dimen_5_dp),
                        color = blueDark,
                        style = newMediumTextStyle
                    )
                }
            }
            if (status == StatusEnum.NOT_STARTED.name) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimen_16_dp),
                    horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    if (primaryButtonText.isNotBlank()) {
                        PrimaryButton(
                            text = "Start",
                            onClick = { /*TODO*/ },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 5.dp)
                ) {
                    Divider(
                        modifier = Modifier
                            .height(1.dp)
                            .weight(1f)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimen_16_dp),
                    horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)
                ) {
                    if (status == StatusEnum.COMPLETED.name) {
                        Text(
                            text = "View",
                            modifier = Modifier
                                .padding(horizontal = 5.dp),
                            color = blueDark,
                            style = newMediumTextStyle
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "more action button",
                            tint = blueDark,
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                        if (primaryButtonText.isNotBlank()) {
                            PrimaryButton(
                                text = "Continue",
                                onClick = { /*TODO*/ },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                }
            }
        }

    }
}
