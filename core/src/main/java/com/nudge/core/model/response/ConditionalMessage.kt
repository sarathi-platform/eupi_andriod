package com.nudge.core.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ConditionalMessage(
    @SerializedName("condition")
    @Expose
    val condition: String,
    @SerializedName("languageList")
    @Expose
    val languageList: List<ConditionalMessageLanguage>?
)
