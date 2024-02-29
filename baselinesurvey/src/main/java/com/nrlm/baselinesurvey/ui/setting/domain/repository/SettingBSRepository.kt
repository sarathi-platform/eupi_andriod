package com.nrlm.baselinesurvey.ui.setting.domain.repository

import com.nudge.core.model.SettingOptionModel

interface SettingBSRepository {
    fun getSettingOptionList():List<SettingOptionModel>
}