package com.patsurvey.nudge.activities.settings.domain.use_case

import android.content.Context
import com.patsurvey.nudge.activities.settings.domain.repository.SettingBSRepository

class ExportHandlerSettingUseCase(
    val repository:SettingBSRepository
) {
    suspend fun exportAllData(context:Context){
      repository.exportAllFiles(context)
    }
}