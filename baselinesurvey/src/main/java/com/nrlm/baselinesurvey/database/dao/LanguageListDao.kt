//package com.nrlm.baselinesurvey.database.dao
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.nrlm.baselinesurvey.LANGUAGE_TABLE_NAME
//import com.nrlm.baselinesurvey.database.entity.LanguageEntity
//
////@Dao
////interface LanguageListDao {
//////    @Query("SELECT * FROM $LANGUAGE_TABLE_NAME ORDER BY orderNumber ASC")
//////    fun getAllLanguages(): List<LanguageEntity>
//////
//////    @Query("Select * FROM $LANGUAGE_TABLE_NAME where id = :id")
//////    fun getLanguage(id: Int): LanguageEntity
//////
//////    @Insert(onConflict = OnConflictStrategy.REPLACE)
//////    fun insertLanguage(language: LanguageEntity)
//////
//////    @Insert(onConflict = OnConflictStrategy.REPLACE)
//////    fun insertAll(villages: List<LanguageEntity>)
////}