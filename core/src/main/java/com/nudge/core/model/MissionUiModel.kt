package com.nudge.core.model

data class MissionUiModel(
    val missionId: Int,
    val description: String,
    val missionStatus: String,
    val activityCount: Int,
    val pendingActivityCount: Int,
    val missionSubtitle: String
)
