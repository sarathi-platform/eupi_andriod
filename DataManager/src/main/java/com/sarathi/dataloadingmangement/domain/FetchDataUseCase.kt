package com.sarathi.dataloadingmangement.domain

import com.sarathi.dataloadingmangement.domain.use_case.FetchContentDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchMissionDataFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchUserDetailsUseCase


data class FetchDataUseCase(
    val fetchUserDetailsUseCase: FetchUserDetailsUseCase,
    val fetchMissionDataFromNetworkUseCase: FetchMissionDataFromNetworkUseCase,
    val fetchContentDataFromNetworkUseCase: FetchContentDataFromNetworkUseCase,
)