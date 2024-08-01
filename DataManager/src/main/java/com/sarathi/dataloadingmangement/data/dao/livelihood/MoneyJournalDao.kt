package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity

@Dao
interface MoneyJournalDao {

    @Insert
    suspend fun insetMoneyJournalEntry(moneyJournalEntity: MoneyJournalEntity)

}