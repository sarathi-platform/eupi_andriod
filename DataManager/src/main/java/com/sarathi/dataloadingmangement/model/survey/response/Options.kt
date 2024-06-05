package com.sarathi.dataloadingmangement.model.survey.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.data.entities.OptionItemEntity

data class Options(

    @SerializedName("count")
    @Expose
    var count: Int = 0,

    @SerializedName("display")
    @Expose
    var display: String = BLANK_STRING,

    @SerializedName("optionId")
    @Expose
    var optionId: Int,

    @SerializedName("optionValue")
    @Expose
    var optionValue: Int,

    @SerializedName("selected")
    @Expose
    var selected: Boolean,

    @SerializedName("summary")
    @Expose
    var summary: String = BLANK_STRING,

    @SerializedName("weight")
    @Expose
    var weight: Int = -1

) {
    companion object {
        fun getOptionsFromOptionsItems(optionItems: List<OptionItemEntity>): List<Options> {
            val optionsList = mutableListOf<Options>()
            optionItems.forEach {
                optionsList.add(
                    Options(
                        optionId = it.optionId ?: 0,
                        optionValue = it.optionValue ?: 0,
                        summary = it.summary ?: BLANK_STRING,
                        weight = it.weight ?: 0,
                        display = it.display ?: BLANK_STRING,
                        count = it.count ?: 0,
                        selected = it.isSelected ?: false
                    )
                )
            }
            return optionsList
        }
    }
}
