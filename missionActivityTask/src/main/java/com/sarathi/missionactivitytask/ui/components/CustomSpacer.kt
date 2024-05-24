package com.sarathi.missionactivitytask.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.sarathi.missionactivitytask.ui.theme.dimen_10_dp

@Composable
fun CustomVerticalSpacer(
    modifier: Modifier = Modifier,
    size: Dp = dimen_10_dp
) {
    Spacer(
        modifier = Modifier
            .height(dimen_10_dp)
            .fillMaxWidth()
            .then(modifier)
    )
}


@Composable
fun CustomHorizontalSpacerSpacer(
    modifier: Modifier = Modifier,
    size: Dp = dimen_10_dp,
) {
    Spacer(
        modifier = Modifier
            .width(dimen_10_dp)
            .then(modifier)
    )
}