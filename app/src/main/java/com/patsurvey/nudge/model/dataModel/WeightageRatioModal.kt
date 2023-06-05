package com.patsurvey.nudge.model.dataModel

import com.google.gson.annotations.SerializedName

data class WeightageRatioModal(
    @SerializedName("weightage")
    val weightage : String="0.0",
    @SerializedName("ratio")
    val ratio : String="0.0",
    @SerializedName("operator")
    val operator : String,
    @SerializedName("score")
    val score: String
)