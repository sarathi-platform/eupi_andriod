package com.nrlm.baselinesurvey.ui.setting.domain.use_case

data class SettingBSUserCase (
    val getUserDetailsUseCase: GetUserDetailsUseCase,
    val logoutUseCase: LogoutUseCase,
    val saveLanguageScreenOpenFromUseCase: SaveLanguageScreenOpenFromUseCase,
    val clearLocalDBUseCase: ClearLocalDBUseCase,
    val getSettingOptionListUseCase: GetSettingOptionListUseCase,
)