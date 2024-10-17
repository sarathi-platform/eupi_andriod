package com.sarathi.dataloadingmangement.model.uiModel

import com.sarathi.dataloadingmangement.BLANK_STRING

data class ActivityUiModel(
    val missionId: Int,
    val activityId: Int,
    val description: String,
    val status: String,
    val taskCount: Int,
    val pendingTaskCount: Int,
    val activityType: String,
    val activityTypeId: Int,
    var icon: String? = BLANK_STRING
) {

    companion object {

        fun getSelectAllActivityUiModel(missionId: Int, description: String): ActivityUiModel {
            return ActivityUiModel(
                missionId = missionId,
                activityId = -1,
                description = description,
                status = BLANK_STRING,
                taskCount = 0,
                pendingTaskCount = 0,
                activityTypeId = 0,
                activityType = BLANK_STRING
            )

        }

    }

}
