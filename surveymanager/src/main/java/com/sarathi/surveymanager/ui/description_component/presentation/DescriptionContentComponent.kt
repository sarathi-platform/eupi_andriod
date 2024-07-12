package com.sarathi.surveymanager.ui.description_component.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_18_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_24_dp
import com.nudge.core.ui.theme.largeTextStyle
import com.nudge.core.ui.theme.lightGray2
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.smallerTextStyle
import com.nudge.core.ui.theme.summaryCardViewBlue
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.download_manager.FileType
import com.sarathi.dataloadingmangement.ui.component.LinkTextButtonWithIcon
import com.sarathi.dataloadingmangement.ui.component.TextWithReadMoreComponent
import com.sarathi.surveymanager.R

@Composable
fun DescriptionContentComponent(
    modifier: Modifier = Modifier,
    contentList: List<Content>,
    onMediaContentClick: (contentKey: String) -> Unit,
    onCloseListener: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.then(modifier)
    ) {
        IconButton(onClick = {
            onCloseListener()
        }) {
            Icon(
                painter = painterResource(id = R.drawable.info_icon),
                contentDescription = "question info button",
                Modifier.size(dimen_18_dp),
                tint = blueDark
            )
        }
        Divider(
            thickness = dimen_1_dp,
            color = lightGray2,
            modifier = Modifier.fillMaxWidth()
        )
        Column {

            contentList.forEach { content ->
                when (content.contentType.toLowerCase()) {

                    FileType.TEXT.name.toLowerCase() -> {
                        TextWithReadMoreComponent(
                            contentData = content.contentValue,
                            textStyle = largeTextStyle
                        )
                    }

                    else -> {
                        LinkTextButtonWithIcon(
                            modifier = Modifier
                                .align(Alignment.Start),
                            title = content.contentKey,
                            textColor = summaryCardViewBlue,
                            iconTint = summaryCardViewBlue
                        ) {
                            onMediaContentClick(content.contentKey)
                        }
                    }
                }
                CustomVerticalSpacer()
            }

            CustomVerticalSpacer()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimen_16_dp, horizontal = dimen_24_dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        onCloseListener()
                    }, shape = RoundedCornerShape(
                        roundedCornerRadiusDefault
                    ), colors = ButtonDefaults.buttonColors(
                        backgroundColor = blueDark,
                        contentColor = white
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 25.dp),
                        text = /*stringResource(R.string.ok_text)*/"Ok",
                        color = white,
                        style = smallerTextStyle
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
