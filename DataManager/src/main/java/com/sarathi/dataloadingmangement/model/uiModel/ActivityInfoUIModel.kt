package com.sarathi.dataloadingmangement.model.uiModel

import com.nudge.core.model.CoreAppDetails
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.R

data class ActivityInfoUIModel(
    var missionName: String,
    var activityName: String,
    override var livelihoodOrder: String? = BLANK_STRING,
    override var livelihoodType: String? = BLANK_STRING,
    override var livelihoodName: String? = BLANK_STRING,
    override var programLivelihoodReferenceId: List<Int>? = emptyList()
) : InfoUiModel {

    private fun getActivityScreenSubTitle(): String {
        val livelihoodString =
            CoreAppDetails.getContext()?.getString(R.string.livelihood) ?: BLANK_STRING
        return if (this.livelihoodName.isNullOrBlank()) {
            BLANK_STRING
        } else {
            "$livelihoodString $livelihoodOrder | ${this.livelihoodName}"
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
