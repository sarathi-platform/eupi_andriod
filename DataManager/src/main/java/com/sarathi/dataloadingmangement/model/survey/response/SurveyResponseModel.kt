package com.sarathi.dataloadingmangement.model.survey.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING

data class SurveyResponseModel(

    @SerializedName("surveyId")
    @Expose
    val surveyId: Int,


    @SerializedName("passingMark")
    @Expose
    val surveyPassingMark: Int,

    @SerializedName("thresholdScore")
    @Expose
    val thresholdScore: Int,

    @SerializedName("contents")
    @Expose
    val contentList: List<ContentList>?,

    @SerializedName("languages")
    @Expose
    val surveyLanguageAttributes: List<SurveyLanguageAttributes>?,

    @SerializedName("endDate")
    @Expose
    val endDate: String,
    @SerializedName("owner")
    @Expose
    val owner: String,
    @SerializedName("referenceId")
    @Expose
    val referenceId: Int,
    @SerializedName("referenceType")
    @Expose
    val referenceType: String,
    @SerializedName("startDate")
    @Expose
    val startDate: String,
    @SerializedName("subject")
    @Expose
    val subject: String,
    @SerializedName("sections")
    @Expose
    val sections: List<Sections>?,
    @SerializedName("originalValue")
    @Expose
    val originalValue: String? = BLANK_STRING,

    )