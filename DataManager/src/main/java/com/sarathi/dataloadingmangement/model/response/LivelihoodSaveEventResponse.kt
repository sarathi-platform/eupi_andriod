package com.sarathi.dataloadingmangement.model.response

import com.google.gson.annotations.SerializedName

data class LivelihoodSaveEventResponse(
    @SerializedName("transactionId")
    val transactionId: String,
    @SerializedName("subjectId")
    val subjectId: Int,
    @SerializedName("livelihoodId")
    val livelihoodId: Int,
    @SerializedName("eventId")
    val eventId: Int,
    @SerializedName("eventType")
    val eventType: String,
    @SerializedName("date")
    val date: Long,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("assetCount")
    val assetCount: Int,
    @SerializedName("assetType")
    val assetType: Int,
    @SerializedName("assetTypeValue")
    val assetTypeValue: String,
    @SerializedName("eventValue")
    val eventValue: String,
    @SerializedName("livelihoodValue")
    val livelihoodValue: String,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("productValue")
    val productValue: String,
)