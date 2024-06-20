package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigModel
import javax.inject.Inject


class GetActivityUiConfigRepositoryImpl @Inject constructor(
    private val activityConfigDao: UiConfigDao,
    private val coreSharedPrefs: CoreSharedPrefs
) : IUiConfigRepository {
    override suspend fun getActivityUiConfig(
        missionId: Int,
        activityId: Int
    ): List<UiConfigModel> {
        return activityConfigDao.getActivityUiConfig(
            missionId = missionId,
            activityId = activityId,
            languageCode = coreSharedPrefs.getAppLanguage(),
            uniqueUserIdentifier = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }


}
