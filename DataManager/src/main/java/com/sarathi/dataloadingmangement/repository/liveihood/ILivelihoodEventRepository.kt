package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.response.LivelihoodEvent

interface ILivelihoodEventRepository {
    //  suspend fun fetchLivelihoodFromServer(mainLivelihood: LivelihoodResponse): ApiResponseModel<LivelihoodResponse>
    suspend fun saveLivelihoodEventEntityToDB(livelihoodEvent: LivelihoodEvent)
}