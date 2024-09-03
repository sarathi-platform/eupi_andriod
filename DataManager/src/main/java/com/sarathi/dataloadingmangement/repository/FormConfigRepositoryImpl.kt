package com.sarathi.dataloadingmangement.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.FormUiConfigDao
import com.sarathi.dataloadingmangement.data.entities.FormUiConfigEntity
import javax.inject.Inject

class FormConfigRepositoryImpl @Inject constructor(
    private val formUiConfigDao: FormUiConfigDao,
    private val coreSharedPrefs: CoreSharedPrefs
) : IFormConfigRepository {

    override suspend fun getFormUiConfig(
        activityId: Int,
        missionId: Int
    ): List<FormUiConfigEntity> {
        return formUiConfigDao.getActivityFormUiConfig(
            uniqueUserIdentifier = coreSharedPrefs.getUniqueUserIdentifier(),
            activityId = activityId,
            missionId = missionId
        )
    }

    override suspend fun getFormUiValue(activityId: Int, missionId: Int, key: String): String {
        return formUiConfigDao.getFormConfigForKey(
            uniqueUserIdentifier = coreSharedPrefs.getUniqueUserIdentifier(),
            activityId = activityId,
            missionId = missionId,
            key = key
        ) ?: BLANK_STRING
    }


}
