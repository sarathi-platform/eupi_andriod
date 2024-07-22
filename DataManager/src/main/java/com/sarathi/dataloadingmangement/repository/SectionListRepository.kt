package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.SectionStatusEntity
import com.sarathi.dataloadingmangement.model.uiModel.SectionUiModel

interface SectionListRepository {
    fun getSectionListForSurvey(surveyId: Int): List<SectionUiModel>

    suspend fun getSectionStatusForTask(surveyId: Int, taskId: Int): List<SectionStatusEntity>
}