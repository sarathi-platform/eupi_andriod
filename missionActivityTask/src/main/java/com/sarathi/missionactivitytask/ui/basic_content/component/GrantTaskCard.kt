package com.sarathi.missionactivitytask.ui.basic_content.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.theme.blueDark
import com.sarathi.missionactivitytask.ui.theme.defaultTextStyle
import com.sarathi.missionactivitytask.ui.theme.dimen_10_dp
import com.sarathi.missionactivitytask.ui.theme.dimen_18_dp
import com.sarathi.missionactivitytask.ui.theme.newMediumTextStyle
import com.sarathi.missionactivitytask.ui.theme.roundedCornerRadiusDefault
import com.sarathi.missionactivitytask.ui.theme.smallTextStyleMediumWeight
import com.sarathi.missionactivitytask.ui.theme.white

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
    isCompleted: Boolean = true
) {
    androidx.compose.material3.Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                color = white,
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
                    .padding(start = 16.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isHamletIcon) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_vo_name_icon),
                        contentDescription = "more action button",
                        modifier = Modifier.size(30.dp),
                        tint = blueDark,
                    )
                }

                Text(
                    text = title,
                    style = defaultTextStyle,
                    modifier = Modifier
                        .padding(horizontal = 5.dp),
                    color = blueDark
                )
                if (isCompleted) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        painter = painterResource(id = R.drawable.icon_check),
                        contentDescription = "more action button",
                        modifier = Modifier.size(30.dp),
                        tint = blueDark,
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
            if (isCompleted) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = " 10 Didis ",
                        modifier = Modifier.weight(.4f),
                        color = blueDark,
                        style = newMediumTextStyle
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.home_icn),
                        contentDescription = "Received: ₹40,000",
                        tint = blueDark,
                    )
                    Text(
                        text = "Received: ₹40,000",
                        modifier = Modifier
                            .padding(horizontal = 5.dp),
                        color = blueDark,
                        style = newMediumTextStyle
                    )
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 5.dp)) {
                Divider(
                    modifier = Modifier
                        .height(1.dp)
                        .weight(1f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_18_dp),
                horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)
            ) {
                if (isCompleted) {
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
                    Button(
                        onClick = {
                            //  onStartClick()
                        },
                        enabled = true,
                        shape = RoundedCornerShape(roundedCornerRadiusDefault),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = blueDark, contentColor = white
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = primaryButtonText,
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
}
