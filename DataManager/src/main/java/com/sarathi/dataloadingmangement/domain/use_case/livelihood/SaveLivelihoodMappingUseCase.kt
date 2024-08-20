package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.repository.liveihood.SaveLivelihoodMappingForSubjectRepository
import javax.inject.Inject

class SaveLivelihoodMappingUseCase @Inject constructor(
    private val saveLivelihoodMappingForSubjectRepository: SaveLivelihoodMappingForSubjectRepository
) {

    suspend operator fun invoke(subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity) {
        saveLivelihoodMappingForSubjectRepository.saveSubjectLivelihoodMappingForSubject(
            subjectLivelihoodMappingEntity
        )
    }
    suspend  fun saveAndUpdateSubjectLivelihoodMappingForSubject(subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity) {
        saveLivelihoodMappingForSubjectRepository.saveAndUpdateSubjectLivelihoodMappingForSubject(
            subjectLivelihoodMappingEntity
        )
    }
    fun getUserId() = saveLivelihoodMappingForSubjectRepository.getUserId()

}