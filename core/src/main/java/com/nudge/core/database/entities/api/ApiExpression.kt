package com.nudge.core.database.entities.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ApiExpression(
    @SerializedName("condition")
    @Expose
    val condition: String,
    @SerializedName("activityType")
    @Expose
    val activityType: List<String>,
)

