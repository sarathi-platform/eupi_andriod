package com.nrlm.baselinesurvey.model.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AnswerDetailDTOList(
    @SerializedName("options")
    @Expose
    var options: List<Options> = listOf(),

    @SerializedName("questionId")
    @Expose
    var questionId: Int,

    @SerializedName("section")
    @Expose
    var section: Int?,

    @SerializedName("questionName")
    @Expose
    var questionName: String,

    @SerializedName("questionSummary")
    @Expose
    var questionSummary: String,

    @SerializedName("questionType")
    @Expose
    var questionType: String
)
