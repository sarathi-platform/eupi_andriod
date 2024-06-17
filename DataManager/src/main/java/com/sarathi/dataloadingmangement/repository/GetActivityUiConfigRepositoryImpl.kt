package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity
import javax.inject.Inject


class GetActivityUiConfigRepositoryImpl @Inject constructor(
    private val activityConfigDao: UiConfigDao,
    private val coreSharedPrefs: CoreSharedPrefs
) : IUiConfigRepository {
    override suspend fun getActivityUiConfig(
        missionId: Int,
        activityId: Int
    ): List<UiConfigEntity> {
        return activityConfigDao.getActivityUiConfig(
            missionId,
            activityId,
            coreSharedPrefs.getUniqueUserIdentifier()
        )
    }


}
