package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetJournalEntity

@Dao
interface AssetJournalDao {

    @Insert
    suspend fun insertAssetJournalEntry(assetJournalEntity: AssetJournalEntity)

}