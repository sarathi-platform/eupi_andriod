package com.nrlm.baselinesurvey.ui.setting.domain.repository

import com.nrlm.baselinesurvey.model.response.ApiResponseModel

interface SettingBSRepository {
    suspend fun performLogout(): ApiResponseModel<String>

    fun clearSharedPref()

    fun saveLanguageScreenOpenFrom()

    fun clearLocalData()

    fun setAllDataSynced()

    fun getUserMobileNumber():String
    fun getUserID():String
    fun getUserEmail():String

    fun getUserName(): String
    fun getMobileNo(): String

}