package com.sarathi.missionactivitytask.ui.components

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.sarathi.missionactivitytask.ui.theme.blueDark
import com.sarathi.missionactivitytask.ui.theme.trackLinearColor


@Composable
fun LinearProgressBar(
    progress: Float = 0f,
    modifier: Modifier = Modifier,
    activeColor: Color = blueDark,
    trackColor: Color = trackLinearColor
) {
    LinearProgressIndicator(
        progress = progress,
        modifier = modifier,
        color = activeColor,
        trackColor = trackColor,
        strokeCap = StrokeCap.Round,
    )
}