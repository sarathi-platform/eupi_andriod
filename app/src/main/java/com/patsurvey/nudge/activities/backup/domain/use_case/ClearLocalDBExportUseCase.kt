package com.patsurvey.nudge.activities.backup.domain.use_case

import com.patsurvey.nudge.activities.backup.domain.repository.ExportImportRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.patsurvey.nudge.utils.UPCM_USER

class ClearLocalDBExportUseCase(
    private val repository: ExportImportRepository
) {
    suspend operator fun invoke():Boolean{
        return try {
            if (repository.getLoggedInUserType() == UPCM_USER)
                repository.clearLocalData()
            else repository.clearSelectionLocalDB()
            true
        }catch (exception:Exception){
            BaselineLogger.e("ClearLocalDBUseCase", "invoke", exception)
            false
        }
    }

    fun setAllDataSyncStatus(){
        repository.setAllDataSynced()
    }
}