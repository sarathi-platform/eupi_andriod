package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.SurveyConfigEntity

interface GetSurveyConfigFromDbRepository {

    suspend fun getSurveyConfig(
        missionId: Int,
        activityId: Int,
        surveyId: Int
    ): List<SurveyConfigEntity>

    suspend fun getSurveyConfig(
        missionId: Int,
        activityId: Int,
        surveyId: Int,
        formId: Int
    ): List<SurveyConfigEntity>

}