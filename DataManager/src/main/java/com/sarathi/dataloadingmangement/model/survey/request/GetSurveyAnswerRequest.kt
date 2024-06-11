package com.sarathi.dataloadingmangement.model.survey.request

import com.google.gson.annotations.SerializedName

data class GetSurveyAnswerRequest(
    @SerializedName("surveyId") val surveyId: Int,
    @Transient
    @SerializedName("mobileNumber") val mobileNumber: String,
    @SerializedName("userId") val userId: Int

)
