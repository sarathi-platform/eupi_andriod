package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity

interface GetLivelihoodMappingForSubjectFromDbRepository {

    suspend fun getLivelihoodMappingForSubject(subjectId: Int): List<SubjectLivelihoodMappingEntity>
    suspend fun getLivelihoodForSubject(subjectId: Int): SubjectLivelihoodMappingEntity?
    suspend fun getLivelihoodMappingForSubjects(subjectId: List<Int>): List<SubjectLivelihoodMappingEntity>



}