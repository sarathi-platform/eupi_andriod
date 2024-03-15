package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_ID

data class QuestionAnswerResponseModel(
    @SerializedName("id") var id: String = BLANK_STRING,
    @SerializedName("surveyId") var surveyId: Int = DEFAULT_ID,
    @SerializedName("referenceId") var referenceId: Int = DEFAULT_ID,
    @SerializedName("languageId") var languageId: Int = DEFAULT_ID,
    @SerializedName("dateCreated") var dateCreated: Long = DEFAULT_ID.toLong(),
    @SerializedName("referenceType") var referenceType: String? = BLANK_STRING,
    @SerializedName("subjectId") var subjectId: Int = DEFAULT_ID,
    @SerializedName("subjectType") var subjectType: String? = BLANK_STRING,
    @SerializedName("didiId") var didiId: Int = DEFAULT_ID,
    @SerializedName("sectionId") var sectionId: String = BLANK_STRING,
    @SerializedName("status") var status: String = BLANK_STRING,
    @SerializedName("tag") var tag: String? = BLANK_STRING,
    @SerializedName("topicName") var topicName: String = BLANK_STRING,
    @SerializedName("clientId") var clientId: String = BLANK_STRING,
    @SerializedName("payload") var payload: String = BLANK_STRING,
    @SerializedName("question") var question: QuestionResponsModel? = null
)