package com.nudge.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.CASTE_TABLE
import com.nudge.core.database.entities.CasteEntity

@Dao
interface CasteListDao {

    @Query("SELECT * FROM $CASTE_TABLE")
    fun getAllCaste(): List<CasteEntity>

    @Query("SELECT * FROM $CASTE_TABLE where languageId = :languageId order by id")
    fun getAllCasteForLanguage(languageId: Int): List<CasteEntity>

    @Query("Select * FROM $CASTE_TABLE where id = :id AND languageId=:languageId")
    fun getCaste(id: Int, languageId: Int): CasteEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCaste(caste: CasteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(castes: List<CasteEntity>)

    @Query("DELETE from $CASTE_TABLE")
    fun deleteCasteTable()


    @Query("DELETE from $CASTE_TABLE where languageId =:languageId")
    fun deleteCasteTableForLanguage(languageId: Int)
}