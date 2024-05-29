package com.nudge.syncmanager.ui.common_sync_ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nudge.syncmanager.ui.sync_event_screen.theme.blueDark
import com.nudge.syncmanager.ui.sync_event_screen.theme.dimen_10_dp
import com.nudge.syncmanager.ui.sync_event_screen.theme.dimen_16_dp
import com.nudge.syncmanager.ui.sync_event_screen.theme.dimen_20_dp
import com.nudge.syncmanager.ui.sync_event_screen.theme.dimen_5_dp
import com.nudge.syncmanager.ui.sync_event_screen.theme.languageItemActiveBg
import com.nudge.syncmanager.ui.sync_event_screen.theme.roundedCornerRadiusDefault
import com.nudge.syncmanager.R

@Composable
fun CommonSyncScreen (  title: String,
                        ProgBarState :Float,
                        onClick: () -> Unit){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen_16_dp)
            .clickable { },
        elevation = dimen_10_dp
    ) {
        Column(
            modifier = Modifier.padding(dimen_16_dp)
        ) {
            Spacer(modifier = Modifier.padding(dimen_5_dp))

            Row {
                Icon(
                    painter = painterResource(
                        id =  R.drawable.sync_icon
                    ),
                    contentDescription = stringResource( R.string.sync_Icon),
                    tint = Color.Black,
                    modifier = Modifier.height(dimen_20_dp)
                )
                Spacer(modifier = Modifier.weight(1f))

                Text(

                    text = title,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    ),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(
                        id =  R.drawable.right_arrow
                    ),
                    contentDescription = stringResource( R.string.arrow_Icon),
                    tint = Color.Black,
                    modifier = Modifier.height(dimen_20_dp)
                )
            }
            Spacer(modifier = Modifier.padding(dimen_16_dp))

            LinearProgressIndicator(
                progress = animateFloatAsState(
                    targetValue = ProgBarState,
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
                ).value,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimen_20_dp)
                    .clip(RoundedCornerShape(roundedCornerRadiusDefault)),
                backgroundColor = languageItemActiveBg,
                color = blueDark,
            )
        }
    }

}