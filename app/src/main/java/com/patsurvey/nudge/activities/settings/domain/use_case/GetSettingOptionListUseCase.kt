package com.patsurvey.nudge.activities.settings.domain.use_case

import com.patsurvey.nudge.activities.settings.domain.repository.SettingBSRepository

class GetSettingOptionListUseCase(
    private val repository : SettingBSRepository
) {
   fun getUserType():String?{
       return repository.getUserType()
   }

    fun getSelectedVillageId():Int{
        return repository.getVillageId()
    }

    fun settingOpenFrom():Int{
        return repository.getSettingOpenFrom()
    }
}