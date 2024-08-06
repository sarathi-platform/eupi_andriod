package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity

interface ISubjectLivelihoodEventMapping {
    suspend fun getSubjectLivelihoodEventMappingListFromDb(
        subjectId: Int
    ): List<SubjectLivelihoodEventMappingEntity>?

    suspend fun saveSubjectLivelihoodEventMapping(subjectLivelihoodEventMappingEntity: SubjectLivelihoodEventMappingEntity)

}