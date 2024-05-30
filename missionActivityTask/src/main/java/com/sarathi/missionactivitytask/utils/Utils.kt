package com.sarathi.missionactivitytask.utils

import androidx.compose.ui.graphics.Color
import com.nudge.core.ui.events.theme.mediumRankColor

fun statusColor(status: StatusEnum): Color = when (status) {
    StatusEnum.Active -> Color.Green
    StatusEnum.Inactive -> Color.Red
    StatusEnum.INPROGRESS -> mediumRankColor
    StatusEnum.COMPLETED -> Color.Green
    StatusEnum.PENDING -> Color.Gray
    StatusEnum.NOT_STARTED -> Color.Gray
}