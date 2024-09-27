package com.sarathi.dataloadingmangement.model.uiModel

data class MissionUiModel(
    val missionId: Int,
    val description: String,
    val missionStatus: String,
    val activityCount: Int,
    val pendingActivityCount: Int
)
