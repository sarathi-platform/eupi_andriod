package com.sarathi.dataloadingmangement.model.uiModel

import com.sarathi.dataloadingmangement.BLANK_STRING

data class ActivityUiModel(
    val missionId: Int,
    val activityId: Int,
    val description: String,
    val status: String,
    val taskCount: Int,
    val pendingTaskCount: Int,
    var icon: String? = BLANK_STRING
)
