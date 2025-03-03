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
    suspend fun saveAndUpdateLivelihoodMappingForSubject(
        livelihoodId: Int,
        type:Int,
        subjectId: Int,
        livelihoodType: String

        )
    {
        saveLivelihoodMappingForSubjectRepository.saveAndUpdateSubjectLivelihoodMappingForSubject(
            livelihoodId = livelihoodId,
            type = type,
            subjectId = subjectId,
            livelihoodType = livelihoodType
        )
    }

    fun getUserId() = saveLivelihoodMappingForSubjectRepository.getUserId()

}