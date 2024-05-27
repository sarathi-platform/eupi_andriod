package com.sarathi.missionactivitytask.domain.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel
import javax.inject.Inject

class GetMissionRepositoryImpl @Inject constructor(
    val missionDao: MissionDao,
    private val coreSharedPreferences: CoreSharedPrefs
) : BaseRepository(),
    IMissionRepository {

    override suspend fun getAllActiveMission(): List<MissionUiModel> =
        missionDao.getMissions(coreSharedPreferences.getUniqueUserIdentifier(), "en")

}
