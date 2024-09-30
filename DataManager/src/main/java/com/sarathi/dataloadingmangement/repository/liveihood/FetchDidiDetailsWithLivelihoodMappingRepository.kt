package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.uiModel.livelihood.SubjectEntityWithLivelihoodMappingUiModel

interface FetchDidiDetailsWithLivelihoodMappingRepository {

    suspend fun fetchDidiDetailsWithLivelihoodMapping(): List<SubjectEntityWithLivelihoodMappingUiModel>

}