package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

import com.nudge.core.DEFAULT_SUCCESS_CODE
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchDidiDetailsFromNetworkRepository
import javax.inject.Inject

class FetchDidiDetailsFromNetworkUseCase @Inject constructor(
    private val fetchDidiDetailsFromNetworkRepository: FetchDidiDetailsFromNetworkRepository
) {

    suspend operator fun invoke() {
        fetchDidiDetailsFromNetworkRepository.fetchDidiDetailsFromNetwork()
    }

    suspend fun isFetchDidiDetailsAPIStatusFailed(): Boolean {
        return fetchDidiDetailsFromNetworkRepository.isFetchDidiDetailsAPIStatus()?.errorCode != DEFAULT_SUCCESS_CODE
    }

}