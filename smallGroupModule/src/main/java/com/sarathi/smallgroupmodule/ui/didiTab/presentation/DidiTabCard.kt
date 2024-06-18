package com.sarathi.smallgroupmodule.ui.didiTab.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.nudge.core.NO_TOLA_TITLE
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.data.entities.getSubtitle
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.components.CustomVerticalSpacer
import com.sarathi.missionactivitytask.ui.components.IconProperties
import com.sarathi.missionactivitytask.ui.components.ImageCardWithBottomContent
import com.sarathi.missionactivitytask.ui.components.ImageProperties
import com.sarathi.missionactivitytask.ui.components.TextProperties
import com.sarathi.missionactivitytask.ui.components.TextWithIconComponent
import com.sarathi.smallgroupmodule.ui.theme.blueDark
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
                contentDescription = "Didi Image",
                modifier = Modifier
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .width(dimen_45_dp)
                    .height(dimen_45_dp)
            ), bottomContent = {
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

            }
        }
        CustomVerticalSpacer()
    }

}