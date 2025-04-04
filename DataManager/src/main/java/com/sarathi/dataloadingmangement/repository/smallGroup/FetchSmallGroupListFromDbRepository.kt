package com.sarathi.dataloadingmangement.repository.smallGroup

import com.sarathi.dataloadingmangement.model.uiModel.SmallGroupSubTabUiModel

interface FetchSmallGroupListFromDbRepository {

    fun getUniqueUserId(): String

    suspend fun getSmallGroupListForUser(uniqueUserId: String): List<SmallGroupSubTabUiModel>

}