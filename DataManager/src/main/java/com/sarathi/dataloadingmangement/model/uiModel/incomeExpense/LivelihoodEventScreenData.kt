package com.sarathi.dataloadingmangement.model.uiModel.incomeExpense

import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.enums.LivelihoodEventTypeDataCaptureMapping

data class LivelihoodEventScreenData(
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
    @SerializedName("productValue")
    val productValue: String,
    @SerializedName("assetTypeValue")
    val assetTypeValue: String,
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
    @SerializedName("selectedEvent")
    val selectedEvent: LivelihoodEventTypeDataCaptureMapping,
    @SerializedName("localTransactionId")
    val localTransactionId: String,
    @SerializedName("toAssetType")
    val toAssetType: Int = -1,
    @SerializedName("toAssetTypeValue")
    val toAssetTypeValue: String = BLANK_STRING
)