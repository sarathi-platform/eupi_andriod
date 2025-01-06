package com.nudge.core.model.response

import com.google.gson.annotations.SerializedName

data class EventConsumerResponse(

    @SerializedName("clientId")
    val clientId: String,

    @SerializedName("eventId")
    val eventId: String,

    @SerializedName("requestId")
    val requestId: String,

    @SerializedName("status")
    val status: String
)