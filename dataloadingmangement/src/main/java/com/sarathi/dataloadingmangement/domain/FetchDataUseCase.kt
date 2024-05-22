package com.sarathi.dataloadingmangement.domain

import com.sarathi.dataloadingmangement.domain.use_case.FetchMissionDataFromNetworkUseCase


data class FetchDataUseCase(
    val fetchMissionDataFromNetworkUseCase: FetchMissionDataFromNetworkUseCase,
) {
}