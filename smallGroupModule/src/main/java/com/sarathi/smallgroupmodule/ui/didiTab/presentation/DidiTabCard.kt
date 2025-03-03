package com.sarathi.smallgroupmodule.ui.didiTab.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.nudge.core.NO_TOLA_TITLE
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.ImageCardWithBottomContent
import com.nudge.core.ui.commonUi.ImageProperties
import com.nudge.core.ui.theme.eventTextColor
import com.nudge.core.ui.theme.smallerTextStyle
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
import com.sarathi.smallgroupmodule.ui.theme.mediumTextStyle
import com.sarathi.smallgroupmodule.ui.theme.smallTextStyleMediumWeight
import com.sarathi.smallgroupmodule.ui.theme.textColorDark
import com.sarathi.smallgroupmodule.ui.theme.textColorDark80

@Composable
fun DidiTabCard(
    modifier: Modifier = Modifier,
    subjectEntity: SubjectEntity,
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

                    Row(modifier = Modifier.fillMaxWidth()) {
                        ButtonOutline(
                            modifier = Modifier
                                .weight(1.0f)
                                .fillMaxWidth(),
                            buttonTitle = "Verify SHG",
                            icon = null,
                            borderColor = eventTextColor
                        ) { }
                        Spacer(modifier = Modifier.width(dimen_10_dp))
                        ButtonOutline(
                            modifier = Modifier
                                .weight(1.0f)
                                .fillMaxWidth(),
                            buttonTitle = "Verify Aadhaar",
                            icon = null,
                            borderColor = eventTextColor
                        ) { }
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
fun VerifiedInfoCard(title: String, verificationStatus: String, verifiedDateTime: String) {

    Row {
        Text(
            title,
            style = smallerTextStyle.copy(fontWeight = FontWeight.Medium)
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                verificationStatus,
                style = smallerTextStyle.copy(fontWeight = FontWeight.Medium, color =),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End // Align text to the end
            )
            Text(
                verifiedDateTime,
                style = smallerTextStyle.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End // Align text to the end
            )

        }
    }
}

@Composable
@Preview
fun previewVerifiedInfoCard() {
    VerifiedInfoCard("SHG: shg_name", "SHG Verified", verifiedDateTime = "18 Mar 2025 2:28pm")
}
