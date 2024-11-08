package com.patsurvey.nudge.activities.ui.progress.domain.repository.impls

import com.nudge.core.BLANK_STRING
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import com.nudge.core.preference.CoreSharedPrefs
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.FetchSelectionUserDataRepository
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.UserDetailsResponse
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.LAST_SYNC_TIME
import com.patsurvey.nudge.utils.PREF_KEY_IDENTITY_NUMBER
import com.patsurvey.nudge.utils.PREF_KEY_PROFILE_IMAGE
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_STATE_ID
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.network.ApiException
import javax.inject.Inject

class FetchSelectionUserDataRepositoryImpl @Inject constructor(
    private val sharedPrefs: CoreSharedPrefs,
    private val apiService: ApiService,
    private val languageDao: LanguageListDao,
    private val villageListDao: VillageListDao
) : FetchSelectionUserDataRepository {

    override suspend fun fetchAndSaveUserDetailsAndVillageListFromNetwork(userViewApiRequest: String): Boolean {

        try {
            val apiResponse = fetchUseDetailFromNetwork(userViewApiRequest)

            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let { userApiResponse ->
                    saveUserDetails(userApiResponse)
                    userApiResponse.villageList?.let { villageList ->
                        if (villageList.isNotEmpty()) {
                            villageListDao.insertOnlyNewData(villageList)
                            villageList.firstOrNull()?.let { villageEntity ->
                                sharedPrefs.setStateId(
                                    villageEntity.stateId
                                )
                            }
                        } else {
                            throw ApiResponseFailException("Village List is empty")
                        }
                    } ?: throw ApiResponseFailException("Village List is null")

                }
                if (!apiResponse.lastSyncTime.isNullOrEmpty()) {
                    updateLastSyncTime(apiResponse.lastSyncTime)
                }
                return true
            } else {
                return false
            }
        } catch (apiResponseFailException: ApiResponseFailException) {
            throw apiResponseFailException
        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }

    override suspend fun fetchUseDetailFromNetwork(userViewApiRequest: String): ApiResponseModel<UserDetailsResponse> {
        return apiService.userAndVillageListAPI(userViewApiRequest)
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

        userDetailsResponse.typeName?.let {
            sharedPrefs.setUserType(it)
            if (it.equals(BPC_USER_TYPE, true)) {
                sharedPrefs.setIsUserBPC(true)
            } else {
                sharedPrefs.setIsUserBPC(false)
            }
        }

        userDetailsResponse.identityNumber?.let {
            sharedPrefs.savePref(
                PREF_KEY_IDENTITY_NUMBER,
                it
            )
        }
        userDetailsResponse.profileImage?.let { sharedPrefs.savePref(PREF_KEY_PROFILE_IMAGE, it) }
        userDetailsResponse.villageList?.firstOrNull()?.let {
            sharedPrefs.savePref(
                PREF_KEY_TYPE_STATE_ID,
                it.stateId
            )
        }
    }

    fun updateLastSyncTime(lastSyncTime: String) {
        val saveSyncTime = sharedPrefs.getPref(LAST_SYNC_TIME, 0L)
        if (saveSyncTime > 0) {
            val compValue = lastSyncTime.toLong().compareTo(saveSyncTime)
            if (compValue > 0) {
                sharedPrefs.savePref(LAST_SYNC_TIME, lastSyncTime.toLong())
            }

        } else sharedPrefs.savePref(LAST_SYNC_TIME, lastSyncTime.toLong())
    }

}