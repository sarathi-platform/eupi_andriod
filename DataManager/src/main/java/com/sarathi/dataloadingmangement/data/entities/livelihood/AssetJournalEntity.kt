package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarathi.dataloadingmangement.ASSET_JOURNAL_TABLE_NAME

@Entity(tableName = ASSET_JOURNAL_TABLE_NAME)
data class AssetJournalEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,
    val userId: String,
    val transactionId: String,
    val transactionDate: Long,
    val transactionDetails: String,
    val transactionFlow: String,
    val transactionType: String,
    val assetId: Int,
    val assetCount: Int,
    val referenceId: Int,
    val referenceType: String,
    val subjectId: Int,
    val subjectType: String,
    val status: Int,
    val modifiedDate: Long,

    ) {

    companion object {

        fun getAssetJournalEntity(
            userId: String,
            count: Int,
            date: Long,
            particulars: String,
            transactionId: String,
            referenceId: Int,
            referenceType: String,
            subjectType: String,
            subjectId: Int,
            assetId: Int,
            transactionFlow: String,
        ): AssetJournalEntity {
            return AssetJournalEntity(
                id = 0,
                userId = userId,
                assetCount = count,
                transactionDate = date,
                transactionId = transactionId,
                referenceId = referenceId,
                referenceType = referenceType,
                subjectType = subjectType,
                subjectId = subjectId,
                transactionDetails = particulars,
                transactionFlow = transactionFlow,
                status = 1,
                transactionType = referenceType,
                assetId = assetId,
                modifiedDate = System.currentTimeMillis()
            )

        }

        /*fun getAssetJournalEntity(
            assetJournalApiResponse: AssetJournalApiResponse,
            userId: String
        ): AssetJournalEntity {
            return assetJournalEntity(
                id = 0,
                userId = userId,
                transactionAmount = assetJournalApiResponse.amount.toDouble(),
                transactionDate = assetJournalApiResponse.transactionDate,
                transactionId = assetJournalApiResponse.transactionId,
                referenceId = assetJournalApiResponse.referenceId,
                referenceType = assetJournalApiResponse.referenceType,
                subjectType = assetJournalApiResponse.subjectType,
                subjectId = assetJournalApiResponse.subjectId,
                transactionDetails = assetJournalApiResponse.particulars,
                transactionFlow = assetJournalApiResponse.transactionFlow,
                status = assetJournalApiResponse.status,
                transactionType = assetJournalApiResponse.transactionType,
                modifiedDate = assetJournalApiResponse.modifiedDate
            )
        }*/
    }

}
