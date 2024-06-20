package com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case

import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import com.sarathi.smallgroupmodule.ui.didiTab.domain.repository.FetchSmallGroupListFromDbRepository
import javax.inject.Inject

class FetchSmallGroupListsFromDbUseCase @Inject constructor(
    private val fetchSmallGroupListFromDbRepository: FetchSmallGroupListFromDbRepository
) {

    suspend operator fun invoke(): List<SmallGroupSubTabUiModel> {
        val uniqueUserId = fetchSmallGroupListFromDbRepository.getUniqueUserId()
        return fetchSmallGroupListFromDbRepository.getSmallGroupListForUser(uniqueUserId)
    }


}