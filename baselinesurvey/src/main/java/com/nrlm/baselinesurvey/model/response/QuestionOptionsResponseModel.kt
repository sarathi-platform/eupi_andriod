package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING

data class QuestionOptionsResponseModel(
    @SerializedName("optionId") var optionId: Int? = null,
    @SerializedName("selectedValue") var selectedValue: String? = null,
    @SerializedName("referenceId") var referenceId: String = BLANK_STRING,
    @SerializedName("tag") var tag: Int = 0

)
