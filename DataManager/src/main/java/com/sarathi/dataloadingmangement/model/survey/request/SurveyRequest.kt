package com.sarathi.dataloadingmangement.model.survey.request

import com.google.gson.annotations.SerializedName

data class SurveyRequest(
    @SerializedName("referenceId") val referenceId: Int,
    @SerializedName("referenceType") val referenceType: String,
    @SerializedName("surveyId") val surveyId: Int,

    )