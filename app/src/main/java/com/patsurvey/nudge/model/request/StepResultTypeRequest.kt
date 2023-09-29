package com.patsurvey.nudge.model.request

import com.google.gson.annotations.SerializedName

data class StepResultTypeRequest(
    @SerializedName("type") val type:String,
    @SerializedName("result") val result:String
)
