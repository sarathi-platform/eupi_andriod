package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.LIVELIHOOD_EVENT_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEventEntity


@Dao
interface LivelihoodEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLivelihood(livelihoodEventEntity: LivelihoodEventEntity)

    @Query("DELETE FROM $LIVELIHOOD_EVENT_TABLE_NAME where userId=:userId ")
    fun deleteProductForUser(userId: String)
}