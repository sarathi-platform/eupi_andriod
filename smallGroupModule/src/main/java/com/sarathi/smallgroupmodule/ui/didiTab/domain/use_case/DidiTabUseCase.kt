package com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case

import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchDidiDetailsFromNetworkUseCase
import com.sarathi.dataloadingmangement.domain.use_case.smallGroup.FetchSmallGroupFromNetworkUseCase

data class DidiTabUseCase(
    val fetchDidiDetailsFromDbUseCase: FetchDidiDetailsFromDbUseCase,
//    val fetchSmallGroupDetailsFromDbUseCase: FetchSmallGroupDetailsFromDbUseCase,
    val fetchDidiDetailsFromNetworkUseCase: FetchDidiDetailsFromNetworkUseCase,
    val fetchSmallGroupFromNetworkUseCase: FetchSmallGroupFromNetworkUseCase
)