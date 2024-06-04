package com.sarathi.missionactivitytask.ui.grantTask.domain.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.model.uiModel.ActivityConfigUiModel
import com.sarathi.missionactivitytask.domain.repository.BaseRepository
import javax.inject.Inject


class GetActivityConfigRepositoryImpl @Inject constructor(
    private val activityConfigDao: ActivityConfigDao,
    private val coreSharedPrefs: CoreSharedPrefs
) :
    BaseRepository(),
    IActivityConfigRepository {

    override suspend fun getActivityConfig(activityId: Int): ActivityConfigUiModel {
        return activityConfigDao.getActivityConfigWithSection(
            activityId = activityId,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            languageId = coreSharedPrefs.getAppLanguage()
        )
    }


}
