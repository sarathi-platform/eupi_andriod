package com.nudge.core.model.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AppConfigApiRequest(
    @Expose
    @SerializedName("mobileNo")
    val mobileNo: String,
    @Expose
    @SerializedName("propertyNames")
    val propertyName: List<String>
)
