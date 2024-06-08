package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.GrantConfigDao
import com.sarathi.dataloadingmangement.data.entities.GrantConfigEntity
import javax.inject.Inject


class GrantConfigRepositoryImpl @Inject constructor(
    private val grantConfigDao: GrantConfigDao,
    val coreSharedPrefs: CoreSharedPrefs
) : IGrantConfigRepository {
    override suspend fun getGrantConfig(activityConfigId: Int): List<GrantConfigEntity> {
        return grantConfigDao.getGrantConfig(activityConfigId)
    }

    override suspend fun getGrantComponentDTO(surveyId: Int, activityConfigId: Int): String {
        return grantConfigDao.getGrantComponent(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            surveyId = surveyId,
            activityConfigId = activityConfigId
        )
    }


}
