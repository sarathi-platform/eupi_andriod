package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.SubjectLivelihoodMappingDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import javax.inject.Inject

class SaveLivelihoodMappingForSubjectRepositoryImpl @Inject constructor(
    private val subjectLivelihoodMappingDao: SubjectLivelihoodMappingDao,
    private val coreSharedPrefs: CoreSharedPrefs
) : SaveLivelihoodMappingForSubjectRepository {

    override suspend fun saveSubjectLivelihoodMappingForSubject(subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity) {
        subjectLivelihoodMappingDao.insertSubjectLivelihoodMapping(subjectLivelihoodMappingEntity)

    }
    override suspend fun saveAndUpdateSubjectLivelihoodMappingForSubject(subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity) {
        subjectLivelihoodMappingDao.insertOrModifyLivelihoodMapping(subjectLivelihoodMappingEntity)

    }
    override fun getUserId() = coreSharedPrefs.getUniqueUserIdentifier()

}