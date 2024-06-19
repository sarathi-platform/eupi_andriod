package com.patsurvey.nudge.activities.settings.domain.use_case

import com.patsurvey.nudge.activities.settings.domain.repository.SettingBSRepository
import com.patsurvey.nudge.utils.NudgeLogger
import javax.inject.Inject

class ClearSelectionDBExportUseCase @Inject constructor(
    val repository:SettingBSRepository
) {
    suspend operator fun invoke():Boolean{
        return try {
            repository.clearSelectionLocalDB()
            true
        }catch (exception:Exception){
            NudgeLogger.e("ClearSelectionDBExportUseCase", "invoke", exception)
            false
        }
    }

    fun setAllDataSyncStatus(){
        repository.setAllDataSynced()
    }
}