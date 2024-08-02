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

    val transactionId: String,
    val date: Long,
    val transactionDetails: String,
    val moneyJournalRefId: String,
    val transactionEventType: String,
    val inFlow: Int,
    val outFlow: Int

)
