package com.sarathi.smallgroupmodule.ui.didiTab.domain.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import javax.inject.Inject

class FetchSmallGroupListFromDbRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val smallGroupEntityDao: SmallGroupDidiMappingDao
) : FetchSmallGroupListFromDbRepository {

    override suspend fun getSmallGroupListForUser(uniqueUserId: String): List<SmallGroupSubTabUiModel> {
        return smallGroupEntityDao.getAllMappingForUserByDate(userId = uniqueUserId)
    }

    override fun getUniqueUserId() = coreSharedPrefs.getUniqueUserIdentifier()
    /*"Ultra Poor change maker (UPCM)_6789543210"*/

}