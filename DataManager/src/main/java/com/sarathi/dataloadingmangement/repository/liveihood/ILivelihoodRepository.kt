package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.response.Livelihood
import com.sarathi.dataloadingmangement.model.response.LivelihoodResponse

interface ILivelihoodRepository {
    suspend fun fetchLivelihoodFromServer(mainLivelihood: LivelihoodResponse): ApiResponseModel<LivelihoodResponse>
    suspend fun saveLivelihoodToDB(livelihood: Livelihood)
}