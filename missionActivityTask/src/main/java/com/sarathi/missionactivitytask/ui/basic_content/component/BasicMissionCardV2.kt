package com.sarathi.missionactivitytask.ui.basic_content.component

import android.net.Uri
import android.text.TextUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.rememberImagePainter
import com.nudge.core.BLANK_STRING
import com.nudge.core.calculateProgress
import com.nudge.core.model.FilterUiModel
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_12_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_27_dp
import com.nudge.core.ui.theme.dimen_35_dp
import com.nudge.core.ui.theme.dimen_4_dp
import com.nudge.core.ui.theme.dimen_50_dp
import com.nudge.core.ui.theme.dimen_56_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.iconBgColor
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.smallerTextStyleMediumWeight
import com.nudge.core.ui.theme.textColorBrown
import com.nudge.core.utils.FileUtils
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.components.LinearProgressBarComponent
import com.sarathi.missionactivitytask.ui.components.PrimaryButtonChip
import com.sarathi.missionactivitytask.utils.StatusEnum
import com.sarathi.missionactivitytask.utils.statusColor

@Composable
fun BasicMissionCardV2(
    title: String = BLANK_STRING,
    needToShowProgressBar: Boolean = false,
    status: String = StatusEnum.PENDING.name,
    pendingCount: Int = 0,
    totalCount: Int = 0,
    filterUiModel: FilterUiModel?,
    primaryButtonText: String = BLANK_STRING,
    prefixIcon: Int = R.drawable.ic_group_icon,
    livelihoodType: String? = BLANK_STRING,
    livelihoodOrder: Int? = 0,
    onPrimaryClick: () -> Unit
) {
    val context = LocalContext.current

    BasicCardView(
        modifier = Modifier
            .clickable {
                onPrimaryClick()
            }
            .fillMaxWidth()
            .padding(horizontal = dimen_16_dp)
            .border(
                width = dimen_1_dp,
                color = statusColor(status),
                shape = RoundedCornerShape(dimen_6_dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimen_16_dp,
                    end = dimen_16_dp,
                    top = dimen_12_dp,
                    bottom = dimen_12_dp
                ),
            horizontalArrangement = Arrangement.spacedBy(dimen_8_dp)
        ) {
            Box(
                modifier = Modifier
                    .size(dimen_56_dp)
                    .background(
                        color = iconBgColor,
                        shape = RoundedCornerShape(roundedCornerRadiusDefault)
                    ),
                contentAlignment = Alignment.Center

            ) {
                Icon(
                    painter = painterResource(id = prefixIcon),
                    tint = blueDark,
                    contentDescription = null,  // Ideally, provide meaningful descriptions
                    modifier = Modifier.size(dimen_27_dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = dimen_10_dp)
            ) {
                Text(
                    text = title,
                    style = mediumTextStyle
                        .copy(color = blueDark),
                    modifier = Modifier
                        .padding(start = dimen_5_dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimen_4_dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressBarComponent(
                        progress = calculateProgress(
                            pendingCount = pendingCount,
                            totalCount = totalCount
                        ),
                        modifier = Modifier
                            .weight(1f)
                    )
                    Text(
                        text = "$pendingCount / $totalCount",
                        style = smallTextStyle.copy(color = blueDark),
                    )
                }
            }

            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(dimen_4_dp)
            ) {
                if (filterUiModel != null && livelihoodOrder != 0 && !livelihoodType.isNullOrEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(Modifier.size(dimen_35_dp)) {
                            val imageUri =
                                if (TextUtils.isEmpty(filterUiModel.imageFileName)) Uri.EMPTY else filterUiModel.imageFileName?.let {
                                    FileUtils.getImageUri(
                                        context = context,
                                        fileName = it
                                    )
                                }
                            Image(
                                painter = rememberImagePainter(
                                    imageUri
                                ),
                                contentDescription = "filter icon",
                                contentScale = ContentScale.Inside,
                                modifier = Modifier
                                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                    .width(dimen_35_dp)
                                    .height(dimen_35_dp)
                            )
                        }

                        Text(
                            filterUiModel.filterValue,
                            style = smallerTextStyleMediumWeight.copy(color = textColorBrown),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Spacer(
                        modifier = Modifier
                            .size(dimen_50_dp)
                    )
                }
                PrimaryButtonChip(
                    onClick = { onPrimaryClick() },
                    modifier = Modifier
                )
            }
        }
    }
}
