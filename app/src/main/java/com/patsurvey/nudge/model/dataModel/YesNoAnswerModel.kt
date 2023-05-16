package com.patsurvey.nudge.model.dataModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class YesNoAnswerModel(
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("question")
    @Expose
    val question: String,

    @SerializedName("answer")
    @Expose
    val answer: Boolean = false,

    @SerializedName("questionAnswered")
    @Expose
    val questionAnswered: Boolean = false,

)
