package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.SectionStatusEntityDao
import com.sarathi.dataloadingmangement.data.entities.SectionStatusEntity
import javax.inject.Inject

class SectionStatusUpdateRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val sectionStatusEntityDao: SectionStatusEntityDao
) : SectionStatusUpdateRepository {

    override suspend fun addOrUpdateSectionStatusForTask(
        missionId: Int,
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        status: String
    ) {
        if (isStatusAvailableForTaskSection(missionId, surveyId, sectionId, taskId)) {
            updateSectionStatusForTask(missionId, surveyId, sectionId, taskId, status)
        } else {
            addSectionStatusForTask(missionId, surveyId, sectionId, taskId, status)
        }
    }

    override suspend fun addSectionStatusForTask(
        missionId: Int,
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        status: String
    ) {
        sectionStatusEntityDao.addSectionStatus(
            SectionStatusEntity(
                id = 0,
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                missionId = missionId,
                surveyId = surveyId,
                sectionId = sectionId,
                taskId = taskId,
                sectionStatus = status
            )
        )
    }

    override suspend fun updateSectionStatusForTask(
        missionId: Int,
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        status: String
    ) {
        sectionStatusEntityDao.updateSectionStatusForTask(
            missionId = missionId,
            surveyId = surveyId,
            taskId = taskId,
            sectionId = sectionId,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            sectionStatus = status
        )
    }

    override suspend fun isStatusAvailableForTaskSection(
        missionId: Int,
        surveyId: Int,
        sectionId: Int,
        taskId: Int
    ): Boolean {
        return sectionStatusEntityDao.isStatusAvailableForSection(
            missionId = missionId,
            surveyId = surveyId,
            sectionId = sectionId,
            taskId = taskId,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        ) > 0
    }

}