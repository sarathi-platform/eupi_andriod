package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarathi.dataloadingmangement.MONEY_JOURNAL_TABLE_NAME

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
    val status: Int
)

