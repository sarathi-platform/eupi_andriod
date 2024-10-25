//package com.sarathi.dataloadingmangement.data.dao
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.sarathi.dataloadingmangement.LANGUAGE_TABLE_NAME
//import com.sarathi.dataloadingmangement.data.entities.LanguageEntity
//
//@Dao
//interface LanguageDao {
//    @Query("SELECT * FROM $LANGUAGE_TABLE_NAME ORDER BY orderNumber ASC")
//    fun getAllLanguages(): List<LanguageEntity>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAll(villages: List<LanguageEntity>)
//}