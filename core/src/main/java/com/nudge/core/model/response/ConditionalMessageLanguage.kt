package com.nudge.core.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ConditionalMessageLanguage(
    @SerializedName("message")
    @Expose
    val message: String,
    @SerializedName("languageCode")
    @Expose
    val languageCode: String
)
