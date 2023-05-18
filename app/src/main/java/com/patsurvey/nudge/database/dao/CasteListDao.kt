package com.patsurvey.nudge.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.utils.CASTE_TABLE

@Dao
interface CasteListDao {

    @Query("SELECT * FROM $CASTE_TABLE")
    fun getAllCaste(): List<CasteEntity>

    @Query("SELECT * FROM $CASTE_TABLE where languageId = :languageId")
    fun getAllCasteForLanguage(languageId:Int): List<CasteEntity>

    @Query("Select * FROM $CASTE_TABLE where id = :id")
    fun getCaste(id: Int): CasteEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCaste(caste: CasteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(castes: List<CasteEntity>)

    @Query("DELETE from $CASTE_TABLE")
    fun deleteCasteTable()
}