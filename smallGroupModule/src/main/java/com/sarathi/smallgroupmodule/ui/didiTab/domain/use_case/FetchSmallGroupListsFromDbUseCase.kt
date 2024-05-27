package com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case

import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import com.sarathi.smallgroupmodule.ui.didiTab.domain.repository.FetchSmallGroupDetailsFromDbRepository
import javax.inject.Inject

class FetchSmallGroupListsFromDbUseCase @Inject constructor(
    private val fetchSmallGroupDetailsFromDbRepository: FetchSmallGroupDetailsFromDbRepository
) {

    suspend operator fun invoke(): List<SmallGroupSubTabUiModel> {
        val uniqueUserId = fetchSmallGroupDetailsFromDbRepository.getUniqueUserId()
        return fetchSmallGroupDetailsFromDbRepository.getSmallGroupListForUser(uniqueUserId)
    }


}