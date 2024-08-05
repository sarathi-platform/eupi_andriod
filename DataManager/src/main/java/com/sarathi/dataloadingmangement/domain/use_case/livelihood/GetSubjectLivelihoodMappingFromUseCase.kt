package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.repository.liveihood.GetLivelihoodMappingForSubjectFromDbRepository
import javax.inject.Inject

class GetSubjectLivelihoodMappingFromUseCase @Inject constructor(
    private val getLivelihoodMappingForSubjectFromDbRepository: GetLivelihoodMappingForSubjectFromDbRepository
) {

    suspend operator fun invoke(subjectId: Int): SubjectLivelihoodMappingEntity? {
        return getLivelihoodMappingForSubjectFromDbRepository.getLivelihoodMappingForSubject(
            subjectId
        )
    }

}