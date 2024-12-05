package com.nudge.core.model.request

import com.google.gson.annotations.SerializedName

data class EventConsumerRequest(
    @SerializedName("endDate")
    val endDate: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("requestId")
    val requestId: List<String?>,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("clientId")
    val clientIds: List<String>?
)