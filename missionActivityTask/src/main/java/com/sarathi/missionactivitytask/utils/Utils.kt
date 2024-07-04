package com.sarathi.missionactivitytask.utils

import androidx.compose.ui.graphics.Color
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.greenOnline

fun statusColor(status: String): Color = when (status) {
    StatusEnum.Active.name -> Color.Green
    StatusEnum.Inactive.name -> Color.Red
    StatusEnum.INPROGRESS.name -> Color.Gray
    StatusEnum.COMPLETED.name -> greenOnline
    StatusEnum.PENDING.name -> Color.Gray
    StatusEnum.NOT_STARTED.name -> Color.Gray
    StatusEnum.NOT_AVAILABLE.name -> Color.Gray
    else -> {
        Color.Gray
    }
}

fun String.getImagePathFromString(): String {
    return try {
        this.split("|").first()
    } catch (ex: Exception) {
        return BLANK_STRING
    }
}