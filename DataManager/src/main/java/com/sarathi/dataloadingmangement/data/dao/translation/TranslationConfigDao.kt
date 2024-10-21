package com.sarathi.dataloadingmangement.data.dao.translation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.TRANSLATION_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.traslation.TranslationConfigEntity

@Dao
interface TranslationConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTranslationConfig(translationConfigEntity: TranslationConfigEntity): Long

    @Query("Select  * from $TRANSLATION_CONFIG_TABLE_NAME where userId=:userId and key =:key")
    fun getTranslationConfigModel(userId: String, key: String): TranslationConfigEntity

    @Query("Delete from $TRANSLATION_CONFIG_TABLE_NAME where userId=:userId")
    fun deleteTranslationConfigModelForUser(userId: String)

}