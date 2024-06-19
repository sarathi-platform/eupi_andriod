package com.sarathi.missionactivitytask.ui.grantTask.domain.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigModel
import com.sarathi.dataloadingmangement.repository.IUiConfigRepository
import com.sarathi.missionactivitytask.domain.repository.BaseRepository
import javax.inject.Inject


class GetActivityUiConfigRepositoryImpl @Inject constructor(
    private val activityConfigDao: UiConfigDao,
    private val coreSharedPrefs: CoreSharedPrefs
) :
    BaseRepository(),
    IUiConfigRepository {
    override suspend fun getActivityUiConfig(
        missionId: Int,
        activityId: Int
    ): List<UiConfigModel> {
        return activityConfigDao.getActivityUiConfig(
            missionId,
            activityId,
            coreSharedPrefs.getUniqueUserIdentifier(),
            coreSharedPrefs.getAppLanguage()
        )
    }


}
