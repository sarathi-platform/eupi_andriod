package com.nrlm.baselinesurvey.model.datamodel


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SaveAnswerEventDto(
    @SerializedName("dateCreated")
    @Expose
    val dateCreated: Long,
    @SerializedName("languageId")
    @Expose
    val languageId: Int,
    @SerializedName("referenceId")
    @Expose
    val referenceId: Int,
    @SerializedName("sections")
    @Expose
    val sections: List<SaveAnswerEventSectionItemDto>,
    @SerializedName("subjectId")
    @Expose
    val subjectId: Int,
    @SerializedName("subjectType")
    @Expose
    val subjectType: String,
    @SerializedName("surveyId")
    @Expose
    val surveyId: Int,
)