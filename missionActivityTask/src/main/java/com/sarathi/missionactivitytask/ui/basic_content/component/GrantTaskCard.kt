package com.sarathi.missionactivitytask.ui.basic_content.component

import android.net.Uri
import android.text.TextUtils
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
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.brownDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_12_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_30_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.greyBorderColor
import com.nudge.core.ui.theme.languageItemActiveBg
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.smallerTextStyleNormalWeight
import com.nudge.core.ui.theme.unmatchedOrangeColor
import com.nudge.core.ui.theme.white
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.components.CircularImageViewComponent
import com.sarathi.missionactivitytask.ui.components.PrimaryButton
import com.sarathi.missionactivitytask.ui.grantTask.model.GrantTaskCardModel
import com.sarathi.missionactivitytask.utils.StatusEnum

@Composable
fun GrantTaskCard(
    title: GrantTaskCardModel?,
    subTitle1: GrantTaskCardModel?,
    subtitle2: GrantTaskCardModel?,
    subtitle3: GrantTaskCardModel?,
    subtitle4: GrantTaskCardModel?,
    subtitle5: GrantTaskCardModel?,
    primaryButtonText: GrantTaskCardModel?,
    onPrimaryButtonClick: (subjectName: String) -> Unit,
    secondaryButtonText: GrantTaskCardModel?,
    status: GrantTaskCardModel?,
    imagePath: Uri?,
    modifier: Modifier = Modifier,
    isHamletIcon: Boolean = false,
    onNotAvailable: () -> Unit,
) {
    val taskMarkedNotAvailable = remember {
        mutableStateOf(status?.value == StatusEnum.NOT_AVAILABLE.name)
    }
    androidx.compose.material3.Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimen_30_dp
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(dimen_16_dp)
            .clip(RoundedCornerShape(dimen_6_dp))
            .border(
                width = dimen_1_dp,
                color = if (status?.value == StatusEnum.COMPLETED.name) greenOnline else greyBorderColor,
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
                    Text(
                        text = title?.value ?: BLANK_STRING,
                        style = mediumTextStyle,
                        color = brownDark
                    )
                    SubContainerView(subTitle1)
                }
                if (status?.value == (StatusEnum.COMPLETED.name)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check_circle),
                        contentDescription = null,
                        modifier = Modifier.size(dimen_20_dp),
                        tint = greenOnline,
                    )
                } else if (status?.value == StatusEnum.INPROGRESS.name) {
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
                if (subtitle2?.value?.isNotBlank() == true) {
                    Text(
                        text = subtitle2.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimen_5_dp),
                        color = blueDark,
                        style = newMediumTextStyle
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SubContainerView(subtitle3)
                SubContainerView(subtitle4)
                SubContainerView(subtitle5)
            }

//            GrantAmountView(subtitle3, subtitle4, iconResId = R.drawable.ic_recieve_grant)
//            GrantAmountView(subtitle2 = subtitle5, iconResId = R.drawable.ic_grant_sanction)

            if (status?.value == StatusEnum.NOT_STARTED.name) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimen_16_dp),
                    horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)
                ) {
                    PrimarySecondaryButtonView(
                        modifier = Modifier.weight(1.0f),
                        secondaryButtonText?.value ?: BLANK_STRING,
                        taskMarkedNotAvailable,
                        onNotAvailable,
                        primaryButtonText?.value ?: BLANK_STRING,
                        onPrimaryButtonClick,
                        title?.value ?: BLANK_STRING
                    )
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
                    if (status?.value == StatusEnum.COMPLETED.name) {
                        Row(modifier = Modifier.clickable {
                            onPrimaryButtonClick(title?.value ?: BLANK_STRING)
                        }) {
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
                        }

                    } else {
                        PrimarySecondaryButtonView(
                            modifier = Modifier.weight(1.0f),
                            secondaryButtonText?.value ?: BLANK_STRING,
                            taskMarkedNotAvailable,
                            onNotAvailable,
                            primaryButtonText = stringResource(R.string.continue_text),
                            onPrimaryButtonClick,
                            title?.value ?: BLANK_STRING
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubContainerView(taskCard: GrantTaskCardModel?) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (taskCard?.icon != null) {
            ImageViewer(taskCard.icon)
            Spacer(modifier = Modifier.width(6.dp))

        }
        if (!TextUtils.isEmpty(taskCard?.label)) {
            Text(text = taskCard?.label!!, color = blueDark, style = newMediumTextStyle)
        }
        if (!TextUtils.isEmpty(taskCard?.value)) {

            Text(
                text = taskCard?.value!!,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_5_dp),
                color = blueDark,
                style = newMediumTextStyle
            )
        }
    }
}

@Composable
fun ImageViewer(uri: Uri) {


    AsyncImage(
        model = uri,
        contentDescription = "Loaded Image",
        modifier = Modifier.size(22.dp)
    )

}

@Composable
private fun PrimarySecondaryButtonView(
    modifier: Modifier = Modifier,
    secondaryButtonText: String,
    taskMarkedNotAvailable: MutableState<Boolean>,
    onNotAvailable: () -> Unit,
    primaryButtonText: String,
    onPrimaryButtonClick: (subjectName: String) -> Unit,
    title: String
) {
    if (secondaryButtonText.isNotBlank()) {
        PrimaryButton(
            text = secondaryButtonText,
            isIcon = false,
            onClick = {
                taskMarkedNotAvailable.value = true
                onNotAvailable()
            },
            color = if (taskMarkedNotAvailable.value) ButtonDefaults.buttonColors(
                containerColor = blueDark,
                contentColor = white
            ) else ButtonDefaults.buttonColors(
                containerColor = languageItemActiveBg,
                contentColor = blueDark
            ),
            modifier = Modifier
        )
    } else {
        Spacer(modifier = modifier)
    }
    if (primaryButtonText.isNotBlank()) {
        PrimaryButton(
            text = primaryButtonText,
            color = if (taskMarkedNotAvailable.value) ButtonDefaults.buttonColors(
                containerColor = languageItemActiveBg,
                contentColor = blueDark
            ) else ButtonDefaults.buttonColors(
                containerColor = blueDark,
                contentColor = white
            ),
            onClick = {
                taskMarkedNotAvailable.value = false
                onPrimaryButtonClick(title)
            },
            modifier = modifier
        )
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
                text = subtitle2,
                modifier = Modifier
                    .weight(.4f)
                    .padding(horizontal = dimen_5_dp),
                color = blueDark,
                style = newMediumTextStyle
            )
        }
    }
}
