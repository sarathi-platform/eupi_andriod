package com.nrlm.baselinesurvey.model.request

import com.google.gson.annotations.SerializedName

data class SurveyRequestBodyModel(
    /*@SerializedName("languageId") val languageId: Int,
    @SerializedName("surveyName") val surveyName: String,
    @SerializedName("stateId") val stateId: Int*/

   @SerializedName("languageId") val languageId: Int,
   @SerializedName("surveyName") val surveyName: String,
   @SerializedName("referenceId") val referenceId: Int,
   @SerializedName("referenceType") val referenceType: String
)
