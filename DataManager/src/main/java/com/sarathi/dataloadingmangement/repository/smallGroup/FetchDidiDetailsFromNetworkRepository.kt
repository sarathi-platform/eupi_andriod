package com.sarathi.dataloadingmangement.repository.smallGroup

import com.sarathi.dataloadingmangement.model.response.BeneficiaryApiResponse

interface FetchDidiDetailsFromNetworkRepository {

    suspend fun fetchDidiDetailsFromNetwork()

    suspend fun saveDidiDetailsToDb(beneficiaryApiResponse: BeneficiaryApiResponse)

}