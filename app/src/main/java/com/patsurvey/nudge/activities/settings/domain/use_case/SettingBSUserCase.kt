package com.patsurvey.nudge.activities.settings.domain.use_case

data class SettingBSUserCase (
    val getSettingOptionListUseCase: GetSettingOptionListUseCase,
    val logoutUseCase: LogoutUseCase,
    val saveLanguageScreenOpenFromUseCase: SaveLanguageScreenOpenFromUseCase
)