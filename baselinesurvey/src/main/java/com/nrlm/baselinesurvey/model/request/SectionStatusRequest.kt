package com.nrlm.baselinesurvey.model.request

import com.google.gson.annotations.SerializedName

data class SectionStatusRequest(
    @SerializedName("sectionId") val sectionId: Int,
    @SerializedName("surveyId") val surveyId: Int,
    @Transient
    @SerializedName("mobileNumber") val mobileNumber: String
)
