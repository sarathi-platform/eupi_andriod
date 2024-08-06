package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.SubjectLivelihoodMappingDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import javax.inject.Inject

class GetLivelihoodMappingForSubjectFromDbRepositoryImpl @Inject constructor(
    val subjectLivelihoodMappingDao: SubjectLivelihoodMappingDao,
    val coreSharedPrefs: CoreSharedPrefs
) : GetLivelihoodMappingForSubjectFromDbRepository {

    override suspend fun getLivelihoodMappingForSubject(subjectId: Int): SubjectLivelihoodMappingEntity? {
        return subjectLivelihoodMappingDao.getSubjectLivelihoodMappingAvailable(
            subjectId = subjectId,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

}