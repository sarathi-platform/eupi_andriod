package com.patsurvey.nudge.activities.settings.domain.use_case

import com.nudge.core.usecase.BaselineV1CheckUseCase
import com.patsurvey.nudge.activities.sync.home.domain.use_case.GetSyncEventsUseCase

data class SettingBSUserCase (
    val getSettingOptionListUseCase: GetSettingOptionListUseCase,
    val logoutUseCase: LogoutUseCase,
    val saveLanguageScreenOpenFromUseCase: SaveLanguageScreenOpenFromUseCase,
    val getAllPoorDidiForVillageUseCase: GetAllPoorDidiForVillageUseCase,
    val exportHandlerSettingUseCase: ExportHandlerSettingUseCase,
    val getUserDetailsUseCase: GetUserDetailsUseCase,
    val getSummaryFileUseCase: GetSummaryFileUseCase,
    val getCasteUseCase: GetCasteUseCase,
    val clearSelectionDBExportUseCase: ClearSelectionDBExportUseCase,
    val getSyncEventsUseCase: GetSyncEventsUseCase,
    val baselineV1CheckUseCase: BaselineV1CheckUseCase
)