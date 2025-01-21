package com.sarathi.dataloadingmangement.model.uiModel

import com.nudge.core.model.CoreAppDetails
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.R

data class ActivityInfoUIModel(
    var missionName: String,
    var activityName: String,
    var livelihoodOrder: String? = BLANK_STRING,
    var livelihoodType: String? = BLANK_STRING
) {

    private fun getActivityScreenSubTitle(): String {
        val livelihoodString =
            CoreAppDetails.getContext()?.getString(R.string.livelihood) ?: BLANK_STRING
        return if (this.livelihoodType.isNullOrBlank()) {
            BLANK_STRING
        } else {
            "$livelihoodString $livelihoodOrder | ${this.livelihoodType}"
        }
    }

    fun getTaskScreenSubTitle(): String {
        val subTitle = getActivityScreenSubTitle()
        return if (!subTitle.isNullOrEmpty()) "$subTitle | ${this.missionName}" else BLANK_STRING
    }

    companion object {
        fun getDefaultValue(): ActivityInfoUIModel {
            return ActivityInfoUIModel(BLANK_STRING, BLANK_STRING, BLANK_STRING, BLANK_STRING)
        }
    }
}
