package com.nrlm.baselinesurvey.ui.setting.domain.repository

import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nudge.core.model.SettingOptionModel

interface SettingBSRepository {
    suspend fun performLogout(): ApiResponseModel<String>

    fun clearSharedPref()

    fun saveLanguageScreenOpenFrom()

    fun clearLocalData()

}