package com.nudge.core.usecase

import com.nudge.core.data.repository.FetchRemoteQueryFromNetworkRepository
import javax.inject.Inject

class FetchRemoteQueryFromNetworkUseCase @Inject constructor(
    private val fetchRemoteQueryFromNetworkRepository: FetchRemoteQueryFromNetworkRepository
) {

    suspend fun invoke() {

    }

}