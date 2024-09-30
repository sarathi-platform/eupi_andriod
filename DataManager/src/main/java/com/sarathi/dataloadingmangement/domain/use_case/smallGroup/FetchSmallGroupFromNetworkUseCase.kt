package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

import com.nudge.core.DEFAULT_SUCCESS_CODE
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupDetailsFromNetworkRepository
import javax.inject.Inject

class FetchSmallGroupFromNetworkUseCase @Inject constructor(
    private val fetchSmallGroupDetailsFromNetworkRepository: FetchSmallGroupDetailsFromNetworkRepository
) {

    suspend operator fun invoke() {
        fetchSmallGroupDetailsFromNetworkRepository.fetchSmallGroupDetails()
    }
    suspend fun isFetchSmallGroupDetailsAPIFailed(): Boolean {
        return fetchSmallGroupDetailsFromNetworkRepository.isFetchSmallGroupDetailsAPIStatus()?.status != DEFAULT_SUCCESS_CODE
    }


}