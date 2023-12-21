package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.ui.description_component.presentation.DescriptionContentComponent
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.descriptionBoxBackgroundLightBlue
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.lightGray2
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyleNormalWeight
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.states.DescriptionContentState
import com.patsurvey.nudge.customviews.htmltext.HtmlText

@Composable
fun ExpandableDescriptionContentComponent(
    questionDetailExpanded: (index: Int) -> Unit,
    index: Int,
    question: QuestionEntity,
    imageClickListener: (imageTypeDescriptionContent: String) -> Unit,
    videoLinkClicked: (videoTypeDescriptionContent: String) -> Unit,
) {
    val questionDetailVisibilityState = remember {
        mutableStateOf(false)
    }

    //TODO Modify code to handle contentList.
    val descriptionContentState = remember {
        mutableStateOf(DescriptionContentState(textTypeDescriptionContent = question.questionSummary ?: BLANK_STRING))
    }

    Box(
        Modifier
            .fillMaxWidth()
            .background(
                if (questionDetailVisibilityState.value)
                    descriptionBoxBackgroundLightBlue
                else
                    white
            ), contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = {
                questionDetailVisibilityState.value = true
                questionDetailExpanded(index)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.info_icon),
                    contentDescription = "question info button",
                    Modifier.size(dimen_18_dp),
                    tint = blueDark
                )
            }
            AnimatedVisibility(visible = questionDetailVisibilityState.value) {

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