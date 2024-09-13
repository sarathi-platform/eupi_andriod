package com.sarathi.dataloadingmangement.model.events.incomeExpense

import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetJournalEntity

data class SaveAssetJournalEventDto(
    @SerializedName("assetCount")
    val assetCount: Int,
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
    @SerializedName("assetId")
    val assetId: Int,
    @SerializedName("modifiedDate")
    val modifiedDate: Long,

) {
    companion object {

        fun getAssetJournalEventDto(assetJournalEntity: AssetJournalEntity): SaveAssetJournalEventDto {
            return SaveAssetJournalEventDto(
                assetId = assetJournalEntity.assetId,
                assetCount = assetJournalEntity.assetCount,
                particulars = assetJournalEntity.transactionDetails,
                referenceId = assetJournalEntity.referenceId,
                referenceType = assetJournalEntity.referenceType,
                subjectId = assetJournalEntity.subjectId,
                status = assetJournalEntity.status,
                subjectType = assetJournalEntity.subjectType,
                transactionId = assetJournalEntity.transactionId,
                transactionFlow = assetJournalEntity.transactionFlow,
                transactionDate = assetJournalEntity.transactionDate,
                transactionType = assetJournalEntity.transactionType,
                createdDate = assetJournalEntity.createdDate,
                modifiedDate = assetJournalEntity.modifiedDate

            )
        }
    }
}