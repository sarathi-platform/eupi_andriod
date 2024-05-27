package com.sarathi.smallgroupmodule.ui.didiTab.domain.repository

import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import javax.inject.Inject

class FetchSmallGroupDetailsFromDbRepositoryImpl @Inject constructor(
//    private val corePrefRepo: CorePrefRepo,
    private val smallGroupEntityDao: SmallGroupDidiMappingDao
) : FetchSmallGroupDetailsFromDbRepository {

    override suspend fun getSmallGroupListForUser(uniqueUserId: String): List<SmallGroupSubTabUiModel> {
        return smallGroupEntityDao.getAllMappingForUserGroupBySmallGroupId(userId = uniqueUserId)
    }

    override fun getUniqueUserId() = /*prefRepo.getUniqueUserIdentifier()*/
        "Ultra Poor change maker (UPCM)_6789543210" //TODO Temp code remove after integrating corePref

}