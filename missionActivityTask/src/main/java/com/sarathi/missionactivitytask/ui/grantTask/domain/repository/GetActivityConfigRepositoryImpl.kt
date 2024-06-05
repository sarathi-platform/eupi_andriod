package com.sarathi.missionactivitytask.ui.grantTask.domain.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity
import com.sarathi.missionactivitytask.domain.repository.BaseRepository
import javax.inject.Inject


class GetActivityConfigRepositoryImpl @Inject constructor(
    private val activityConfigDao: UiConfigDao,
    private val coreSharedPrefs: CoreSharedPrefs
) :
    BaseRepository(),
    IUiConfigRepository {
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
