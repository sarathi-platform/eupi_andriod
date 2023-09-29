package com.patsurvey.nudge.network.model

import com.google.gson.annotations.SerializedName

data class ErrorDataModel(
    @SerializedName("errorCode") val errorCode: Int = -1,
    @SerializedName("message") val message: String? = null,
)
