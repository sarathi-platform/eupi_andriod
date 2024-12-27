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
    val missionType: String? = "NON LIVELIHOOD",
    val livelihoodType: String?,
    val livelihoodOrder: Int?
) {
    fun getSubTitle(): String {
        val livelihoodString =
            CoreAppDetails.getContext()?.getString(R.string.livelihood) ?: BLANK_STRING
        return if (this.livelihoodType.isNullOrBlank()) {
            BLANK_STRING
        } else {
            "$livelihoodString $livelihoodOrder | ${this.livelihoodType}"
        }
    }
    fun getSubTitleDetail(): String {
        val subTitle = getSubTitle()
        return if (!subTitle.isNullOrEmpty()) "$subTitle | ${this.description}" else BLANK_STRING
    }

}
