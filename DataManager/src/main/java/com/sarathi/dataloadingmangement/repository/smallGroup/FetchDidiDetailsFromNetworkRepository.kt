package com.sarathi.dataloadingmangement.repository.smallGroup

import com.nudge.core.database.entities.ApiStatusEntity
import com.sarathi.dataloadingmangement.model.response.BeneficiaryApiResponse

interface FetchDidiDetailsFromNetworkRepository {

    suspend fun fetchDidiDetailsFromNetwork()

    suspend fun saveDidiDetailsToDb(beneficiaryApiResponse: BeneficiaryApiResponse)
    suspend fun isFetchDidiDetailsAPIStatus(): ApiStatusEntity?
    fun updateApiStatus(
        apiEndPoint: String,
        status: Int,
        errorMessage: String,
        errorCode: Int
    )
}