package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.SubjectLivelihoodEventMappingDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import javax.inject.Inject

class SubjectLivelihoodEventMappingRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val subjectLivelihoodEventMappingDao: SubjectLivelihoodEventMappingDao
) : ISubjectLivelihoodEventMapping {

    override suspend fun getSubjectLivelihoodEventMappingListFromDb(
        subjectId: Int
    ): List<SubjectLivelihoodEventMappingEntity>? {
        return subjectLivelihoodEventMappingDao.getSubjectLivelihoodEventMappingAvailable(
            subjectId = subjectId,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

    override suspend fun saveSubjectLivelihoodEventMapping(subjectLivelihoodEventMappingEntity: SubjectLivelihoodEventMappingEntity) {
        subjectLivelihoodEventMappingDao.insertSubjectLivelihoodEventMapping(
            subjectLivelihoodEventMappingEntity = subjectLivelihoodEventMappingEntity
        )
    }
}