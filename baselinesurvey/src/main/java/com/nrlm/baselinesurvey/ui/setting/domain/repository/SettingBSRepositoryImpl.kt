package com.nrlm.baselinesurvey.ui.setting.domain.repository

import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nudge.core.model.SettingOptionModel

class SettingBSRepositoryImpl(private val prefRepo: PrefRepo,
                              private val apiService: ApiService,
    ):SettingBSRepository {
    override fun getSettingOptionList():List<SettingOptionModel> {
       return listOf(
            SettingOptionModel(1,"Sync Now","new Datta"),
            SettingOptionModel(2,"Sync Now","new Datta"),
            SettingOptionModel(3,"Sync Now","new Datta"))
    }
}