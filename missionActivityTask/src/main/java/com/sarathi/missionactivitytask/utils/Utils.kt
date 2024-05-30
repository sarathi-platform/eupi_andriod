package com.sarathi.missionactivitytask.utils

import androidx.compose.ui.graphics.Color
import com.nudge.core.theme.mediumRankColor

fun statusColor(status: StatusEnum): Color = when (status) {
    StatusEnum.Active -> Color.Green
    StatusEnum.Inactive -> Color.Red
    StatusEnum.InProgress -> mediumRankColor
    StatusEnum.Completed -> Color.Green
    StatusEnum.Pending -> Color.Gray
}