package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.model.datamodel.ValuesDto

data class QuestionOptionsResponseModel(
    @SerializedName("optionId") var optionId: Int? = null,
    @SerializedName("selectedValue") var selectedValue: String? = null,
    @SerializedName("referenceId") var referenceId: String = BLANK_STRING,
    @SerializedName("tag") var tag: Int = 0,
    @SerializedName("selectedValueWithIds")
    @Expose
    val selectedValueWithIds: List<ValuesDto> = emptyList()
)
