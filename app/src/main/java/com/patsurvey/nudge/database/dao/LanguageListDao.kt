package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.utils.LANGUAGE_TABLE_NAME

@Dao
interface LanguageListDao {
    @Query("SELECT * FROM $LANGUAGE_TABLE_NAME")
    fun getAllLanguages(): List<LanguageEntity>

    @Query("Select * FROM $LANGUAGE_TABLE_NAME where id = :id")
    fun getLanguage(id: Int): LanguageEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLanguage(language: LanguageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(villages: List<LanguageEntity>)
}