package com.sarathi.dataloadingmangement.model.survey.response

import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING


data class QuestionResponsModel(
    @SerializedName("questionId") var questionId: Int = 0,
    @SerializedName("questionType") var questionType: String = BLANK_STRING,
    @SerializedName("showQuestion") var showQuestion: Boolean = false,
    @SerializedName("tag") var tag: String = BLANK_STRING,
    @SerializedName("formId") var formId: Int?,
    @SerializedName("options") var options: ArrayList<QuestionOptionsResponseModel> = arrayListOf()
)
