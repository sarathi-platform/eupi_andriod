package com.sarathi.dataloadingmangement.model.uiModel

import com.sarathi.dataloadingmangement.BLANK_STRING

data class MissionUiModel(
    val missionId: Int,
    val description: String,
    val missionStatus: String,
    val activityCount: Int,
    val pendingActivityCount: Int,
    val programId: Int,
    val livelihoodType: String?,
    val livelihoodOrder: Int?
) {
    fun getSubTitle(): String {
        val tagLabel = when (this.livelihoodOrder) {
            1 -> "Livelihood 1"
            2 -> "Livelihood 2"
            else -> BLANK_STRING
        }
        return if (tagLabel.isBlank() || this.livelihoodType.isNullOrBlank()) {
            BLANK_STRING
        } else {
            "$tagLabel > ${this.livelihoodType}"
        }
    }

}
