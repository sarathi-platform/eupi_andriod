package com.patsurvey.nudge.model.dataModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class YesNoQuestionModel(
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("question")
    @Expose
    val question: String
)
