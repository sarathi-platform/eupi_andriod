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
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
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
import com.patsurvey.nudge.customviews.htmltext.HtmlText

@Composable
fun InfoComponent(
    questionDetailExpanded: (index: Int) -> Unit,
    index: Int,
    question: QuestionEntity
) {
    val questionDetailVisibilityState = remember {
        mutableStateOf(false)
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

                Column(
                    modifier = Modifier.padding(horizontal = dimen_16_dp),
                    verticalArrangement = Arrangement.spacedBy(dimen_10_dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimen_10_dp)
                    )
                    HtmlText(
                        text = question.questionSummary ?: BLANK_STRING,
                        style = smallerTextStyleNormalWeight,
                        color = blueDark,
                        modifier = Modifier.padding(dimen_16_dp)
                    )
                    Button(
                        onClick = {
                            questionDetailVisibilityState.value =
                                !questionDetailVisibilityState.value
                        }, shape = RoundedCornerShape(
                            roundedCornerRadiusDefault
                        ), colors = ButtonDefaults.buttonColors(
                            backgroundColor = blueDark,
                            contentColor = white
                        )
                    ) {
                        Text(
                            text = "Ok",
                            color = white,
                            style = smallerTextStyle
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimen_10_dp)
                    )

                }
            }
        }
    }
}