package com.patsurvey.nudge.activities.settings.domain.use_case
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.patsurvey.nudge.activities.settings.domain.repository.SettingBSRepository

class ClearLocalDBUseCase(
    private val repository: SettingBSRepository
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
}