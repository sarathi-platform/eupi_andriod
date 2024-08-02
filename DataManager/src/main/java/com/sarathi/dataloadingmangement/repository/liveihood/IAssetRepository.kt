package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.response.Asset

interface IAssetRepository {
    //suspend fun fetchLivelihoodFromServer(mainLivelihood: LivelihoodResponse): ApiResponseModel<LivelihoodResponse>
    suspend fun saveAssetToDB(asset: Asset)
}