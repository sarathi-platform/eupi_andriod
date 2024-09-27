package com.sarathi.dataloadingmangement.model.survey.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING

data class SurveyLanguageAttributes(
    @SerializedName("languageCode")
    val languageCode: String,

    @SerializedName("paraphrase")
    @Expose
    val paraphrase: String? = BLANK_STRING,

    @SerializedName("description")
    @Expose
    val description: String = BLANK_STRING,
)
