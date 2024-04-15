package com.nrlm.baselinesurvey.ui.setting.domain.repository

import com.nrlm.baselinesurvey.model.response.ApiResponseModel

interface SettingBSRepository {
    suspend fun performLogout(): ApiResponseModel<String>

    fun clearSharedPref()

    fun saveLanguageScreenOpenFrom()

    fun regenerateAllBaselineEvent()
}