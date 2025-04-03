package com.sarathi.dataloadingmangement.repository.smallGroup

import com.nudge.core.model.response.LokOsStateCodeResponseModel
import com.nudge.core.model.response.ShgDetailsFromLokOsResponseModel
import com.nudge.core.model.response.VillageDetailsFromLokOsResponseModel
import com.sarathi.dataloadingmangement.data.entities.ShgVerificationDataModel

interface ShgVerificationRepository {

    suspend fun getStateCodesListFromLokOs(): List<LokOsStateCodeResponseModel>

    suspend fun getVillageDetailsFromLokOs(villageId: Int): VillageDetailsFromLokOsResponseModel?

    suspend fun getShgDetailsFromLokOs(
        stateCode: String,
        shgCode: String
    ): ShgDetailsFromLokOsResponseModel?

    fun getStateCodeForShg(stateCodesListFromLokOs: List<LokOsStateCodeResponseModel>): String

    suspend fun saveShgVerificationStatus(shgVerificationDataModel: ShgVerificationDataModel)
}