package com.patsurvey.nudge.activities.backup.domain.use_case

import com.nudge.core.utils.CoreLogger
import com.patsurvey.nudge.activities.backup.domain.repository.ExportImportRepository
import com.patsurvey.nudge.utils.UPCM_USER
import com.sarathi.dataloadingmangement.domain.use_case.DeleteAllGrantDataUseCase

class ClearLocalDBExportUseCase(
    private val repository: ExportImportRepository,
    private val deleteAllDataUseCase: DeleteAllGrantDataUseCase
) {
    suspend operator fun invoke(): Boolean {
        return try {
            if (repository.getLoggedInUserType() == UPCM_USER) {
                repository.clearLocalData() // Clear Baseline Db
                deleteAllDataUseCase.invoke() //Clear Grant Db
            } else {
                repository.clearSelectionLocalDB()
            }
            true
        } catch (exception: Exception) {
            CoreLogger.e(
                tag = "ClearLocalDBUseCase",
                msg = "Clear db exception ",
                ex = exception,
                stackTrace = true
            )

            false
        }
    }

    fun setAllDataSyncStatus() {
        repository.setAllDataSynced()
    }
}