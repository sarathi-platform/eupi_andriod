package com.nudge.core.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ApiResponseModel<T>(
    @SerializedName("status")
    @Expose val status: String,

    @SerializedName("message")
    @Expose
     val message: String,

    @SerializedName("data")
    @Expose
     val data: T? = null,

    @SerializedName("lastSyncTime")
    @Expose
    val lastSyncTime: String? = null
)