package com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case

import com.sarathi.smallgroupmodule.ui.didiTab.domain.repository.FetchSmallGroupDetailsFromDbRepository
import javax.inject.Inject

class FetchSmallGroupDetailsFromDbUseCase @Inject constructor(
    private val fetchSmallGroupDetailsFromDbRepository: FetchSmallGroupDetailsFromDbRepository
) {

    suspend operator fun invoke() {


    }

}
