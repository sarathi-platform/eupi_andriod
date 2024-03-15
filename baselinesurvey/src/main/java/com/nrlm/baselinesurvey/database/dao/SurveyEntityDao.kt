package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nrlm.baselinesurvey.SURVEY_TABLE
import com.nrlm.baselinesurvey.database.entity.SurveyEntity

@Dao
interface SurveyEntityDao {

    @Insert
    fun insertSurvey(surveyEntity: SurveyEntity)

    @Query("Select surveyId from $SURVEY_TABLE")
    fun getSurveyIds(): List<Int>

    @Query("Select * from $SURVEY_TABLE where surveyId = :surveyId and languageId = :languageId")
    fun getSurveyDetailForLanguage(surveyId: Int, languageId: Int): SurveyEntity?

    @Query("Select * from $SURVEY_TABLE where surveyName = :surveyName and languageId = :languageId")
    fun getSurveyDetailForLanguage(surveyName: String, languageId: Int): SurveyEntity?

    @Query("Delete from $SURVEY_TABLE where surveyId = :surveyId and languageId = :languageId")
    fun deleteSurveyFroLanguage(surveyId: Int, languageId: Int)

    @Query("Delete from $SURVEY_TABLE")
    fun deleteAllSurvey()

}