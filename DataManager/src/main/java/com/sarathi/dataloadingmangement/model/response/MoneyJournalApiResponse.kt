package com.sarathi.dataloadingmangement.model.response


import com.google.gson.annotations.SerializedName

data class MoneyJournalApiResponse(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("createdDate")
    val createdDate: Long,
    @SerializedName("id")
    val id: Int,
    @SerializedName("transactionId")
    val transactionId: String,
    @SerializedName("modifiedDate")
    val modifiedDate: Long,
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
    val transactionType: String,
    @SerializedName("eventId")
    val eventId: Int?,
    @SerializedName("eventType")
    val eventType: String?,
)