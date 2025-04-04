package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import com.sarathi.dataloadingmangement.repository.smallGroup.FetchSmallGroupListFromDbRepository
import javax.inject.Inject

class FetchSmallGroupListsFromDbUseCase @Inject constructor(
    private val fetchSmallGroupListFromDbRepository: FetchSmallGroupListFromDbRepository
) {

    suspend operator fun invoke(): List<SmallGroupSubTabUiModel> {
        val uniqueUserId = fetchSmallGroupListFromDbRepository.getUniqueUserId()
        return fetchSmallGroupListFromDbRepository.getSmallGroupListForUser(uniqueUserId)
    }


}