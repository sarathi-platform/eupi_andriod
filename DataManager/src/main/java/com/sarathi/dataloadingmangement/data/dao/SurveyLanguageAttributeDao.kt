package com.sarathi.dataloadingmangement.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.SurveyLanguageAttributeEntity


@Dao
interface SurveyLanguageAttributeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSurveyLanguageAttribute(languageAttributes: SurveyLanguageAttributeEntity)

    @Query(" delete from survey_language_attribute_table where referenceId =:referenceId and referenceId =:referenceType")
    fun deleteSurveyLanguageAttribute(referenceId: Int, referenceType: String)


}