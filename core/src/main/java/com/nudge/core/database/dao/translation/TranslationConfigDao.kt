package com.nudge.core.database.dao.translation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.TRANSLATION_CONFIG_TABLE_NAME
import com.nudge.core.database.entities.traslation.TranslationConfigEntity
import com.nudge.core.helper.TranslationConfigUIModel

@Dao
interface TranslationConfigDao {

    /**
     * Inserts a list of TranslationConfigEntity and replaces any existing entry with the same key.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslationsConfig(translationConfigEntities: List<TranslationConfigEntity>): List<Long>

    /**
     * Fetches a translation configuration entity for a specific user and key.
     */
    @Query("SELECT * FROM $TRANSLATION_CONFIG_TABLE_NAME WHERE userId = :userId AND `key` = :key")
    suspend fun getTranslationAsPerKeyConfigModel(
        userId: String,
        key: String
    ): TranslationConfigEntity?

    /**
     * Fetches all translation configuration entities for a specific user.
     */
    @Query("SELECT * FROM $TRANSLATION_CONFIG_TABLE_NAME WHERE userId = :userId")
    suspend fun getTranslationsConfig(userId: String): List<TranslationConfigEntity>?

    /**
     * Deletes all translation configuration entries for a specific user.
     */
    @Query("DELETE FROM $TRANSLATION_CONFIG_TABLE_NAME WHERE userId = :userId")
    suspend fun deleteTranslationConfigModelForUser(userId: String)

    /**
     * Fetches translation configurations for a user and language code, based on a list of keys.
     */
    @Query("SELECT `key`, value FROM $TRANSLATION_CONFIG_TABLE_NAME WHERE userId = :userId AND languageCode = :languageCode AND `key` IN(:keys)")
    suspend fun getTranslationConfigForUser(
        userId: String,
        keys: List<String>,
        languageCode: String
    ): List<TranslationConfigUIModel>
}
