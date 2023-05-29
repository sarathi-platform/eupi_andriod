package com.patsurvey.nudge.model.dataModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.utils.BLANK_STRING

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

    @SerializedName("weight")
    @Expose
    var weight:Int?=0,

    @SerializedName("summary")
    @Expose
    var summary:String?= BLANK_STRING,

    @SerializedName("optionValue")
    @Expose
    var optionValue:Int?=0,
  )

