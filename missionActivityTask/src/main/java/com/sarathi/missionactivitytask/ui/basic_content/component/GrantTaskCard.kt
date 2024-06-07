package com.sarathi.missionactivitytask.ui.basic_content.component

import android.text.TextUtils
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.events.theme.blueDark
import com.nudge.core.ui.events.theme.brownDark
import com.nudge.core.ui.events.theme.defaultTextStyle
import com.nudge.core.ui.events.theme.dimen_10_dp
import com.nudge.core.ui.events.theme.dimen_16_dp
import com.nudge.core.ui.events.theme.dimen_2_dp
import com.nudge.core.ui.events.theme.dimen_5_dp
import com.nudge.core.ui.events.theme.dimen_8_dp
import com.nudge.core.ui.events.theme.greenOnline
import com.nudge.core.ui.events.theme.greyBorderColor
import com.nudge.core.ui.events.theme.mediumTextStyle
import com.nudge.core.ui.events.theme.newMediumTextStyle
import com.nudge.core.ui.events.theme.smallTextStyleMediumWeight
import com.nudge.core.ui.events.theme.smallerTextStyleNormalWeight
import com.nudge.core.ui.events.theme.textColorDark80
import com.nudge.core.ui.events.theme.unmatchedOrangeColor
import com.nudge.core.ui.events.theme.white
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.components.CircularImageViewComponent
import com.sarathi.missionactivitytask.ui.components.PrimaryButton
import com.sarathi.missionactivitytask.utils.StatusEnum

@Composable
fun GrantTaskCard(
    title: String,
    subTitle: String,
    subtitle2: String,
    subtitle3: String,
    subtitle4: String,
    subtitle5: String,
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    secondaryButtonText: String,
    status: String,
    imagePath: String = BLANK_STRING,
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
            if (imagePath.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularImageViewComponent(modifier = Modifier)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        androidx.compose.material3.Text(
                            text = title,
                            style = mediumTextStyle,
                            color = brownDark
                        )
                        androidx.compose.material3.Text(
                            text = "subtitle",
                            style = smallTextStyleMediumWeight,
                            color = textColorDark80
                        )
                    }
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
            }

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
                if (imagePath.isBlank()) {
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
                GrantAmountView(subtitle2, subtitle3, iconResId = R.drawable.ic_recieve_grant)
                GrantAmountView(subtitle4, subtitle5, iconResId = R.drawable.ic_grant_sanction)
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
                            text = primaryButtonText,
                            onClick = { onPrimaryButtonClick() },
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
                                onClick = { onPrimaryButtonClick() },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun GrantAmountView(
    subtitle1: String,
    subtitle2: String,
    iconResId: Int = R.drawable.ic_recieve_grant
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!TextUtils.isEmpty(subtitle1)) {
            Text(
                text = subtitle1,
                color = blueDark,
                style = smallerTextStyleNormalWeight,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(2.dp)
                    .drawBehind {
                        drawCircle(
                            color = greyBorderColor,
                            radius = this.size.maxDimension / 1.5f
                        )
                    },
            )
            Text(
                text = "Didis",
                color = blueDark,
                style = newMediumTextStyle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(dimen_5_dp)
            )
            Spacer(modifier = Modifier.weight(.3f))
        }

        if (!TextUtils.isEmpty(subtitle2)) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = "",
                tint = greenOnline
            )
            Text(
                text = "${stringResource(R.string.received)}$subtitle2",
                modifier = Modifier
                    .weight(.4f)
                    .padding(horizontal = dimen_5_dp),
                color = blueDark,
                style = newMediumTextStyle
            )
        }
    }
}
