package com.nrlm.baselinesurvey.model.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SaveSurveyRequestModel(
    @SerializedName("answerDetailDTOList")
    @Expose
    var answerDetailDTOList: List<AnswerDetailDTOList> = listOf(),

    @SerializedName("beneficiaryId")
    @Expose
    var beneficiaryId: Int,

    @SerializedName("languageId")
    @Expose
    var languageId: Int,

    @SerializedName("localCreatedDate")
    @Expose
    var localCreatedDate: Long = System.currentTimeMillis(),

    @SerializedName("localModifiedDate")
    @Expose
    var localModifiedDate: Long = System.currentTimeMillis(),

    @SerializedName("baselineSurveyStatus")
    @Expose
    var baselineSurveyStatus: Int,

    @SerializedName("sectionList")
    @Expose
    var sectionList: List<SectionList> = listOf(),

    @SerializedName("shgFlag")
    @Expose
    var shgFlag: Int? = null,

    @SerializedName("stateId")
    @Expose
    var stateId: Int,

    @SerializedName("surveyId")
    @Expose
    var surveyId: Int,

    @SerializedName("totalScore")
    @Expose
    var totalScore: Int? = 0,

    @SerializedName("userType")
    @Expose
    var userType: String,

    @SerializedName("villageId")
    @Expose
    var villageId: Int
)
