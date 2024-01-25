package com.nrlm.baselinesurvey.model.datamodel

import com.google.gson.annotations.SerializedName

data class SurveyResponse(
    @SerializedName("surveyId")
    val surveyId: Int? = null,

    @SerializedName("surveyPassingMark")
    val surveyPassingMark: Int,

    @SerializedName("thresholdScore")
    val thresholdScore: Int,

    @SerializedName("listOfQuestionSectionList")
    val sectionList: List<Sections?>? = null
)