package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.LIVELIHOOD_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.LivelihoodEntity


@Dao
interface LivelihoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLivelihood(livelihood: LivelihoodEntity)

    @Query("DELETE FROM $LIVELIHOOD_TABLE_NAME where userId=:userId ")
    fun deleteActivityTaskForUser(userId: String)

    @Query("SELECT * FROM $LIVELIHOOD_TABLE_NAME where userId=:userId and isActive=1")
    suspend fun getAllLivelihoodTask(userId: String): List<LivelihoodEntity>
}