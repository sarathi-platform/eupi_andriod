package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodLanguageReferenceEntity


@Dao
interface LivelihoodLanguageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLivelihoodLanguage(languageEntity: List<LivelihoodLanguageReferenceEntity>)

}