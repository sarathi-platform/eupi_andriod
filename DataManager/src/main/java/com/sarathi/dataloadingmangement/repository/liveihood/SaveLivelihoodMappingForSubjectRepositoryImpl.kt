package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.dao.livelihood.SubjectLivelihoodMappingDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.enums.LivelihoodTypeEnum
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
                subjectId = subjectLivelihoodMappingEntity.subjectId,
                userId = subjectLivelihoodMappingEntity.userId,
               type = subjectLivelihoodMappingEntity.type
            ) == 0
        ) {
            subjectLivelihoodMappingDao.insertSubjectLivelihoodMapping(subjectLivelihoodMappingEntity)
        }
        else {
            subjectLivelihoodMappingDao.softDeleteLivelihoodForSubject(
                    userId = subjectLivelihoodMappingEntity.userId ?: BLANK_STRING,
                    subjectId = subjectLivelihoodMappingEntity.subjectId,
                    type = subjectLivelihoodMappingEntity.type,
                    status = 2
                )
                subjectLivelihoodMappingDao.insertSubjectLivelihoodMapping(
                    subjectLivelihoodMappingEntity
                )

        }
    }
    override suspend fun saveAndUpdateSubjectLivelihoodMappingForSubject(primaryLivelihoodId: Int,subjectId:Int,secondaryLivelihoodId:Int) {

        val subjectLivelihoodPrimaryMappingEntity =
            SubjectLivelihoodMappingEntity.getSubjectLivelihoodMappingEntity(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                subjectId = subjectId,
                livelihoodId = primaryLivelihoodId,
                type = LivelihoodTypeEnum.PRIMARY.typeId,
                primaryLivelihoodId = 1,
                secondaryLivelihoodId = 1,
                status = 1
            )
        val subjectLivelihoodSecondaryMappingEntity =
            SubjectLivelihoodMappingEntity.getSubjectLivelihoodMappingEntity(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                subjectId = subjectId,
                livelihoodId = secondaryLivelihoodId,
                type = LivelihoodTypeEnum.SECONDARY.typeId,
                primaryLivelihoodId = 1,
                secondaryLivelihoodId = 1,
                status = 1
            )
        saveAndUpdateSubjectLivelihoodMappingForSubject(subjectLivelihoodPrimaryMappingEntity)
        saveAndUpdateSubjectLivelihoodMappingForSubject(subjectLivelihoodSecondaryMappingEntity)
    }

    override fun getUserId() = coreSharedPrefs.getUniqueUserIdentifier()

}