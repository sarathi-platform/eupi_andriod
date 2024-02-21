package com.nrlm.baselinesurvey.ui.setting.domain.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nudge.core.model.SettingOptionModel

class SettingBSRepositoryImpl(private val prefRepo: PrefRepo,
                              private val apiService: ApiService,
    ):SettingBSRepository {

    override suspend fun performLogout(): ApiResponseModel<String> {
        return apiService.performLogout()
    }

    override fun clearSharedPref() {
        prefRepo.saveAccessToken(BLANK_STRING)
    }
}