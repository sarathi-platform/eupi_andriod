package com.nudge.core.ui.commonUi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.nudge.core.R
import com.nudge.core.getTimeAgoDetailed
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.syncTimeSmallTextStyle

@Composable
fun LastSyncTimeView(
    lastSyncTime: Long,
    mobileNumber: String,
    isShowPhoneNumber: Boolean = true,
    onCancelWorker: () -> Unit
) {
    val context = LocalContext.current
    if (lastSyncTime != 0L) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_10_dp)
                    .clickable {
                        onCancelWorker()
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.last_sync_date_time),
                    style = syncTimeSmallTextStyle,
                    color = blueDark
                )

                Text(
                    text = getTimeAgoDetailed(lastSyncTime, context),
                    style = syncTimeSmallTextStyle,
                    color = blueDark
                )
            }
            if (isShowPhoneNumber) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimen_10_dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.profile_phone) + ":",
                        style = syncTimeSmallTextStyle,
                        color = blueDark
                    )

                    Text(
                        text = mobileNumber,
                        style = syncTimeSmallTextStyle,
                        color = blueDark
                    )
                }
            }
        }
    }
}
