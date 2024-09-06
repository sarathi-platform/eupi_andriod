package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.response.LivelihoodSaveEventResponse

interface IGetLivelihoodSaveEventRepository {
    suspend fun getLivelihoodSaveEventFromNetwork(): ApiResponseModel<List<LivelihoodSaveEventResponse>>

    suspend fun saveLivelihoodSaveEventIntoDb(livelihoodSaveEventResponse: List<LivelihoodSaveEventResponse>)
}