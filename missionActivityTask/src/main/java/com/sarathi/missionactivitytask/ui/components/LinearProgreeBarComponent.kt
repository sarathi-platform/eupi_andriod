package com.sarathi.missionactivitytask.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.nudge.core.formatProgressNumber
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.trackLinearColor


@Composable
fun LinearProgressBarComponent(
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    activeColor: Color = greenOnline,
    trackColor: Color = trackLinearColor
) {
    LinearProgressIndicator(
        progress = formatProgressNumber(progress),
        modifier = modifier
            .fillMaxWidth()
            .height(7.dp),
        color = activeColor,
        trackColor = trackColor,
        strokeCap = StrokeCap.Round,
    )
}