package com.nudge.core.model.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Validations(
    @SerializedName("asset_type")
    @Expose
    val assetType: String,
    @SerializedName("event_type")
    @Expose
    val eventType: String,
    @SerializedName("product_type")
    @Expose
    val productType: String,
    @SerializedName("livelihood_type")
    @Expose
    val livelihoodType: String,
    @SerializedName("validation")
    @Expose
    val validation: List<Validation>
)