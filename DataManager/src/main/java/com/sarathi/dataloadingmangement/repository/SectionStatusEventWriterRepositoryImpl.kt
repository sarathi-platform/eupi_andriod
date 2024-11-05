package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.SectionStatusEntityDao
import com.sarathi.dataloadingmangement.data.dao.SurveyEntityDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.entities.SurveyEntity
import com.sarathi.dataloadingmangement.model.events.SectionStatusUpdateEventDto
import javax.inject.Inject

class SectionStatusEventWriterRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val taskDao: TaskDao,
    private val sectionStatusEntityDao: SectionStatusEntityDao,
    private val surveyEntityDao: SurveyEntityDao
) : SectionStatusEventWriterRepository {
    override suspend fun writeSectionStatusEvent(
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        status: String
    ): SectionStatusUpdateEventDto {
        val task = taskDao.getTaskById(coreSharedPrefs.getUniqueUserIdentifier(), taskId)
        return SectionStatusUpdateEventDto(
            surveyId = surveyId,
            sectionId = sectionId,
            didiId = task.subjectId,
            sectionStatus = status,
            localTaskId = task.localTaskId
        )
    }

    override suspend fun getSurveyForId(surveyId: Int): SurveyEntity? {
        return surveyEntityDao.getSurveyDetailForLanguage(
            coreSharedPrefs.getUniqueUserIdentifier(),
            surveyId
        )
    }


}