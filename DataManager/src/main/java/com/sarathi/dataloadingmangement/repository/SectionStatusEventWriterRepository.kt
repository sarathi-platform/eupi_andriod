package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.SurveyEntity
import com.sarathi.dataloadingmangement.model.events.SectionStatusUpdateEventDto

interface SectionStatusEventWriterRepository {
    suspend fun writeSectionStatusEvent(
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        status: String
    ): SectionStatusUpdateEventDto

    suspend fun getSurveyForId(surveyId: Int): SurveyEntity?
}