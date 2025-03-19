package com.sarathi.smallgroupmodule.ui.didiTab.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.nudge.core.BLANK_STRING
import com.nudge.core.DD_MMM_YYYY_H_MMA
import com.nudge.core.NO_TOLA_TITLE
import com.nudge.core.SHG_VERIFICATION_STATUS_NOT_VERIFIED
import com.nudge.core.SHG_VERIFICATION_STATUS_VERIFIED
import com.nudge.core.SHG_VERIFICATION_STATUS_VERIFIED_ID_NOT_FOUND
import com.nudge.core.getDate
import com.nudge.core.helper.LocalTranslationHelper
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.ImageCardWithBottomContent
import com.nudge.core.ui.commonUi.ImageProperties
import com.nudge.core.ui.theme.dimen_40_dp
import com.nudge.core.ui.theme.dimen_4_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.eventTextColor
import com.nudge.core.ui.theme.smallTextStyleMediumWeight2
import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.data.entities.getSubtitle
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.components.IconProperties
import com.sarathi.missionactivitytask.ui.components.TextProperties
import com.sarathi.missionactivitytask.ui.components.TextWithIconComponent
import com.sarathi.smallgroupmodule.ui.commonUi.ButtonOutline
import com.sarathi.smallgroupmodule.ui.theme.blueDark
import com.sarathi.smallgroupmodule.ui.theme.dimen_10_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_45_dp
import com.sarathi.smallgroupmodule.ui.theme.gary_light
import com.sarathi.smallgroupmodule.ui.theme.mediumTextStyle
import com.sarathi.smallgroupmodule.ui.theme.orangeLight
import com.sarathi.smallgroupmodule.ui.theme.smallTextStyleMediumWeight
import com.sarathi.smallgroupmodule.ui.theme.textColorDark
import com.sarathi.smallgroupmodule.ui.theme.textColorDark80
import com.sarathi.smallgroupmodule.ui.theme.verifiedBgColor
import com.sarathi.smallgroupmodule.ui.theme.verifiedTextColor

@Composable
fun DidiTabCard(
    modifier: Modifier = Modifier,
    subjectEntity: SubjectEntity,
    onShgVerifyClick: (subjectEntity: SubjectEntity) -> Unit,
    onClick: () -> Unit
) {

    Column {
        ImageCardWithBottomContent(
            modifier = Modifier
                .then(modifier)
                .clickable {
                    onClick()
                },
            imageProperties = ImageProperties(
                path = subjectEntity.crpImageLocalPath,
                altText = subjectEntity.subjectName,
                contentDescription = "Didi Image",
                modifier = Modifier
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .width(dimen_45_dp)
                    .height(dimen_45_dp)
            ), bottomContent = {
                Column {
                    TextWithIconComponent(
                        iconProperties = IconProperties(
                            icon = painterResource(id = R.drawable.home_icn),
                            "home icon"
                        ),
                        textProperties = TextProperties(
                            text = if (!subjectEntity.cohortName.equals(
                                    NO_TOLA_TITLE,
                                    true
                                )
                            ) subjectEntity.cohortName else subjectEntity.villageName,
                            style = smallTextStyleMediumWeight,
                            color = textColorDark
                        )
                    )

                    if (!subjectEntity.shgVerificationStatus.equals(
                            SHG_VERIFICATION_STATUS_NOT_VERIFIED
                        )
                    ) {
                        val status = subjectEntity.shgVerificationStatus.value(
                            SHG_VERIFICATION_STATUS_NOT_VERIFIED
                        )
                        VerifiedInfoCard(
                            title = "SHG: " + subjectEntity.shgName.value(),
                            statusAndDateColor = getStatusAndDateColor(status),
                            verificationStatus = getShgStatusText(status),
                            verifiedDateTime = subjectEntity.shgVerificationDate.getDate(pattern = DD_MMM_YYYY_H_MMA)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        ButtonOutline(
                            modifier = Modifier
                                .weight(1.0f)
                                .fillMaxWidth()
                                .height(dimen_40_dp),
                            buttonTitle = "Verify SHG",
                            icon = null,
                            borderColor = eventTextColor
                        ) {
                            onShgVerifyClick(subjectEntity)
                        }
                        Spacer(modifier = Modifier.width(dimen_10_dp))
                        ButtonOutline(
                            modifier = Modifier
                                .weight(1.0f)
                                .fillMaxWidth()
                                .height(dimen_40_dp),
                            buttonTitle = "Verify Aadhaar",
                            icon = null,
                            borderColor = eventTextColor
                        ) {

                        }
                    }
                }

            }
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = subjectEntity.subjectName,
                    style = mediumTextStyle,
                    color = blueDark
                )
                Text(
                    text = subjectEntity.getSubtitle(),
                    style = smallTextStyleMediumWeight,
                    color = textColorDark80
                )

                CustomVerticalSpacer()


            }
        }
        CustomVerticalSpacer()

    }

}

@Composable
fun VerifiedInfoCard(
    title: String,
    statusAndDateColor: Pair<Color, Color>,
    verificationStatus: String,
    verifiedDateTime: String
) {

    Row(
        modifier = Modifier
            .background(color = verifiedBgColor)
            .padding(horizontal = dimen_4_dp, vertical = dimen_8_dp)
    ) {
        Text(
            title,
            style = smallTextStyleMediumWeight2
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                verificationStatus,
                style = smallTextStyleMediumWeight2.copy(
                    color = statusAndDateColor.first
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End // Align text to the end
            )
            Text(
                verifiedDateTime,
                style = smallTextStyleMediumWeight2.copy(
                    color = statusAndDateColor.second
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End // Align text to the end
            )

        }
    }
}

fun getStatusAndDateColor(status: String): Pair<Color, Color> {
    return when (status) {
        SHG_VERIFICATION_STATUS_VERIFIED -> Pair(verifiedTextColor, verifiedTextColor)
        SHG_VERIFICATION_STATUS_VERIFIED_ID_NOT_FOUND -> Pair(orangeLight, gary_light)
        else -> Pair(textColorDark, textColorDark)
    }
}

@Composable
fun getShgStatusText(status: String): String {
    val translationHelper = LocalTranslationHelper.current
    return when (status) {
        SHG_VERIFICATION_STATUS_VERIFIED -> "SHG Verified"
        SHG_VERIFICATION_STATUS_VERIFIED_ID_NOT_FOUND -> "ID Not Found"
        else -> BLANK_STRING
    }
}

@Composable
@Preview
fun previewVerifiedInfoCard() {
    VerifiedInfoCard(
        "SHG: shg_name",
        statusAndDateColor = Pair(verifiedTextColor, verifiedTextColor),
        "SHG Verified",
        verifiedDateTime = "18 Mar 2025 2:28pm"
    )
}
