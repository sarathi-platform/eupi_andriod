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
import androidx.compose.foundation.layout.absolutePadding
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
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.buttonTextStyle
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_20_dp
import com.nudge.core.ui.theme.dimen_22_dp
import com.nudge.core.ui.theme.dimen_3_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.greyBorderColor
import com.nudge.core.ui.theme.greyColor
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.unmatchedOrangeColor
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardModel
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.components.CircularImageViewComponent
import com.sarathi.missionactivitytask.utils.StatusEnum

@Composable
fun LivelihoodTaskCard(
    title: TaskCardModel?,
    subTitle1: TaskCardModel?,
    subtitle2: TaskCardModel?,
    subtitle3: TaskCardModel?,
    subtitle4: TaskCardModel?,
    subtitle5: TaskCardModel?,
    subtitle6: TaskCardModel?,
    subtitle7: TaskCardModel?,
    primaryButtonText: TaskCardModel?,
    onPrimaryButtonClick: (subjectName: String) -> Unit,
    secondaryButtonText: TaskCardModel?,
    status: TaskCardModel?,
    imagePath: Uri?,
    modifier: Modifier = Modifier,
    isActivityCompleted: Boolean,
    isHamletIcon: Boolean = false,
    isNotAvailableButtonEnable: Boolean = false,
    isShowSecondaryStatusIcon: Boolean = false,
    secondaryStatusIcon: Int = R.drawable.ic_green_file,
    onNotAvailable: () -> Unit,
) {
    val taskMarkedNotAvailable = remember(status?.value) {
        mutableStateOf(status?.value == StatusEnum.NOT_AVAILABLE.name)
    }
    val taskStatus = remember(status?.value) {
        mutableStateOf(status?.value)
    }
    BasicCardView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen_16_dp)
            .border(
                width = dimen_1_dp,
                color = if (taskStatus?.value == StatusEnum.COMPLETED.name) greenOnline else greyBorderColor,
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
                    .padding(horizontal = dimen_16_dp, vertical = dimen_5_dp),
                horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (imagePath != null) {
                    CircularImageViewComponent(modifier = Modifier, imagePath = imagePath) {

                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        title?.value?.let {
                            if (it.contains("vo", true)) {
                                title.icon?.let {
                                    ImageViewer(it)
                                    Spacer(modifier = Modifier.width(dimen_5_dp))
                                }
                                Spacer(modifier = Modifier.width(dimen_3_dp))
                            }
                            Text(
                                text = title.value,
                                style = buttonTextStyle,
                                color = blueDark
                            )
                        }
                    }
                    SubContainerView(subTitle1)
                }
                if (taskStatus?.value == (StatusEnum.COMPLETED.name)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check_circle),
                        contentDescription = null,
                        modifier = Modifier.size(dimen_20_dp),
                        tint = greenOnline,
                    )
                } else if (taskStatus?.value == StatusEnum.NOT_AVAILABLE.name) {
                    Text(
                        text = stringResource(id = R.string.not_available),
                        style = defaultTextStyle,
                        modifier = Modifier
                            .padding(horizontal = dimen_5_dp),
                        color = greyColor
                    )
                } else if (taskStatus.value == StatusEnum.INPROGRESS.name) {
                    if (isShowSecondaryStatusIcon) {
                        Icon(
                            painter = painterResource(id = secondaryStatusIcon),
                            contentDescription = "Green Icon",
                            tint = greenOnline,
                            modifier = Modifier.size(dimen_22_dp)
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.in_progress),
                            style = defaultTextStyle,
                            modifier = Modifier
                                .padding(horizontal = dimen_5_dp),
                            color = unmatchedOrangeColor
                        )
                    }

                    }


                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = dimen_16_dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (subtitle2?.value?.isNotBlank() == true) {
                    SubContainerView(subtitle2)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SubContainerView(subtitle6, isNumberFormattingRequired = false)
                SubContainerView(subtitle3)
                SubContainerView(subtitle4, isNumberFormattingRequired = true)
                SubContainerView(subtitle5)
                SubContainerView(subtitle7)

            }

            if (taskStatus?.value == StatusEnum.NOT_STARTED.name) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimen_5_dp)
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
                        .padding(
                            start = dimen_16_dp,
                            end = dimen_16_dp,
                            bottom = dimen_8_dp,
                            top = dimen_8_dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)
                ) {

                    PrimarySecondaryButtonView(
                        modifier = Modifier.weight(1.0f),
                        secondaryButtonText?.value ?: BLANK_STRING,
                        taskMarkedNotAvailable,
                        onNotAvailable,
                        primaryButtonText?.value ?: BLANK_STRING,
                        onPrimaryButtonClick,
                        title?.value ?: BLANK_STRING,
                        isActivityCompleted,
                        taskStatus,
                        isNotAvailableButtonEnable
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimen_5_dp)
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
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (taskStatus?.value == StatusEnum.COMPLETED.name) {
                        Row(modifier = Modifier.clickable {
                            onPrimaryButtonClick(title?.value ?: BLANK_STRING)
                        }) {

                            Text(
                                text = if (isActivityCompleted) stringResource(R.string.task_view) else stringResource(R.string.edit),
                                modifier = Modifier
                                    .padding(horizontal = dimen_5_dp)
                                    .absolutePadding(bottom = 3.dp),
                                color = blueDark,
                                style = newMediumTextStyle,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "",
                                tint = blueDark,
                            )
                        }


                    }
                    else {
                        PrimarySecondaryButtonView(
                            modifier = Modifier.weight(1.0f),
                            secondaryButtonText?.value ?: BLANK_STRING,
                            taskMarkedNotAvailable,
                            onNotAvailable,
                            primaryButtonText = stringResource(R.string.continue_text),
                            onPrimaryButtonClick,
                            title?.value ?: BLANK_STRING,
                            isActivityCompleted,
                            taskStatus,
                            isNotAvailableButtonEnable
                        )
                    }
                }
            }
        }
    }


