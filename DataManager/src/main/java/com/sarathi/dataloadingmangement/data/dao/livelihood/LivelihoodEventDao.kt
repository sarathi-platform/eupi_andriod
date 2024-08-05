package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEventEntity


@Dao
interface LivelihoodEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLivelihoodEvent(livelihoodEventEntity: LivelihoodEventEntity)
}