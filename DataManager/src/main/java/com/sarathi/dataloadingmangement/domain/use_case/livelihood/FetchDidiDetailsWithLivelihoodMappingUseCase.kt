package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.sarathi.dataloadingmangement.model.uiModel.livelihood.SubjectEntityWithLivelihoodMappingUiModel
import com.sarathi.dataloadingmangement.repository.liveihood.FetchDidiDetailsWithLivelihoodMappingRepository
import javax.inject.Inject

class FetchDidiDetailsWithLivelihoodMappingUseCase @Inject constructor(
    private val fetchDidiDetailsWithLivelihoodMappingRepository: FetchDidiDetailsWithLivelihoodMappingRepository
) {

    suspend operator fun invoke(): List<SubjectEntityWithLivelihoodMappingUiModel> {
        return fetchDidiDetailsWithLivelihoodMappingRepository.fetchDidiDetailsWithLivelihoodMapping()
    }


}