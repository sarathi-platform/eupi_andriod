package com.sarathi.dataloadingmangement.repository.smallGroup

import android.text.TextUtils
import com.nudge.core.BLANK_STRING
import com.nudge.core.SUCCESS
import com.nudge.core.model.response.LokOsStateCodeResponseModel
import com.nudge.core.model.response.ShgDetailsFromLokOsResponseModel
import com.nudge.core.model.response.VillageDetailsFromLokOsResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_STATE_NAME
import com.nudge.core.utils.CoreLogger
import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.entities.ShgVerificationDataModel
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class ShgVerificationRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val subjectEntityDao: SubjectEntityDao,
    private val dataLoadingApiService: DataLoadingApiService,
) : ShgVerificationRepository {

    override suspend fun getStateCodesListFromLokOs(): List<LokOsStateCodeResponseModel> {
        try {
            val response = dataLoadingApiService.getStateCodesFromLokOS()
            return if (response.status.equals(SUCCESS)) {
                response.data?.let { stateCodesList ->
                    return stateCodesList
                } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            CoreLogger.e(
                tag = "ShgVerificationRepositoryImpl",
                msg = "getStateCodesListFromLokOs ->  Exception: ${e.message}",
                ex = e,
                stackTrace = true
            )
            return emptyList()
        }
    }

    override suspend fun getVillageDetailsFromLokOs(villageId: Int): VillageDetailsFromLokOsResponseModel? {
        try {
            val response = dataLoadingApiService.fetchVillageDetailsFromLokOS(villageId.toLong())
            return if (response.status.equals(SUCCESS)) {
                response.data
            } else {
                null
            }
        } catch (e: Exception) {
            CoreLogger.e(
                tag = "ShgVerificationRepositoryImpl",
                msg = "getVillageDetailsFromLokOs ->  Exception: ${e.message}",
                ex = e,
                stackTrace = true
            )
            return null
        }
    }

    override suspend fun getShgDetailsFromLokOs(
        stateCode: String,
        shgCode: String
    ): ShgDetailsFromLokOsResponseModel? {

        if (TextUtils.isEmpty(stateCode)) {
            return null
        }

        try {
            val response = dataLoadingApiService.fetchShgDetailsFromLokOS(stateCode, shgCode)
            return if (response.status.equals(SUCCESS)) {
                response.data
            } else {
                null
            }
        } catch (e: Exception) {
            CoreLogger.e(
                tag = "ShgVerificationRepositoryImpl",
                msg = "getShgDetailsFromLokOs ->  Exception: ${e.message}",
                ex = e,
                stackTrace = true
            )
            return null
        }
    }

    override fun getStateCodeForShg(stateCodesListFromLokOs: List<LokOsStateCodeResponseModel>): String {
        return stateCodesListFromLokOs.find {
            it.stateNameEn.equals(
                coreSharedPrefs.getPref(
                    PREF_STATE_NAME,
                    BLANK_STRING
                ), true
            )
        }?.stateShortNameEn ?: BLANK_STRING
    }

    override suspend fun saveShgVerificationStatus(shgVerificationDataModel: ShgVerificationDataModel) {
        subjectEntityDao.updateShgVerificationStatus(
            shgVerificationDataModel.subjectId,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            shgVerificationStatus = shgVerificationDataModel.shgVerificationStatus.value(),
            shgVerificationDate = shgVerificationDataModel.shgVerificationDate.value(),
            shgName = shgVerificationDataModel.shgName.value(),
            shgCode = shgVerificationDataModel.shgCode.value(),
            shgMemberId = shgVerificationDataModel.shgMemberId.value()
        )
    }


}