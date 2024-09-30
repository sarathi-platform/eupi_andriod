package com.sarathi.dataloadingmangement.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.LanguageDao
import com.sarathi.dataloadingmangement.data.entities.LanguageEntity
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.network.response.UserDetailsResponse
import javax.inject.Inject

class UserDetailRepository @Inject constructor(
    val sharedPrefs: CoreSharedPrefs,
    val apiInterface: DataLoadingApiService,
    val languageDao: LanguageDao
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
        userDetailsResponse.referenceId.let {
            sharedPrefs.setStateId(
                userDetailsResponse.referenceId.first().stateId ?: -1
            )
        }
    }
}