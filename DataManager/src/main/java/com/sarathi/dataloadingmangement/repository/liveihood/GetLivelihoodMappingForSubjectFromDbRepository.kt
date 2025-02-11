package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.SubjectEntityWithLivelihoodMappingUiModel

interface GetLivelihoodMappingForSubjectFromDbRepository {

    suspend fun getLivelihoodMappingForSubject(subjectId: Int): List<SubjectLivelihoodMappingEntity>
    suspend fun getLivelihoodForSubject(subjectId: Int): SubjectLivelihoodMappingEntity?
    suspend fun getLivelihoodMappingForSubjects(subjectId: List<Int>): List<SubjectLivelihoodMappingEntity>
    suspend fun getSubjectEntityWithLivelihoodMappingUiModelListForSubject(subjectId: Int): List<SubjectEntityWithLivelihoodMappingUiModel>


}