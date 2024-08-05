package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity

interface GetLivelihoodMappingForSubjectFromDbRepository {

    suspend fun getLivelihoodMappingForSubject(subjectId: Int): SubjectLivelihoodMappingEntity?

}