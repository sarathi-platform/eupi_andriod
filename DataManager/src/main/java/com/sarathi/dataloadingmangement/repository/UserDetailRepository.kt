package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_EMAIL
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_IDENTITY_NUMBER
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_NAME
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_PROFILE_IMAGE
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_ROLE_NAME
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_TYPE_NAME
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_USER_NAME
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_STATE_ID
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
        sharedPrefs.savePref(PREF_KEY_USER_NAME, userDetailsResponse.username ?: "")
        sharedPrefs.savePref(PREF_KEY_NAME, userDetailsResponse.name ?: "")
        sharedPrefs.savePref(PREF_KEY_EMAIL, userDetailsResponse.email ?: "")
        sharedPrefs.savePref(PREF_KEY_IDENTITY_NUMBER, userDetailsResponse.identityNumber ?: "")
        sharedPrefs.savePref(PREF_KEY_PROFILE_IMAGE, userDetailsResponse.profileImage ?: "")
        sharedPrefs.savePref(PREF_KEY_ROLE_NAME, userDetailsResponse.roleName ?: "")
        sharedPrefs.savePref(PREF_KEY_TYPE_NAME, userDetailsResponse.typeName ?: "")
        sharedPrefs.savePref(PREF_STATE_ID, userDetailsResponse.referenceId.first().stateId ?: -1)
    }
}