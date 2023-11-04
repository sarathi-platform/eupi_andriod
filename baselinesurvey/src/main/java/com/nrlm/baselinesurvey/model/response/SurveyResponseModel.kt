package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.model.datamodel.QuestionEntity
import com.nrlm.baselinesurvey.model.datamodel.Sections

data class SurveyResponseModel(

    @SerializedName("surveyId")
    @Expose
    val surveyId: Int,

    @SerializedName("surveyName")
    @Expose
    val surveyName: String,

    @SerializedName("surveyPassingMark")
    @Expose
    val surveyPassingMark: Int,

    @SerializedName("thresholdScore")
    @Expose
    val thresholdScore: Int,

    @SerializedName("surveyDescription")
    @Expose
    val surveyDescription: String,

    @SerializedName("surveyDescriptionType")
    @Expose
    val surveyDescriptionType: String,

    @SerializedName("listOfQuestionSectionList")
    @Expose
    val listOfQuestionSectionList: List<Sections>
)