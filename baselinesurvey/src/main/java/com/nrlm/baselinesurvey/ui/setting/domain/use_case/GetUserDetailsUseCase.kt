package com.nrlm.baselinesurvey.ui.setting.domain.use_case

import com.nrlm.baselinesurvey.ui.setting.domain.repository.SettingBSRepository

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

    fun getUserName(): String {
        return repository.getUserName()
    }

    fun getMobileNo(): String {
        return repository.getMobileNo()
    }
}