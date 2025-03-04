package com.nudge.core.model.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Validation(
    @SerializedName("condition")
    @Expose
    val condition: String,
    @SerializedName("expression")
    @Expose
    val expression: String?,
    @SerializedName("field")
    @Expose
    val field: String?,
    @SerializedName("message")
    @Expose
    val message: String,
    @SerializedName("conditionalMessage")
    @Expose
    val conditionalMessage: List<ConditionalMessage>?,
    @SerializedName("languageCode")
    @Expose
    val languageCode: String,
    @SerializedName("regex")
    @Expose
    val regex: String?,

)