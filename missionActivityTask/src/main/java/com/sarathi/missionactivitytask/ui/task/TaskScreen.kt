package com.sarathi.missionactivitytask.ui.task

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.components.PrimaryButton
import com.sarathi.missionactivitytask.ui.components.SecondaryButton
import com.sarathi.missionactivitytask.ui.theme.blueDark
import com.sarathi.missionactivitytask.ui.theme.defaultTextStyle
import com.sarathi.missionactivitytask.ui.theme.greenOnline
import com.sarathi.missionactivitytask.ui.theme.greyBorderColor
import com.sarathi.missionactivitytask.ui.theme.newMediumTextStyle
import com.sarathi.missionactivitytask.ui.theme.red
import com.sarathi.missionactivitytask.ui.theme.smallTextStyle
import com.sarathi.missionactivitytask.ui.theme.smallTextStyleWithNormalWeight
import com.sarathi.missionactivitytask.ui.theme.unmatchedOrangeColor
import com.sarathi.missionactivitytask.ui.theme.white
import com.sarathi.missionactivitytask.utils.StatusEnum

@Composable
fun TaskDisbursementCard(
    profileImage: Painter,
    name: String,
    address: String,
    location: String,
    sanctionedAmount: String,
    disbursedAmount: String,
    status: String = StatusEnum.COMPLETED.name,
    modifier: Modifier,
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
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = profileImage,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                color = red,
                                shape = RoundedCornerShape(6.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = name,
                            style = defaultTextStyle,
                            color = blueDark
                        )

                        Text(text = address, style = newMediumTextStyle, color = blueDark)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (status == (StatusEnum.COMPLETED.name)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check_circle),
                            contentDescription = "more action button",
                            modifier = Modifier.size(16.dp),
                            tint = greenOnline,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Completed", style = newMediumTextStyle, color = greenOnline)
                    }else if (status == (StatusEnum.INPROGRESS.name)) {
                        Text(text = "In Progress", style = newMediumTextStyle, color = unmatchedOrangeColor)
                    }

                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = location, style = newMediumTextStyle, color = blueDark)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_received_money),
                    contentDescription = null,
                    tint = blueDark
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sanctioned:",
                    style = smallTextStyleWithNormalWeight,
                    color = blueDark
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = sanctionedAmount,
                    style = smallTextStyle,
                    color = blueDark
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_received_money),
                    contentDescription = null,
                    tint = blueDark
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Disbursed:",
                    style = smallTextStyleWithNormalWeight,
                    color = blueDark
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = disbursedAmount,
                    style = smallTextStyle,
                    color = blueDark
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 5.dp)
            ) {
                Divider(
                    modifier = Modifier
                        .height(1.dp)
                        .weight(1f)
                )
            }
            if (status != StatusEnum.COMPLETED.name) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    SecondaryButton(
                        text = "Not Available",
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = 1.dp,
                                color = blueDark,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .height(45.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PrimaryButton(
                        text = if (status == (StatusEnum.INPROGRESS.name)) "Continue" else "Start",
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp),
                        isIcon = false
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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

@Preview
@Composable
fun MissionTaskDisbursement(){
    TaskDisbursementCard(
        profileImage = painterResource(id = R.drawable.profile_img),
        name = "Shanti Devi",
        address = "#45, Killu dada",
        location = "Sundar Pahari",
        sanctionedAmount = "₹2,000",
        disbursedAmount = "₹2,000",
        modifier = Modifier,
    )
}

