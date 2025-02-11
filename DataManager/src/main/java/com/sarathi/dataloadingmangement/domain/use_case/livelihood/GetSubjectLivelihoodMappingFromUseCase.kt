package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.SubjectEntityWithLivelihoodMappingUiModel
import com.sarathi.dataloadingmangement.repository.liveihood.GetLivelihoodMappingForSubjectFromDbRepository
import javax.inject.Inject

class GetSubjectLivelihoodMappingFromUseCase @Inject constructor(
    private val getLivelihoodMappingForSubjectFromDbRepository: GetLivelihoodMappingForSubjectFromDbRepository
) {

    suspend operator fun invoke(subjectId: Int): List<SubjectLivelihoodMappingEntity> {
        return getLivelihoodMappingForSubjectFromDbRepository.getLivelihoodMappingForSubject(
            subjectId
        )
    }
    suspend  fun getLivelihoodMappingForSubject(subjectId: List<Int>): List<SubjectLivelihoodMappingEntity>{
        return getLivelihoodMappingForSubjectFromDbRepository.getLivelihoodMappingForSubjects(
            subjectId
        )
    }
    suspend fun getLivelihoodForDidi(subjectId: Int): List<SubjectLivelihoodMappingEntity?> {
        return getLivelihoodMappingForSubjectFromDbRepository.getLivelihoodMappingForSubject(
            subjectId
        )
    }

    suspend fun getSubjectEntityWithLivelihoodMappingUiModelListForSubject(subjectId: Int): List<SubjectEntityWithLivelihoodMappingUiModel> {
        return getLivelihoodMappingForSubjectFromDbRepository.getSubjectEntityWithLivelihoodMappingUiModelListForSubject(
            subjectId
        )
    }
}