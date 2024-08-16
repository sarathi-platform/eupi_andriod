package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
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
        if (subjectLivelihoodMappingDao.isSubjectLivelihoodMappingAvailable(
                subjectId = subjectLivelihoodMappingEntity.subjectId!!,
                userId = subjectLivelihoodMappingEntity.userId
            ) == 0
        ) { subjectLivelihoodMappingDao.insertSubjectLivelihoodMapping(subjectLivelihoodMappingEntity)
        } else {
            subjectLivelihoodMappingDao.updatePrimaryLivelihoodForSubject(
                userId = subjectLivelihoodMappingEntity.userId ?: BLANK_STRING,
                subjectId = subjectLivelihoodMappingEntity.subjectId,
                primaryLivelihoodId = subjectLivelihoodMappingEntity.primaryLivelihoodId
            )
            subjectLivelihoodMappingDao.updateSecondaryLivelihoodForSubject(
                userId = subjectLivelihoodMappingEntity.userId ?: BLANK_STRING,
                subjectId = subjectLivelihoodMappingEntity.subjectId,
                secondaryLivelihoodId = subjectLivelihoodMappingEntity.secondaryLivelihoodId
            )

        }
    }

    override fun getUserId() = coreSharedPrefs.getUniqueUserIdentifier()

}