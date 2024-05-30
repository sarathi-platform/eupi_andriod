package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.SURVEY_TABLE
import com.sarathi.dataloadingmangement.data.entities.SurveyEntity

@Dao
interface SurveyEntityDao {

    @Insert
    fun insertSurvey(surveyEntity: SurveyEntity)

    @Query("Select DISTINCT surveyId from $SURVEY_TABLE where userId=:userId")
    fun getSurveyIds(userId: String): List<Int>

    @Query("Select * from $SURVEY_TABLE where userId=:userId and surveyId = :surveyId and languageId = :languageId")
    fun getSurveyDetailForLanguage(userId: String, surveyId: Int, languageId: String): SurveyEntity?

    @Query("Select * from $SURVEY_TABLE where userId=:userId and surveyName = :surveyName and languageId = :languageId")
    fun getSurveyDetailForLanguage(
        userId: String,
        surveyName: String,
        languageId: String
    ): SurveyEntity?

    @Query("Delete from $SURVEY_TABLE where userId=:userId and surveyId = :surveyId")
    fun deleteSurveyFroLanguage(userId: String, surveyId: Int)

    @Query("Delete from $SURVEY_TABLE where userId=:userId")
    fun deleteAllSurvey(userId: String)

}