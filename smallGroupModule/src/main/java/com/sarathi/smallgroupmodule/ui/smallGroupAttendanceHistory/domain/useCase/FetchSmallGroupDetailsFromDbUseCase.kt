package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.useCase

import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel
import com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.repository.FetchSmallGroupDetailsFromDbRepository
import javax.inject.Inject

class FetchSmallGroupDetailsFromDbUseCase @Inject constructor(
    private val fetchSmallGroupDetailsFromDbRepository: FetchSmallGroupDetailsFromDbRepository
) {

    suspend operator fun invoke(smallGroupId: Int): SmallGroupSubTabUiModel {
        val uniqueUserId = fetchSmallGroupDetailsFromDbRepository.getUniqueUserId()
        return fetchSmallGroupDetailsFromDbRepository.getSmallGroupDetailsForUserAndSmallGroupId(
            uniqueUserId,
            smallGroupId
        )

    }

}
