package com.nrlm.baselinesurvey.ui.backup.domain.use_case

import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.EventsWriterUserCase

data class ExportImportUseCase(
    val getExportOptionListUseCase: GetExportOptionListUseCase,
    val clearLocalDBExportUseCase: ClearLocalDBExportUseCase,
    val getUserDetailsExportUseCase: GetUserDetailsExportUseCase,
    val eventsWriterUseCase: EventsWriterUserCase
)
