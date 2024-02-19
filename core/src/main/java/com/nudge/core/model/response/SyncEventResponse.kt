package com.nudge.core.model.response

import com.google.gson.annotations.SerializedName

data class SyncEventResponse(
    @SerializedName("clientId") val clientId: String, val status: String,
    val requestId: String,
    val result: String,
    val errorMessage: String
)
