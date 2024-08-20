package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodLanguageReferenceEntity


@Dao
interface LivelihoodLanguageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLivelihoodLanguage(languageEntity: List<LivelihoodLanguageReferenceEntity>)

    @Query("DELETE from livelihood_language_reference_table where userId = :userId")
    fun deleteLivelihoodLanguageForUser(userId: String)
}