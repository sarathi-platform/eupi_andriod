package com.sarathi.smallgroupmodule.ui.didiTab.domain.repository

import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.dao.smallGroup.SmallGroupDidiMappingDao
import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import javax.inject.Inject

class FetchDidiDetailsFromDbRepositoryImpl @Inject constructor(
//    private val prefRepo: CorePrefRepo,
    private val subjectEntityDao: SubjectEntityDao,
    private val smallGroupDidiMappingDao: SmallGroupDidiMappingDao
) : FetchDidiDetailsFromDbRepository {


    override suspend fun getSubjectListForUser(userId: String): List<SubjectEntity> {
        return subjectEntityDao.getAllSubjects(userId)
    }

    override suspend fun getSubjectListForSmallGroup(
        userId: String,
        smallGroupId: Int
    ): List<SubjectEntity> {
        val smallGroupMapping =
            smallGroupDidiMappingDao.getAllMappingForSmallGroup(userId, smallGroupId)
        return subjectEntityDao.getAllSubjectForIds(userId, smallGroupMapping.map { it.didiId })
    }

    override suspend fun getSubjectListCount(userId: String): Int {
        return subjectEntityDao.getCountForSubject(userId)
    }

    override fun getUniqueUserId() = /*prefRepo.getUniqueUserIdentifier()*/
        "Ultra Poor change maker (UPCM)_6789543210" //TODO Temp code remove after integrating corePref

}