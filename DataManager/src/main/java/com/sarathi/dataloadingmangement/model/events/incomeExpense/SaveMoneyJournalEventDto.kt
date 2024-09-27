package com.sarathi.dataloadingmangement.model.events.incomeExpense

import com.google.gson.annotations.SerializedName

data class SaveMoneyJournalEventDto(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("createdDate")
    val createdDate: Long,
    @SerializedName("transactionId")
    val transactionId: String,
    @SerializedName("particulars")
    val particulars: String,
    @SerializedName("referenceId")
    val referenceId: Int,
    @SerializedName("referenceType")
    val referenceType: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("subjectId")
    val subjectId: Int,
    @SerializedName("subjectType")
    val subjectType: String,
    @SerializedName("transactionDate")
    val transactionDate: Long,
    @SerializedName("transactionFlow")
    val transactionFlow: String,
    @SerializedName("transactionType")
    val transactionType: String
)