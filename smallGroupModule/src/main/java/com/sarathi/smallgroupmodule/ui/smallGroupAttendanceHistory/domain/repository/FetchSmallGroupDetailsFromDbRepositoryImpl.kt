package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import javax.inject.Inject

class FetchSmallGroupDetailsFromDbRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val smallGroupDidiMappingDao: SmallGroupDidiMappingDao
) : FetchSmallGroupDetailsFromDbRepository {
    override fun getUniqueUserId() = coreSharedPrefs.getUniqueUserIdentifier()

    override suspend fun getSmallGroupDetailsForUserAndSmallGroupId(
        uniqueUserId: String,
        smallGroupId: Int
    ): SmallGroupSubTabUiModel {
        return smallGroupDidiMappingDao.getAllMappingForUserByDateAndSmallGroupId(
            uniqueUserId,
            smallGroupId
        )
    }
}