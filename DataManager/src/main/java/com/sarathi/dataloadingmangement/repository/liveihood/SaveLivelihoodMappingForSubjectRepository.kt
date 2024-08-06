package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity

interface SaveLivelihoodMappingForSubjectRepository {

    suspend fun saveSubjectLivelihoodMappingForSubject(subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity)

    fun getUserId(): String

}