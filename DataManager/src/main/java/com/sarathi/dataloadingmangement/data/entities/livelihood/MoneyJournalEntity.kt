package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarathi.dataloadingmangement.MONEY_JOURNAL_TABLE_NAME

@Entity(tableName = MONEY_JOURNAL_TABLE_NAME)
data class MoneyJournalEntity(

    @PrimaryKey
    @ColumnInfo("id")
    val id: Int = 0,
    val transactionId: String,
    val date: Long,
    val transactionDetails: String,
    val refId: String,
    val transactionEventType: String,
    val inFlow: Int,
    val outFlow: Int
)
