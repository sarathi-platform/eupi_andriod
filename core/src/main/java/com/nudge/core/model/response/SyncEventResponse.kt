package com.nudge.core.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SyncEventResponse(
    @SerializedName("clientId")
    @Expose
    val clientId: String,

    @SerializedName("status")
    @Expose
    val status: String,

    @SerializedName("requestId")
    @Expose
    val requestId: String,

    @SerializedName("result")
    @Expose
    val eventResult: EventResult,

    @SerializedName("errorMessage")
    @Expose
    val errorMessage: String,

    @SerializedName("mobileNumber")
    @Expose
    val mobileNumber: String,

    @SerializedName("eventName")
    @Expose
    val eventName: String,

    @SerializedName("type")
    @Expose
    val type: String,
)
