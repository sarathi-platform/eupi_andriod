package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

import com.sarathi.dataloadingmangement.repository.smallGroup.FetchDidiDetailsFromNetworkRepository
import javax.inject.Inject

class FetchDidiDetailsFromNetworkUseCase @Inject constructor(
    private val fetchDidiDetailsFromNetworkRepository: FetchDidiDetailsFromNetworkRepository
) {

    suspend operator fun invoke(userId: Int) {
        fetchDidiDetailsFromNetworkRepository.fetchDidiDetailsFromNetwork(userId)
    }

}