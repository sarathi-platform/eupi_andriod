package com.nrlm.baselinesurvey.ui.setting.domain.use_case

import javax.inject.Inject

data class SettingBSUserCase @Inject constructor(
    val getSettingOptionListUseCase: GetSettingOptionListUseCase
)