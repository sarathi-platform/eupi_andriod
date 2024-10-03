package com.sarathi.dataloadingmangement.model.events.incomeExpense

import com.google.gson.annotations.SerializedName

data class DeleteLivelihoodEvent(
    @SerializedName("doerId")
    val doerId: Int,
    @SerializedName("transactionId")
    val transactionId: String,
    @SerializedName("subjectId")
    val subjectId: Int,
    @SerializedName("subjectType")
    val subjectType: String,
    @SerializedName("modifiedDate")
    val modifiedDate: Long
)
