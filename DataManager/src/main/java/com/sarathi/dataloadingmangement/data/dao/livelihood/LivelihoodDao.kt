package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.LIVELIHOOD_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEntity


@Dao
interface LivelihoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLivelihood(livelihood: LivelihoodEntity)

    @Query("DELETE FROM $LIVELIHOOD_TABLE_NAME where userId=:userId ")
    fun deleteLivelihoodForUser(userId: String)
}