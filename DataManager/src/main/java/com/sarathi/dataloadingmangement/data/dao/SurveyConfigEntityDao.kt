package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.SurveyConfigEntity

@Dao
interface SurveyConfigEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUiConfig(surveyConfigEntity: SurveyConfigEntity)

    @Query("SELECT * FROM survey_config_table WHERE missionId = :missionId AND activityId = :activityId AND surveyId = :surveyId AND userId = :uniqueUserIdentifier AND language = :language")
    fun getSurveyConfigForSurvey(
        missionId: Int,
        activityId: Int,
        surveyId: Int,
        uniqueUserIdentifier: String,
        language: String
    ): List<SurveyConfigEntity>

    @Query("SELECT * FROM survey_config_table WHERE missionId = :missionId AND activityId = :activityId AND surveyId = :surveyId AND userId = :uniqueUserIdentifier AND formId = :formId AND language = :language")
    fun getSurveyConfigForFormId(
        missionId: Int,
        activityId: Int,
        surveyId: Int,
        formId: Int,
        uniqueUserIdentifier: String,
        language: String
    ): List<SurveyConfigEntity>

    @Query("SELECT * FROM survey_config_table WHERE missionId = :missionId AND activityId = :activityId AND surveyId = :surveyId AND userId = :uniqueUserIdentifier AND formId in (:formIds) AND language = :language")
    fun getSurveyConfigForFormIds(
        missionId: Int,
        activityId: Int,
        surveyId: Int,
        formIds: List<Int>,
        uniqueUserIdentifier: String,
        language: String
    ): List<SurveyConfigEntity>

    @Query("Delete from survey_config_table where  missionId=:missionId and activityId=:activityId and userId=:uniqueUserIdentifier")
    fun deleteSurveyConfig(
        missionId: Int,
        activityId: Int,
        uniqueUserIdentifier: String
    )

}