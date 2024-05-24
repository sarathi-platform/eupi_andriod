package com.sarathi.smallgroupmodule.ui.didiTab.domain.repository

import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import javax.inject.Inject

class FetchSmallGroupDetailsFromDbRepositoryImpl @Inject constructor(
//    private val corePrefRepo: CorePrefRepo,
    private val smallGroupEntityDao: SubjectEntityDao
) : FetchSmallGroupDetailsFromDbRepository {


}