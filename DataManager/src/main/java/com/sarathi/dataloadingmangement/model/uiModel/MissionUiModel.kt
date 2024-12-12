package com.sarathi.dataloadingmangement.model.uiModel

import com.nudge.core.model.CoreAppDetails
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.R

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
        val livelihoodString =
            CoreAppDetails.getContext()?.getString(R.string.livelihood) ?: BLANK_STRING
        val tagLabel = when (this.livelihoodOrder) {
            1, 2 -> "$livelihoodString $livelihoodOrder"
            else -> BLANK_STRING
        }
        return if (tagLabel.isBlank() || this.livelihoodType.isNullOrBlank()) {
            BLANK_STRING
        } else {
            "$tagLabel > ${this.livelihoodType}"
        }
    }
    fun getSubTitleDetail(): String {
        val livelihoodString =
            CoreAppDetails.getContext()?.getString(R.string.livelihood) ?: BLANK_STRING
        val tagLabel = when (this.livelihoodOrder) {
            1, 2 -> "$livelihoodString $livelihoodOrder"
            else -> BLANK_STRING
        }
        return if (tagLabel.isBlank() || this.livelihoodType.isNullOrBlank()) {
            BLANK_STRING
        } else {
            "$tagLabel | ${this.livelihoodType} | ${this.description}"
        }
    }

}
