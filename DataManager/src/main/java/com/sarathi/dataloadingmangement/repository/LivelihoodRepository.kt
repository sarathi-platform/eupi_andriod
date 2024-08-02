package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.network.request.LivelihoodRequest
import com.sarathi.dataloadingmangement.network.response.AssetsResponse
import com.sarathi.dataloadingmangement.network.response.LivelihoodResponse

interface LivelihoodRepository {
    suspend fun fetchLivelihoodDataFromServer(livelihoodMangerRequest: List<LivelihoodRequest>): ApiResponseModel<List<LivelihoodResponse>>

    suspend fun saveLivelihoodToDB(livelihood: List<LivelihoodResponse>)

    suspend fun saveLivelihoodAssetsToDB(
        assets: List<AssetsResponse>,
    )

}