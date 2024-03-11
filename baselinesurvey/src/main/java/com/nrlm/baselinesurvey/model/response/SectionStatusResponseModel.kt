package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.SerializedName

data class SectionStatusResponseModel(
    @SerializedName("surveyId") val surveyId: Int?,
    @SerializedName("didiId") val didiId: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("sectionId") val sectionId: String?
)
