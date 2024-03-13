package com.patsurvey.nudge.model.dataModel


import com.google.gson.annotations.SerializedName

data class RankingEditEvent(
    @SerializedName("villageId")
    val villageId: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("status")
    val status: Boolean
)