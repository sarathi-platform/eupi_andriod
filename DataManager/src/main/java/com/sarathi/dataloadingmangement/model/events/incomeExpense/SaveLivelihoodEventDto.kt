package com.sarathi.dataloadingmangement.model.events.incomeExpense

import com.google.gson.annotations.SerializedName

data class SaveLivelihoodEventDto(
    @SerializedName("subjectId")
    val subjectId: Int,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("date")
    val date: Long,
    @SerializedName("assetCount")
    val assetCount: Int,
    @SerializedName("livelihoodId")
    val livelihoodId: Int,
    @SerializedName("eventId")
    val eventId: Int,
    @SerializedName("eventValue")
    val eventValue: String,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("assetType")
    val assetType: Int,
    @SerializedName("transactionId")
    val transactionId: String,
)