package com.sarathi.dataloadingmangement.model.uiModel

import com.nudge.core.model.CoreAppDetails
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.R

interface InfoUiModel {
    var livelihoodOrder: String?
    var livelihoodType: String?
    var livelihoodName: String?
}

data class MissionInfoUIModel(
    var title: String,
    override var livelihoodOrder: String? = BLANK_STRING,
    override var livelihoodType: String? = BLANK_STRING,
    override var livelihoodName: String? = BLANK_STRING,
) : InfoUiModel {
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
