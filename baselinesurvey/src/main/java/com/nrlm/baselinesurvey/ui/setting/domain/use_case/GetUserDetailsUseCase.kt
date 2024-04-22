package com.nrlm.baselinesurvey.ui.setting.domain.use_case

import com.nrlm.baselinesurvey.ui.setting.domain.repository.SettingBSRepository

class GetUserDetailsUseCase(
    private val repository :SettingBSRepository
) {
    fun getUserName(): String {
        return repository.getUserName()
    }

    fun getMobileNo(): String {
        return repository.getMobileNo()
    }
}