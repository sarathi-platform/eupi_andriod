package com.sarathi.dataloadingmangement.model.uiModel

data class ActivityUiModel(
    val missionId: Int,
    val activityId: Int,
    val description: String,
    val status: String,
    val taskCount: Int,
    val pendingTaskCount: Int,
    val activityType: String,
    val activityTypeId: Int
)
