package com.nudge.core.model.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Validations(
    @SerializedName("assetType")
    @Expose
    val assetType: String,
    @SerializedName("eventName")
    @Expose
    val eventName: String,
    @SerializedName("productType")
    @Expose
    val productType: String,
    @SerializedName("livelihoodType")
    @Expose
    val livelihoodType: String,
    @SerializedName("validation")
    @Expose
    val validation: List<Validation>
)