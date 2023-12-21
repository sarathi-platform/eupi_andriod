package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.model.request.SectionList

data class SaveSurveyResponseModel(
    @SerializedName("createdDate")
    @Expose
    var createdDate: Int? = null,

    @SerializedName("modifiedDate")
    @Expose
    var modifiedDate: Int? = null,

    @SerializedName("createdBy")
    @Expose
    var createdBy: Int? = null,

    @SerializedName("modifiedBy")
    @Expose
    var modifiedBy: Int? = null,

    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("beneficiaryId")
    @Expose
    var beneficiaryId: Int? = null,

    @SerializedName("status")
    @Expose
    var status: Int? = null,

    @SerializedName("answers")
    @Expose
    var answers: List<Answers> = listOf(),

    @SerializedName("surveyId")
    @Expose
    var surveyId: Int? = null,

    @SerializedName("userType")
    @Expose
    var userType: String? = null,

    @SerializedName("totalScore")
    @Expose
    var totalScore: Int? = null,

    @SerializedName("stateId")
    @Expose
    var stateId: Int? = null,

    @SerializedName("villageId")
    @Expose
    var villageId: Int? = null,

    @SerializedName("baselineSurveyStatus")
    @Expose
    var baselineSurveyStatus: Int? = null,

    @SerializedName("sectionList")
    @Expose
    var sectionList: List<SectionList> = listOf(),

    @SerializedName("localCreatedDate")
    @Expose
    var localCreatedDate: Int? = null,

    @SerializedName("localModifiedDate")
    @Expose
    var localModifiedDate: Int? = null,

    @SerializedName("shgFlag")
    @Expose
    var shgFlag: Int? = null
)
