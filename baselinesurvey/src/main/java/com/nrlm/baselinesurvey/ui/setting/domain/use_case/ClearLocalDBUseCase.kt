package com.nrlm.baselinesurvey.ui.setting.domain.use_case

import com.nrlm.baselinesurvey.ui.setting.domain.repository.SettingBSRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger

class ClearLocalDBUseCase(
    private val repository:SettingBSRepository
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