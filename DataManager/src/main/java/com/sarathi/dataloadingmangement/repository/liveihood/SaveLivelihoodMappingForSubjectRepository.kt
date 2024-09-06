package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity

interface SaveLivelihoodMappingForSubjectRepository {

    suspend fun saveSubjectLivelihoodMappingForSubject(subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity)

    suspend fun saveAndUpdateSubjectLivelihoodMappingForSubject(subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity)
    suspend fun saveAndUpdateSubjectLivelihoodMappingForSubject(
        primaryLivelihoodId: Int,
        subjectId: Int,
        secondaryLivelihoodId: Int
    )

    fun getUserId(): String

}