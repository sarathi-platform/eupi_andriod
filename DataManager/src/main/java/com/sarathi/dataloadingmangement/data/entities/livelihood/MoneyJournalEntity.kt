package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nudge.core.DD_MMM_YYYY_FORMAT
import com.nudge.core.toInMillisec
import com.sarathi.dataloadingmangement.MONEY_JOURNAL_TABLE_NAME
import com.sarathi.dataloadingmangement.model.response.MoneyJournalApiResponse

@Entity(tableName = MONEY_JOURNAL_TABLE_NAME)
data class MoneyJournalEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,
    val userId: String,
    val transactionId: String,
    val transactionDate: Long,
    val transactionDetails: String,
    var transactionFlow: String,
    val transactionType: String,
    val transactionAmount: Double,
    val referenceId: Int,
    val referenceType: String,
    val subjectId: Int,
    val subjectType: String,
    val status: Int,
    val modifiedDate: Long,
    val createdDate: Long,
    val eventId: Int?,
    val eventType: String?,
    val localTransactionId: String? // To identify the unique events for delete operation
) {
    companion object {

        fun getMoneyJournalEntity(
            userId: String,
            amount: Int,
            date: String,
            particulars: String,
            referenceId: String,
            grantId: Int,
            grantType: String,
            subjectType: String,
            subjectId: Int,
            transactionFlow: String,
            dateFormat: String = DD_MMM_YYYY_FORMAT,
            createdDate: Long,
            eventId: Int? = null,
            eventType: String? = null,
            localTransactionId: String?
        ): MoneyJournalEntity {
            return MoneyJournalEntity(
                id = 0,
                userId = userId,
                transactionAmount = amount.toDouble(),
                transactionDate = date.toInMillisec(dateFormat),
                transactionId = referenceId,
                referenceId = grantId,
                referenceType = grantType,
                subjectType = subjectType,
                subjectId = subjectId,
                transactionDetails = particulars,
                transactionFlow = transactionFlow,
                status = 1,
                transactionType = grantType,
                modifiedDate = System.currentTimeMillis(),
                createdDate = createdDate,
                eventId = eventId,
                eventType = eventType,
                localTransactionId = localTransactionId

            )

        }

        fun getMoneyJournalEntity(
            moneyJournalApiResponse: MoneyJournalApiResponse,
            userId: String
        ): MoneyJournalEntity {
            return MoneyJournalEntity(
                id = 0,
                userId = userId,
                transactionAmount = moneyJournalApiResponse.amount.toDouble(),
                transactionDate = moneyJournalApiResponse.transactionDate,
                transactionId = moneyJournalApiResponse.transactionId,
                referenceId = moneyJournalApiResponse.referenceId,
                referenceType = moneyJournalApiResponse.referenceType,
                subjectType = moneyJournalApiResponse.subjectType,
                subjectId = moneyJournalApiResponse.subjectId,
                transactionDetails = moneyJournalApiResponse.particulars,
                transactionFlow = moneyJournalApiResponse.transactionFlow,
                status = moneyJournalApiResponse.status,
                transactionType = moneyJournalApiResponse.transactionType,
                modifiedDate = moneyJournalApiResponse.modifiedDate,
                createdDate = moneyJournalApiResponse.createdDate,
                eventId = moneyJournalApiResponse.eventId,
                eventType = moneyJournalApiResponse.eventType,
                localTransactionId = moneyJournalApiResponse.localTransactionId
            )

        }
    }
}


