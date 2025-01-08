package com.patsurvey.nudge.activities.backup.domain.use_case

import com.patsurvey.nudge.activities.backup.domain.repository.ExportImportRepository
import com.sarathi.dataloadingmangement.model.uiModel.MissionUiModel

class GetExportOptionListUseCase(private val repository: ExportImportRepository) {
    suspend fun fetchMissionsForUser(): List<MissionUiModel> {
        return repository.fetchMissionsForUser()
    }
}