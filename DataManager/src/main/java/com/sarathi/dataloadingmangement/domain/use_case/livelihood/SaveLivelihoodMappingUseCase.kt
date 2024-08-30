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
    suspend  fun saveAndUpdateSubjectLivelihoodMappingPrimaryForSubject(subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity) {
        saveLivelihoodMappingForSubjectRepository.saveAndUpdateSubjectLivelihoodMappingForPrimarySubject(
            subjectLivelihoodMappingEntity
        )
    }
    suspend  fun saveAndUpdateSubjectLivelihoodMappingSecondaryForSubject(subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity) {
        saveLivelihoodMappingForSubjectRepository.saveAndUpdateSubjectLivelihoodMappingForSecondarySubject(
            subjectLivelihoodMappingEntity
        )
    }
    fun getUserId() = saveLivelihoodMappingForSubjectRepository.getUserId()

}