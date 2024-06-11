package com.patsurvey.nudge.activities.sync.home.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_20_dp
import com.nrlm.baselinesurvey.ui.theme.mediumTextStyle
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.greenDark
import com.patsurvey.nudge.activities.ui.theme.syncItemCountStyle
import com.patsurvey.nudge.activities.ui.theme.syncProgressBg

@Composable
fun EventTypeCard(
    title: String,
    totalEventCount:Int,
    successEventCount:Int,
    isRefreshRequired:Boolean,
    onCardClick: () -> Unit,
    onRefreshClick:() -> Unit
) {
    val progState=successEventCount.toFloat()/totalEventCount
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
                .padding(8.dp)
                .border(
                    width = 0.dp,
                    color = Color.Transparent,
                )
        ) {
            val (titleText,refreshIcon,progressBar,countText) = createRefs()
            Text(
                text = title,
                style = mediumTextStyle,
                color = blueDark    ,
                modifier = Modifier.constrainAs(titleText) {
                 start.linkTo(parent.start)
                 top.linkTo(parent.top)
                    bottom.linkTo(refreshIcon.bottom)
                }
            )
            if(isRefreshRequired) {
                IconButton(onClick = {
                    onRefreshClick()
                }, modifier = Modifier.constrainAs(refreshIcon) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_sync_24),
                        tint = blueDark,
                        contentDescription = "Refresh Button"
                    )
                }
            }

            LinearProgressIndicator(
                progress = animateFloatAsState(
                    targetValue = progState,
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
                ).value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .height(dimen_20_dp)
                    .constrainAs(progressBar) {
                        top.linkTo(titleText.bottom)
                        start.linkTo(parent.start)
                    },
                backgroundColor = syncProgressBg,
                color = greenDark,
            )

            Text(
                text = "$successEventCount/$totalEventCount",
                style = syncItemCountStyle,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp).constrainAs(countText){
                        top.linkTo(progressBar.bottom)
                    }
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun CommonSyncScreenPreview() {
    EventTypeCard(
        title = "Sync Data",
        totalEventCount = 1100,
        successEventCount = 190,
        isRefreshRequired = true,
        onCardClick = {},
        onRefreshClick = {}
    )
}