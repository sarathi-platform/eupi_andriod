package com.sarathi.smallgroupmodule.ui.didiTab.domain.repository

import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel

interface FetchSmallGroupDetailsFromDbRepository {

    fun getUniqueUserId(): String

    suspend fun getSmallGroupListForUser(uniqueUserId: String): List<SmallGroupSubTabUiModel>

}