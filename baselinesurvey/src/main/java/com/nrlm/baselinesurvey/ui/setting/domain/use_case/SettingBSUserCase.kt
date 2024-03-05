package com.nrlm.baselinesurvey.ui.setting.domain.use_case

data class SettingBSUserCase (
    val getSettingOptionListUseCase: GetSettingOptionListUseCase,
    val logoutUseCase: LogoutUseCase,
    val saveLanguageScreenOpenFromUseCase: SaveLanguageScreenOpenFromUseCase
)