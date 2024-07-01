package com.nudge.core.model.response

import com.google.gson.annotations.SerializedName

data class SyncImageStatusResponse(
    @SerializedName("clientId")
    val clientId: String,

    @SerializedName("fileName")
    val fileName: String,

    @SerializedName("mobileNo")
    val mobileNumber: String,

    @SerializedName("status")
    val status: String
)
