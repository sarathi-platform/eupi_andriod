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
    @SerializedName("livelihoodValue")
    val livelihoodValue: String,
    @SerializedName("eventId")
    val eventId: Int,
    @SerializedName("eventValue")
    val eventValue: String,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("productValue")
    val productValue: String,
    @SerializedName("assetType")
    val assetType: Int,
    @SerializedName("assetTypeValue")
    val assetTypeValue: String,
    @SerializedName("transactionId")
    val transactionId: String,
    @SerializedName("createdDate")
    val createdDate: Long,
    @SerializedName("modifiedDate")
    val modifiedDate: Long,
    @SerializedName("status")
    val status: Int,
    @SerializedName("eventType")
    val eventType: String
) {

}