package com.patsurvey.nudge.activities.sync.home.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.nrlm.baselinesurvey.ui.theme.dimen_0_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_24_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_2_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_40_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_5_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.mediumTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyleNormalWeight
import com.nudge.core.ui.theme.grayColor
import com.nudge.core.ui.theme.green
import com.nudge.core.ui.theme.smallTextStyleWithUnderline
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.greenDark
import com.patsurvey.nudge.activities.ui.theme.newMediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.syncItemCountStyle
import com.patsurvey.nudge.activities.ui.theme.syncProgressBg
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.roundOffDecimalFloat

@Composable
fun EventTypeCard(
    title: String,
    syncButtonTitle:String,
    progress: Float? = 0f,
    producerProgress: Float? = 0f,
    isProgressBarVisible: Boolean,
    isStatusVisible: Boolean,
    isImageSyncCard: Boolean,
    onCardClick: () -> Unit,
    onSyncButtonClick: () -> Unit,
    onViewProcessClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen_10_dp)
            .clickable { onCardClick() },
        elevation = dimen_10_dp
    ) {
        ConstraintLayout(
            modifier = Modifier
                .background(Color.White)
                .padding(dimen_8_dp)
                .border(
                    width = dimen_0_dp,
                    color = Color.Transparent,
                )
        ) {
            val (titleText, circularProgressBar, progressBar, countText,
                syncButton, descRow, statusText, producerProgressBar, producerProgressText,
                dataSendText, dataProcessedText) = createRefs()
            Text(
                text = title,
                style = mediumTextStyle,
                color = blueDark,
                modifier = Modifier.constrainAs(titleText) {
                 start.linkTo(parent.start)
                 top.linkTo(parent.top)
                    bottom.linkTo(circularProgressBar.bottom)
                }
            )
            if (isProgressBarVisible) {
                Box(
                    modifier = Modifier
                        .height(dimen_40_dp)
                        .padding(dimen_5_dp)
                        .constrainAs(circularProgressBar) {
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                        }
                ) {
                    CircularProgressIndicator(
                        color = blueDark,
                        strokeWidth = dimen_2_dp,
                        modifier = Modifier
                            .size(dimen_18_dp)
                            .align(Alignment.Center)
                    )
                }
            }

            if (isStatusVisible) {
                Text(
                    text = stringResource(id = R.string.in_progress),
                    style = smallerTextStyleNormalWeight,
                    color = grayColor,
                    modifier = Modifier.constrainAs(statusText) {
                        end.linkTo(if (isProgressBarVisible) circularProgressBar.start else parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(circularProgressBar.bottom)
                    }
                )
            }

            Text(
                text = stringResource(
                    id = if (isImageSyncCard) R.string.images_send_to_server
                    else R.string.data_send_to_server
                ),
                style = smallerTextStyleNormalWeight,
                color = blueDark,
                modifier = Modifier
                    .padding(top = dimen_10_dp)
                    .constrainAs(dataSendText) {
                        top.linkTo(titleText.bottom)
                        start.linkTo(parent.start)
                    }
            )
            LinearProgressIndicator(
                progress = animateFloatAsState(
                    targetValue = producerProgress ?: 0f,
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                    label = BLANK_STRING
                ).value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimen_2_dp)
                    .height(dimen_24_dp)
                    .constrainAs(producerProgressBar) {
                        top.linkTo(dataSendText.bottom)
                        start.linkTo(parent.start)
                    },
                backgroundColor = syncProgressBg,
                color = green,
            )

            Text(
                text = "${roundOffDecimalFloat((producerProgress ?: 0f) * 100)}%",
                style = syncItemCountStyle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimen_5_dp)
                    .constrainAs(producerProgressText) {
                        top.linkTo(dataSendText.bottom)
                        end.linkTo(producerProgressBar.end)
                        start.linkTo(parent.start)
                    }
            )

            Text(
                text = stringResource(
                    id = if (isImageSyncCard) R.string.images_processes_by_server
                    else R.string.data_processes_by_server
                ),
                style = smallerTextStyleNormalWeight,
                color = blueDark,
                modifier = Modifier
                    .padding(top = dimen_10_dp)
                    .constrainAs(dataProcessedText) {
                        top.linkTo(producerProgressBar.bottom)
                        start.linkTo(parent.start)
                    }
            )
            LinearProgressIndicator(
                progress = animateFloatAsState(
                    targetValue = progress ?: 0f,
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = BLANK_STRING
                ).value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimen_2_dp)
                    .height(dimen_24_dp)
                    .constrainAs(progressBar) {
                        top.linkTo(dataProcessedText.bottom)
                        start.linkTo(parent.start)
                    },
                backgroundColor = syncProgressBg,
                color = greenDark,
            )

            Text(
                text = "${roundOffDecimalFloat((progress ?: 0f) * 100)}%",
                style = syncItemCountStyle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimen_5_dp)
                    .constrainAs(countText) {
                        top.linkTo(dataProcessedText.bottom)
                        end.linkTo(progressBar.end)
                        start.linkTo(parent.start)
                    }
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(dimen_8_dp)
                .constrainAs(descRow) {
                    top.linkTo(progressBar.bottom)
                    start.linkTo(parent.start)
                }) {

                producerProgress?.let { producerPer ->
                    if (producerPer > 0) {
                        Text(
                            text = "View Process",
                            style = smallTextStyleWithUnderline,
                            color = blueDark,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .weight(1F)
                                .clickable {
                                    onViewProcessClick()
                                }
                        )
                    }
                }

                progress?.let { per ->
                    if (per >= 1) {
                        Text(
                            text = "Synced",
                            style = smallTextStyle,
                            color = greenDark,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1F)
                        )
                    }
                }


            }
            progress?.let { per ->

                if (syncButtonTitle.isNotEmpty() && per < 1) {
                    Button(
                        onClick = {
                            onSyncButtonClick()
                        },
                        colors = ButtonDefaults.buttonColors(blueDark),
                        modifier = Modifier
                            .padding(top = dimen_10_dp)
                            .constrainAs(syncButton) {
                                end.linkTo(parent.end)
                                top.linkTo(progressBar.bottom)
                            }
                    ) {
                        Text(
                            text = syncButtonTitle,
                            color = white,
                            modifier = Modifier,
                            style = newMediumTextStyle
                        )
                    }
                }
            }


        }
    }

}

@Preview(showBackground = true)
@Composable
fun CommonSyncScreenPreview() {
    EventTypeCard(
        title = "Sync Data",
        progress = 1f,
        producerProgress = .5f,
        isProgressBarVisible = false,
        onCardClick = {},
        syncButtonTitle = "Sync Data",
        isStatusVisible = true,
        onSyncButtonClick = {},
        onViewProcessClick = {},
        isImageSyncCard = true
    )
}