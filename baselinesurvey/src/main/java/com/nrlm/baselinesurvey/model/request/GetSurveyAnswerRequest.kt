package com.nrlm.baselinesurvey.model.request

import com.google.gson.annotations.SerializedName

data class GetSurveyAnswerRequest(
    @SerializedName("surveyId") val surveyId: Int,
    @Transient
    @SerializedName("userId") val userId: String
)
