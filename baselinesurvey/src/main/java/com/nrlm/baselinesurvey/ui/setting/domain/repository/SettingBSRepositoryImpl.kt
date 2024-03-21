package com.nrlm.baselinesurvey.ui.setting.domain.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.LANGUAGE_OPEN_FROM_SETTING
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.network.interfaces.BaseLineApiService

class SettingBSRepositoryImpl(
    private val prefRepo: PrefRepo,
    private val baseLineApiService: BaseLineApiService,
):SettingBSRepository {

    override suspend fun performLogout(): ApiResponseModel<String> {
        return baseLineApiService.performLogout()
    }

    override fun clearSharedPref() {
        prefRepo.saveAccessToken(BLANK_STRING)
    }

    override fun saveLanguageScreenOpenFrom() {
        prefRepo.savePref(LANGUAGE_OPEN_FROM_SETTING,true)
    }
}