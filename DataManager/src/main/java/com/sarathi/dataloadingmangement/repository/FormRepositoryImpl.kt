package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.FormDao
import com.sarathi.dataloadingmangement.data.entities.FormEntity
import javax.inject.Inject

class FormRepositoryImpl @Inject constructor(
    private val formDao: FormDao,
    private val coreSharedPrefs: CoreSharedPrefs
) : IFormRepository {
    override suspend fun saveFromToDB(
        subjectId: Int,
        taskId: Int,
        surveyId: Int,
        missionId: Int,
        activityId: Int,
        referenceId: String,
        subjectType: String
    ) {
        formDao.insertFormData(
            FormEntity.getFormEntity(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                taskId = taskId,
                surveyId = surveyId,
                subjectId = subjectId,
                subjectType = subjectType,
                missionId = missionId,
                activityId = activityId,
                referenceId = referenceId
            )
        )
    }

    override suspend fun deleteForm(
        referenceId: String,
        taskId: Int
    ): Int {
        return formDao.deleteForm(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            referenceId = referenceId,
            taskId = taskId
        )
    }

    override suspend fun getFormSummaryData(): List<FormEntity> {
        return formDao.getFormSummaryData(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            isFormGenerated = false
        )
    }
}