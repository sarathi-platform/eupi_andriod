package com.nudge.core.database.dao.language

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.LANGUAGE_TABLE_NAME
import com.nudge.core.database.entities.language.LanguageEntity

@Dao
interface LanguageListDao {
    @Query("SELECT * FROM $LANGUAGE_TABLE_NAME ORDER BY orderNumber ASC")
    fun getAllLanguages(): List<LanguageEntity>

    @Query("Select * FROM $LANGUAGE_TABLE_NAME where id = :id")
    fun getLanguage(id: Int): LanguageEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLanguage(language: LanguageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(villages: List<LanguageEntity>)

    @Query("DELETE FROM $LANGUAGE_TABLE_NAME")
    suspend fun deleteAllLanguage()
}