package com.patsurvey.nudge.activities.sync.home.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.nrlm.baselinesurvey.ui.theme.dimen_0_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_17_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_20_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_40_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_5_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.mediumTextStyle
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
    totalEventCount:Int,
    successEventCount:Int,
    syncButtonTitle:String,
    isRefreshRequired:Boolean,
    onCardClick: () -> Unit,
    onSyncButtonClick:() -> Unit
) {
    var progState=successEventCount.toFloat()/totalEventCount
    if(totalEventCount == 0 && successEventCount ==0){
        progState = 0f
    }
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
            val (titleText,circularProgressBar,progressBar,countText,syncButton) = createRefs()
            Text(
                text = title,
                style = mediumTextStyle,
                color = blueDark    ,
                modifier = Modifier.constrainAs(titleText) {
                 start.linkTo(parent.start)
                 top.linkTo(parent.top)
                    bottom.linkTo(circularProgressBar.bottom)
                }
            )
            if(isRefreshRequired) {
                Box(
                    modifier = Modifier
                        .height(dimen_40_dp)
                        .padding(dimen_5_dp)
                        .constrainAs(circularProgressBar){
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    }
                ) {
                    CircularProgressIndicator(
                        color = blueDark,
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            LinearProgressIndicator(
                progress = animateFloatAsState(
                    targetValue = progState,
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = BLANK_STRING
                ).value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimen_20_dp)
                    .height(dimen_20_dp)
                    .constrainAs(progressBar) {
                        top.linkTo(titleText.bottom)
                        start.linkTo(parent.start)
                    },
                backgroundColor = syncProgressBg,
                color = greenDark,
            )

            Text(
                text = "${roundOffDecimalFloat(progState*100)}%",
                style = syncItemCountStyle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top =dimen_17_dp).constrainAs(countText){
                        top.linkTo(titleText.bottom)
                        end.linkTo(progressBar.end)
                        start.linkTo(parent.start)
                    }
            )
            if(syncButtonTitle.isNotEmpty() && totalEventCount >0) {

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

@Preview(showBackground = true)
@Composable
fun CommonSyncScreenPreview() {
    EventTypeCard(
        title = "Sync Data",
        totalEventCount = 5,
        successEventCount = 0,
        isRefreshRequired = true,
        onCardClick = {},
        syncButtonTitle = "Sync Data",
        onSyncButtonClick = {}
    )
}