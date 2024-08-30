package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.SubjectLivelihoodMappingDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.enums.LivelihoodTypeEnum
import javax.inject.Inject

class GetLivelihoodMappingForSubjectFromDbRepositoryImpl @Inject constructor(
    val subjectLivelihoodMappingDao: SubjectLivelihoodMappingDao,
    val coreSharedPrefs: CoreSharedPrefs
) : GetLivelihoodMappingForSubjectFromDbRepository {

    override suspend fun getLivelihoodMappingForSubject(subjectId: Int): List<SubjectLivelihoodMappingEntity> {
        return subjectLivelihoodMappingDao.getSubjectLivelihoodMappingAvailable(
            subjectId = subjectId,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            type= listOf(LivelihoodTypeEnum.PRIMARY.typeId,LivelihoodTypeEnum.SECONDARY.typeId)
        )
    }

    override suspend fun getLivelihoodForSubject(subjectId: Int): SubjectLivelihoodMappingEntity? {
        TODO("Not yet implemented")
    }

    override suspend fun getLivelihoodMappingForSubjects(subjectId: List<Int>): List<SubjectLivelihoodMappingEntity> {
        return  subjectLivelihoodMappingDao.getSubjectsLivelihoodMapping(
            subjectIds=subjectId,
            userId = coreSharedPrefs.getUniqueUserIdentifier()


        )
    }

}