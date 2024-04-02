package com.nrlm.baselinesurvey.model.response

import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_ID

data class QuestionResponsModel(
    @SerializedName("questionId") var questionId: Int = DEFAULT_ID,
    @SerializedName("questionType") var questionType: String = BLANK_STRING,
    @SerializedName("showQuestion") var showQuestion: Boolean = false,
    @SerializedName("tag") var tag: String = BLANK_STRING,
    @SerializedName("options") var options: ArrayList<Any> = arrayListOf()
)
