package com.sarathi.dataloadingmangement.model.uiModel.incomeExpense

import com.sarathi.dataloadingmangement.enums.LivelihoodEventTypeDataCaptureMapping

data class LivelihoodEventScreenData(
    val subjectId: Int,
    val amount: Int,
    val date: Long,
    val assetCount: Int,
    val livelihoodId: Int,
    val livelihoodValue: String,
    val productValue: String,
    val assetTypeValue: String,
    val eventId: Int,
    val eventValue: String,
    val productId: Int,
    val assetType: Int,
    val transactionId: String,
    val selectedEvent: LivelihoodEventTypeDataCaptureMapping
)