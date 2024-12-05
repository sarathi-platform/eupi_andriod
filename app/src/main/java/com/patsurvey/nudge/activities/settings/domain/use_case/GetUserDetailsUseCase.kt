package com.patsurvey.nudge.activities.settings.domain.use_case

import com.patsurvey.nudge.activities.settings.domain.repository.SettingBSRepository

class GetUserDetailsUseCase(
    private val repository: SettingBSRepository
) {

    fun getUserMobileNumber():String{
        return repository.getUserMobileNumber()
    }
    fun getUserID():String{
        return repository.getUserID()
    }

    fun getUserEmail():String{
        return repository.getUserEmail()
    }

    fun getUserName():String{
        return repository.getUserName()
    }

    fun isSyncEnable() = repository.isSyncEnable()

}