package com.patsurvey.nudge.model.dataModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AnswerOptionModel(
    @SerializedName("id")
    @Expose
    val id:Int,

    @SerializedName("optionText")
    @Expose
    val optionText:String,

    @SerializedName("isSelected")
    @Expose
    var isSelected:Boolean=false,

    )
