package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEntity
import com.sarathi.dataloadingmangement.model.response.Livelihood
import com.sarathi.dataloadingmangement.model.response.LivelihoodResponse
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class LivelihoodRepositoryImpl @Inject constructor(
    val apiInterface: DataLoadingApiService,
    private val livelihoodDao: LivelihoodDao,
    private val coreSharedPrefs: CoreSharedPrefs

) : ILivelihoodRepository {
    override suspend fun fetchLivelihoodFromServer(livelihoodResponse: LivelihoodResponse): ApiResponseModel<LivelihoodResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun saveLivelihoodToDB(livelihood: Livelihood) {
        livelihoodDao.insertLivelihood(
            livelihood = LivelihoodEntity.getLivelihoodEntity(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                livelihood = livelihood
            )
        )
    }
}