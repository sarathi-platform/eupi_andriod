package com.patsurvey.nudge.model.request

import com.google.gson.annotations.SerializedName

data class GetQuestionListRequest(
    @SerializedName("languageId") var languageId: Int,
    @SerializedName("stateId") var stateId: Int,
    @SerializedName("surveyName") var surveyName: String
)
