package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING

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
    val tag: Int = 0,
    @SerializedName("optionDesc")
    @Expose
    val optionDesc: String = BLANK_STRING,
    @SerializedName("selectedValueWithIds")
    @Expose
    val selectedValueWithIds: List<ValuesDto> = emptyList(),
)