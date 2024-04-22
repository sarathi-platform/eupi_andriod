package com.nrlm.baselinesurvey.ui.backup.domain.use_case

import com.nrlm.baselinesurvey.ui.backup.domain.repository.ExportImportRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger

class ClearLocalDBExportUseCase(
    private val repository:ExportImportRepository
) {
    suspend operator fun invoke():Boolean{
        return try {
            repository.clearLocalData()
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