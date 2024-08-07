package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.SectionStatusEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyEntity
import com.sarathi.dataloadingmangement.model.uiModel.SectionUiModel

interface SectionListRepository {
    suspend fun getSectionListForSurvey(surveyId: Int): List<SectionUiModel>

    suspend fun getSectionStatusForTask(
        missionId: Int,
        surveyId: Int,
        taskId: Int
    ): List<SectionStatusEntity>

    suspend fun getSurveyEntity(surveyId: Int): SurveyEntity?
}