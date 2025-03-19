package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

import android.text.TextUtils
import com.nudge.core.model.response.ShgDetailsFromLokOsResponseModel
import com.nudge.core.model.response.VillageDetailsFromLokOsResponseModel
import com.sarathi.dataloadingmangement.data.entities.ShgVerificationDataModel
import com.sarathi.dataloadingmangement.repository.smallGroup.ShgVerificationRepository
import javax.inject.Inject

class ShgVerificationUseCase @Inject constructor(
    private val shgVerificationRepository: ShgVerificationRepository
) {

    suspend fun getShgForVillage(villageId: Int): VillageDetailsFromLokOsResponseModel? {
        if (villageId <= 0)
            return null

        return shgVerificationRepository.getVillageDetailsFromLokOs(villageId)
    }

    suspend fun getShgDetailsFromLokOs(shgCode: String): ShgDetailsFromLokOsResponseModel? {
        val stateCodeList = shgVerificationRepository.getStateCodesListFromLokOs()
        val stateCode = shgVerificationRepository.getStateCodeForShg(stateCodeList)

        if (stateCodeList.isEmpty())
            return null

        if (TextUtils.isEmpty(stateCode)) {
            return null
        }

        return shgVerificationRepository.getShgDetailsFromLokOs(stateCode, shgCode)
    }

    suspend fun saveShgVerificationStatus(shgVerificationDataModel: ShgVerificationDataModel) {
        shgVerificationRepository.saveShgVerificationStatus(shgVerificationDataModel)
    }

}