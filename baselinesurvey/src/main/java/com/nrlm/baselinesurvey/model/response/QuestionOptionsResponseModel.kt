package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.SerializedName

data class QuestionOptionsResponseModel(
    @SerializedName("optionId") var optionId: Int? = null,
    @SerializedName("selectedValue") var selectedValue: String? = null,
    @SerializedName("referenceId") var referenceId: String? = null

)
