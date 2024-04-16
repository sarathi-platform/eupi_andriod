package com.nrlm.baselinesurvey.ui.splash.domain.repository

import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.ConfigResponseModel

interface SplashScreenRepository {

    suspend fun getLanguageConfigFromNetwork(): ApiResponseModel<ConfigResponseModel?>

    suspend fun saveLanguageIntoDatabase(languageEntity: List<LanguageEntity>)

    suspend fun checkAndAddDefaultLanguage()

    fun isLoggedIn(): Boolean

    fun saveLanguageOpenFrom()

    fun isDataSynced(): Boolean
    fun setAllDataSynced()
    fun performLogout(clearData: Boolean)

    fun clearLocalData()

    fun clearSharedPref()

    fun getPreviousMobileNumber(): String

    fun getMobileNumber(): String


}