package com.sarathi.missionactivitytask.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.nudge.core.ui.theme.dimen_10_dp

@Composable
fun CustomVerticalSpacer(
    modifier: Modifier = Modifier,
    size: Dp = dimen_10_dp
) {
    Spacer(
        modifier = Modifier
            .height(size)
            .fillMaxWidth()
            .then(modifier)
    )
}


@Composable
fun CustomHorizontalSpacer(
    modifier: Modifier = Modifier,
    size: Dp = dimen_10_dp,
) {
    Spacer(
        modifier = Modifier
            .width(size)
            .then(modifier)
    )
}