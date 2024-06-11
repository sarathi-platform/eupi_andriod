package com.sarathi.missionactivitytask.ui.basic_content.component

import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.events.theme.blueDark
import com.nudge.core.ui.events.theme.brownDark
import com.nudge.core.ui.events.theme.defaultTextStyle
import com.nudge.core.ui.events.theme.dimen_10_dp
import com.nudge.core.ui.events.theme.dimen_12_dp
import com.nudge.core.ui.events.theme.dimen_16_dp
import com.nudge.core.ui.events.theme.dimen_1_dp
import com.nudge.core.ui.events.theme.dimen_20_dp
import com.nudge.core.ui.events.theme.dimen_30_dp
import com.nudge.core.ui.events.theme.dimen_5_dp
import com.nudge.core.ui.events.theme.dimen_6_dp
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
    subTitle1: String,
    subtitle2: String,
    subtitle3: String,
    subtitle4: String,
    subtitle5: String,
    primaryButtonText: String,
    onPrimaryButtonClick: (subjectName: String) -> Unit,
    secondaryButtonText: String,
    status: String,
    imagePath: Uri?,
    modifier: Modifier = Modifier,
    isHamletIcon: Boolean = false,
) {
    androidx.compose.material3.Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimen_30_dp
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(dimen_16_dp)
            .clip(RoundedCornerShape(dimen_6_dp))
            .border(
                width = dimen_1_dp,
                color = if (status == StatusEnum.COMPLETED.name) greenOnline else greyBorderColor,
                shape = RoundedCornerShape(dimen_6_dp)
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
                    .padding(dimen_16_dp),
                horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (imagePath != null) {
                    CircularImageViewComponent(modifier = Modifier, imagePath = imagePath)
                }
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
                        text = subTitle1,
                        style = smallTextStyleMediumWeight,
                        color = textColorDark80
                    )
                }
                if (status == (StatusEnum.COMPLETED.name)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check_circle),
                        contentDescription = null,
                        modifier = Modifier.size(dimen_20_dp),
                        tint = greenOnline,
                    )
                } else if (status == StatusEnum.INPROGRESS.name) {
                    Text(
                        text = stringResource(id = R.string.in_progress),
                        style = defaultTextStyle,
                        modifier = Modifier
                            .padding(horizontal = dimen_5_dp),
                        color = unmatchedOrangeColor
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = dimen_16_dp, top = dimen_5_dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (subtitle2.isNotBlank()) {
                    Icon(
                        painter = painterResource(id = R.drawable.home_icn),
                        contentDescription = null,
                        tint = blueDark,
                    )
                    Text(
                        text = subtitle2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimen_5_dp),
                        color = blueDark,
                        style = newMediumTextStyle
                    )
                }
            }
            GrantAmountView(subtitle3, subtitle4, iconResId = R.drawable.ic_recieve_grant)
            GrantAmountView(subtitle2 = subtitle5, iconResId = R.drawable.ic_grant_sanction)
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
                            onClick = { onPrimaryButtonClick(title) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimen_5_dp, bottom = dimen_5_dp)
                ) {
                    Divider(
                        modifier = Modifier
                            .height(dimen_1_dp)
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
                            text = stringResource(R.string.view),
                            modifier = Modifier
                                .padding(horizontal = dimen_5_dp),
                            color = blueDark,
                            style = newMediumTextStyle
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "",
                            tint = blueDark,
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                        if (primaryButtonText.isNotBlank()) {
                            PrimaryButton(
                                text = stringResource(R.string.continue_text),
                                onClick = { onPrimaryButtonClick(title) },
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
    subtitle1: String = BLANK_STRING,
    subtitle2: String = BLANK_STRING,
    iconResId: Int = R.drawable.ic_recieve_grant
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = dimen_16_dp, end = dimen_16_dp, top = dimen_12_dp),
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
            )
            Spacer(modifier = Modifier.weight(.3f))
        }

        if (!TextUtils.isEmpty(subtitle2)) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
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
