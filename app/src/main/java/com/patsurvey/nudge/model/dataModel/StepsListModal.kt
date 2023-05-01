package com.patsurvey.nudge.model.dataModel

import com.google.gson.annotations.SerializedName

data class StepsListModal(
    @SerializedName("step_name")
    val stepName : String,
    @SerializedName("step_no")
    val stepNo : Int,
    @SerializedName("isCompleted")
    var isCompleted : Boolean
)
