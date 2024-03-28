package com.patsurvey.nudge.activities.settings.domain.repository

import com.patsurvey.nudge.model.response.ApiResponseModel


interface SettingBSRepository {
    suspend fun performLogout(): ApiResponseModel<String>

    fun clearSharedPref()

    fun saveLanguageScreenOpenFrom()
}