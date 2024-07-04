package com.sarathi.dataloadingmangement.model.events

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto

data class SaveAnswerEventOptionItemDto(
    @SerializedName("optionId")
    @Expose
    val optionId: Int,
    @SerializedName("selectedValue")
    @Expose
    val selectedValue: String?,
    @SerializedName("referenceId")
    @Expose
    val referenceId: String = "",
    @SerializedName("tag")
    @Expose
    val tag: List<Int> = listOf(),
    @SerializedName("optionDesc")
    @Expose
    val optionDesc: String = BLANK_STRING,
    @SerializedName("selectedValueWithIds")
    @Expose
    val selectedValueWithIds: List<ValuesDto> = emptyList(),
)