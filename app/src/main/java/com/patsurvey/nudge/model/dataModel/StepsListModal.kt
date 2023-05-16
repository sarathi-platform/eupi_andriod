package com.patsurvey.nudge.model.dataModel

import com.google.gson.annotations.SerializedName

data class StepsListModal(
    @SerializedName("id")
    val id: Int,
    @SerializedName("orderNumber")
    val orderNumber: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("villageId")
    var villageId: Int
)
