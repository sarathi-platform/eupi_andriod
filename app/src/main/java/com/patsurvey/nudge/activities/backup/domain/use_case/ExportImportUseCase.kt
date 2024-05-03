package com.patsurvey.nudge.activities.backup.domain.use_case

data class ExportImportUseCase(
    val getExportOptionListUseCase: GetExportOptionListUseCase,
    val clearLocalDBExportUseCase: ClearLocalDBExportUseCase,
    val getUserDetailsExportUseCase: GetUserDetailsExportUseCase
)
