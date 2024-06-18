package com.sarathi.smallgroupmodule.ui.smallGroupAttendance.domain.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import javax.inject.Inject

class FetchDidiListForSmallGroupFromDbRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val smallGroupDidiMappingDao: SmallGroupDidiMappingDao,
    private val subjectEntityDao: SubjectEntityDao
) : FetchDidiListForSmallGroupFromDbRepository {


    override suspend fun getDidiDetailsForSmallGroup(smallGroupId: Int): List<SubjectEntity> {
        val uniqueUserId = coreSharedPrefs.getUniqueUserIdentifier()
        val subjectIds = smallGroupDidiMappingDao.getAllLatestMappingForSmallGroup(
            userId = uniqueUserId,
            smallGroupId = smallGroupId
        ).map { it.didiId }
        return subjectEntityDao.getAllSubjectForIds(uniqueUserId, subjectIds)
    }


}