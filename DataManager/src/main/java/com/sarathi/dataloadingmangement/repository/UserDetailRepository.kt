package com.sarathi.dataloadingmangement.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.database.dao.language.LanguageListDao
import com.nudge.core.database.entities.language.LanguageEntity
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_BLOCK_NAME
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_DISTRICT_NAME
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_STATE_NAME
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.network.response.UserDetailsResponse
import javax.inject.Inject

class UserDetailRepository @Inject constructor(
    val sharedPrefs: CoreSharedPrefs,
    val apiInterface: DataLoadingApiService,
    val languageDao: LanguageListDao
) : IUserDetailRepository {
    override suspend fun fetchUseDetailFromNetwork(userViewApiRequest: String): ApiResponseModel<UserDetailsResponse> {
        return apiInterface.userAndVillageListAPI(userViewApiRequest)
    }

    override suspend fun fetchLanguage(): List<LanguageEntity> {
        return languageDao.getAllLanguages()
    }

    override fun saveUserDetails(userDetailsResponse: UserDetailsResponse) {

        val mobileNo = sharedPrefs.getMobileNo()
        sharedPrefs.setBackupFileName(
            getDefaultBackUpFileName(
                mobileNo,
                userDetailsResponse.typeName ?: BLANK_STRING
            )
        )
        sharedPrefs.setImageBackupFileName(
            getDefaultImageBackUpFileName(
                mobileNo,
                userDetailsResponse.typeName ?: BLANK_STRING
            )
        )

        userDetailsResponse.username?.let { sharedPrefs.setUserName(it) }
        userDetailsResponse.name?.let { sharedPrefs.setName(it) }
        userDetailsResponse.email?.let { sharedPrefs.setUserEmail(it) }
        userDetailsResponse.roleName?.let { sharedPrefs.setUserRole(it) }
        userDetailsResponse.typeName?.let { sharedPrefs.setUserType(it) }
        if (userDetailsResponse.referenceId?.isNotEmpty() == true) {
            userDetailsResponse.referenceId.let {
                sharedPrefs.setStateId(
                    userDetailsResponse.referenceId.first()?.stateId ?: -1
                )
            }
            userDetailsResponse.referenceId.let {
                sharedPrefs.setStateCode(
                    userDetailsResponse.referenceId.first()?.stateCode ?: BLANK_STRING
                )
            }
        }
        userDetailsResponse.federationDetail?.let {
            sharedPrefs.savePref(PREF_STATE_NAME, it.stateName ?: BLANK_STRING)
            sharedPrefs.savePref(PREF_DISTRICT_NAME, it.districtName ?: BLANK_STRING)
            sharedPrefs.savePref(PREF_BLOCK_NAME, it.blockName ?: BLANK_STRING)
        }
    }

    override fun getUSerMobileNo(): String {
        return sharedPrefs.getMobileNo()
    }

    override fun getBuildEnv() = sharedPrefs.getBuildEnvironment()
}