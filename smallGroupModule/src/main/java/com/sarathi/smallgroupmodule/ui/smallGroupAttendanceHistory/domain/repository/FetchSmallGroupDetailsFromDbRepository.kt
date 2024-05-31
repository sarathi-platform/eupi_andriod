package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.domain.repository

import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel

interface FetchSmallGroupDetailsFromDbRepository {

    fun getUniqueUserId(): String

    suspend fun getSmallGroupDetailsForUserAndSmallGroupId(
        uniqueUserId: String,
        smallGroupId: Int
    ): SmallGroupSubTabUiModel

}
