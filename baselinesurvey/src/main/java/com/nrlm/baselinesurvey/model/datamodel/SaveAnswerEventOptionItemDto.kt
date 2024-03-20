package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

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
    val tag: Int = 0
)