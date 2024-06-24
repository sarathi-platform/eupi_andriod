package com.patsurvey.nudge.activities.backup.domain.use_case

import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.patsurvey.nudge.activities.backup.domain.repository.ExportImportRepository
import com.patsurvey.nudge.utils.UPCM_USER
import com.sarathi.dataloadingmangement.domain.use_case.DeleteAllDataUsecase

class ClearLocalDBExportUseCase(
    private val repository: ExportImportRepository,
    private val deleteAllDataUsecase: DeleteAllDataUsecase
) {
    suspend operator fun invoke(): Boolean {
        return try {
            if (repository.getLoggedInUserType() == UPCM_USER) {
                repository.clearLocalData()
                deleteAllDataUsecase.invoke()
            } else {
                repository.clearSelectionLocalDB()
            }
            true
        } catch (exception: Exception) {
            BaselineLogger.e("ClearLocalDBUseCase", "invoke", exception)
            false
        }
    }

    fun setAllDataSyncStatus() {
        repository.setAllDataSynced()
    }
}