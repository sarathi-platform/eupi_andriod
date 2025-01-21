package com.sarathi.dataloadingmangement.model.uiModel

import com.nudge.core.model.CoreAppDetails
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.R

data class MissionInfoUIModel(
    var title: String,
    var livelihoodOrder: String? = BLANK_STRING,
    var livelihoodType: String? = BLANK_STRING,
    var livelihoodName: String? = BLANK_STRING,
) {
    fun getActivityScreenSubTitle(): String {
        val livelihoodString =
            CoreAppDetails.getContext()?.getString(R.string.livelihood) ?: BLANK_STRING
        return if (this.livelihoodName.isNullOrBlank()) {
            BLANK_STRING
        } else {
            "$livelihoodString $livelihoodOrder | ${this.livelihoodName}"
        }
    }

    companion object {
        fun getDefaultValue(): MissionInfoUIModel {
            return MissionInfoUIModel(BLANK_STRING, BLANK_STRING, BLANK_STRING)
        }
    }
}
