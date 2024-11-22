package com.sarathi.surveymanager.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.descriptionBoxBackgroundLightBlue
import com.nudge.core.ui.theme.dimen_18_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.lightGray2
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.utils.DescriptionContentState

@Composable
fun ExpandableDescriptionContentComponent(
    questionDetailExpanded: (index: Int) -> Unit,
    index: Int,
    contents: List<ContentList?>? = emptyList(),
    subTitle: String = BLANK_STRING,
    imageClickListener: (imageTypeDescriptionContent: String) -> Unit = {},
    videoLinkClicked: (videoTypeDescriptionContent: String) -> Unit = {}
) {
    val questionDetailVisibilityState = remember { mutableStateOf(false) }

    val descriptionContentState = remember {
        mutableStateOf(
            DescriptionContentState(
                textTypeDescriptionContent = getContentData(contents, "text")?.contentValue
                    ?: BLANK_STRING,
                imageTypeDescriptionContent = getContentData(contents, "image")?.contentValue
                    ?: BLANK_STRING,
                videoTypeDescriptionContent = getContentData(contents, "video")?.contentValue
                    ?: BLANK_STRING,
                subTextTypeDescriptionContent = subTitle
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (questionDetailVisibilityState.value)
                    descriptionBoxBackgroundLightBlue
                else
                    white
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = {
                questionDetailVisibilityState.value = !questionDetailVisibilityState.value
                questionDetailExpanded(index)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.info_icon),
                    contentDescription = "Toggle question detail visibility",
                    modifier = Modifier.size(dimen_18_dp),
                    tint = blueDark
                )
            }

            AnimatedVisibility(visible = questionDetailVisibilityState.value) {
                Column {
                    Divider(
                        thickness = dimen_1_dp,
                        color = lightGray2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    DescriptionContentComponent(
                        buttonClickListener = {
                            questionDetailVisibilityState.value =
                                !questionDetailVisibilityState.value
                        },
                        imageClickListener = { imageTypeDescriptionContent ->
                            imageClickListener(imageTypeDescriptionContent)
                        },
                        videoLinkClicked = { videoTypeDescriptionContent ->
                            videoLinkClicked(videoTypeDescriptionContent)
                        },
                        descriptionContentState = descriptionContentState.value
                    )
                }
            }
        }
    }
}

fun getContentData(contents: List<ContentList?>?, type: String): ContentList? {
    return contents?.find { it?.contentType == type }
}

