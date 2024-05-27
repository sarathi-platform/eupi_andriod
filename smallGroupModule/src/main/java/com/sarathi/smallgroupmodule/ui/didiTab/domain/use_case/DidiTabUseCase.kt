package com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case

import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchDidiDetailsFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupFromNetworkUseCase
import javax.inject.Inject

data class DidiTabUseCase @Inject constructor(
    val fetchDidiDetailsFromDbUseCase: FetchDidiDetailsFromDbUseCase,
    val fetchSmallGroupListsFromDbUseCase: FetchSmallGroupListsFromDbUseCase,
    val fetchDidiDetailsFromNetworkUseCase: FetchDidiDetailsFromNetworkUseCase,
    val fetchSmallGroupFromNetworkUseCase: FetchSmallGroupFromNetworkUseCase
)