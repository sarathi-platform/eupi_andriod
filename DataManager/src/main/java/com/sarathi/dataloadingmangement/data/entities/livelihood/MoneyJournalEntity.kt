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
    val transactionFlow: String,
    val transactionType: String,
    val transactionAmount: Double,
    val referenceId: Int,
    val referenceType: String,
    val subjectId: Int,
    val subjectType: String,
    val status: Int,
    val modifiedDate: Long,
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
            transactionFlow: String
        ): MoneyJournalEntity {
            return MoneyJournalEntity(
                id = 0,
                userId = userId,
                transactionAmount = amount.toDouble(),
                transactionDate = date.toInMillisec(DD_MMM_YYYY_FORMAT),
                transactionId = referenceId,
                referenceId = grantId,
                referenceType = grantType,
                subjectType = subjectType,
                subjectId = subjectId,
                transactionDetails = particulars,
                transactionFlow = transactionFlow,
                status = 1,
                transactionType = grantType,
                modifiedDate = System.currentTimeMillis()
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
                modifiedDate = moneyJournalApiResponse.modifiedDate

            )

        }
    }
}


