package com.sarathi.dataloadingmangement.model.survey.response

import com.google.gson.annotations.SerializedName

data class SurveyLanguageAttributes(
    @SerializedName("languageCode")
    val languageCode: String,

    @SerializedName("surveyName")
    val surveyName: String,
    @SerializedName("sections")
    val sections: List<Sections>


)
