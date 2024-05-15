package com.sarathi.missionactivitytask.ui.components

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import com.sarathi.missionactivitytask.ui.theme.greenOnline
import com.sarathi.missionactivitytask.ui.theme.trackColor

@Composable
fun LinearProgressBar(progress: Float = 0f, modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier,
        color = greenOnline,
        trackColor = trackColor,
        strokeCap = StrokeCap.Round,
    )
}