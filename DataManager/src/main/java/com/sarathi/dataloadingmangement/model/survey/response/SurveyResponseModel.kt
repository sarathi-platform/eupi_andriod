package com.sarathi.dataloadingmangement.model.survey.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SurveyResponseModel(

    @SerializedName("surveyId")
    @Expose
    val surveyId: Int,

    @SerializedName("surveyName")
    @Expose
    val surveyName: String,

    @SerializedName("passingMark")
    @Expose
    val surveyPassingMark: Int,

    @SerializedName("thresholdScore")
    @Expose
    val thresholdScore: Int,

    /* @SerializedName("surveyDescription")
     @Expose
     val surveyDescription: String,

     @SerializedName("surveyDescriptionType")
     @Expose
     val surveyDescriptionType: String,*/

    @SerializedName("contents")
    @Expose
    val contentList: List<ContentList>,

    @SerializedName("sections")
    @Expose
    val sections: List<Sections>,


    @SerializedName("approver")
    @Expose
    val approver: String,
    @SerializedName("endDate")
    @Expose
    val endDate: String,
    @SerializedName("languageCode")
    @Expose
    val languageCode: String,
    @SerializedName("owner")
    @Expose
    val owner: String,
    @SerializedName("publish")
    @Expose
    val publish: String,
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
)