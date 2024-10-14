package com.nudge.core.model.response

import com.google.gson.annotations.SerializedName

data class RegistryServiceResponse(
    @SerializedName("key")
    val key: String,

    @SerializedName("value")
    val value: String

)
