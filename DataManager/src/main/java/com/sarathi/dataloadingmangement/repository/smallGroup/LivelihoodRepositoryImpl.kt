package com.sarathi.dataloadingmangement.repository.smallGroup


import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.AssetsDao
import com.sarathi.dataloadingmangement.data.dao.LivelihoodDao
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.network.request.LivelihoodRequest
import com.sarathi.dataloadingmangement.network.response.AssetsResponse
import com.sarathi.dataloadingmangement.network.response.LivelihoodResponse
import com.sarathi.dataloadingmangement.repository.LivelihoodRepository
import javax.inject.Inject

class LivelihoodRepositoryImpl @Inject constructor(
    val apiInterface: DataLoadingApiService,
    val livelihoodDao: LivelihoodDao,
    val assetsDao: AssetsDao,
    val coreSharedPrefs: CoreSharedPrefs,
) : LivelihoodRepository {
    override suspend fun fetchLivelihoodDataFromServer(livelihoodRequest: List<LivelihoodRequest>): ApiResponseModel<List<LivelihoodResponse>> {
        return apiInterface.fetchLivelihoodData(livelihoodRequest)
    }
    override suspend fun saveLivelihoodToDB(livelihood: List<LivelihoodResponse>) {
        TODO("Not yet implemented")
    }

    override suspend fun saveLivelihoodAssetsToDB(assets: List<AssetsResponse>) {
        TODO("Not yet implemented")
    }

}