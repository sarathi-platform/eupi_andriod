package com.sarathi.dataloadingmangement.model.events.incomeExpense

import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity

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
    val transactionType: String,
    @SerializedName("modifiedDate")
    val modifiedDate: Long,
) {
    companion object {
        fun getMoneyJournalEventDto(moneyJournalEntity: MoneyJournalEntity): SaveMoneyJournalEventDto {
            return SaveMoneyJournalEventDto(
                amount = moneyJournalEntity.transactionAmount.toInt(),
                particulars = moneyJournalEntity.transactionDetails,
                subjectId = moneyJournalEntity.subjectId,
                transactionId = moneyJournalEntity.transactionId,
                transactionFlow = moneyJournalEntity.transactionFlow,
                transactionType = moneyJournalEntity.transactionType,
                referenceId = moneyJournalEntity.referenceId,
                referenceType = moneyJournalEntity.referenceType,
                subjectType = moneyJournalEntity.subjectType,
                status = moneyJournalEntity.status,
                transactionDate = moneyJournalEntity.transactionDate,
                createdDate = moneyJournalEntity.createdDate,
                modifiedDate = moneyJournalEntity.modifiedDate
            )
        }
    }
}