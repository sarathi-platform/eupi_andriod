package com.sarathi.dataloadingmangement.model.survey.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING

data class QuestionOptionsResponseModel(
    @SerializedName("optionId") var optionId: Int? = null,
    @SerializedName("selectedValue") var selectedValue: String? = null,
    @SerializedName("referenceId") var referenceId: String = BLANK_STRING,
    @SerializedName("tag") var tag: Int = 0,
    @SerializedName("selectedValueWithIds")
    @Expose
    val selectedValueWithIds: List<ValuesDto> = emptyList()
)
