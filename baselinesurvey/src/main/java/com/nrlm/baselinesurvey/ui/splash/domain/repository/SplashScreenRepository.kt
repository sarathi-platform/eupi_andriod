package com.nrlm.baselinesurvey.ui.splash.domain.repository

import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.ConfigResponseModel
import com.nudge.core.database.entities.language.LanguageEntity

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