package com.nrlm.baselinesurvey.ui.setting.domain.use_case

import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.ui.setting.domain.repository.SettingBSRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger

class LogoutUseCase(
    private val repository: SettingBSRepository
) {
    suspend operator fun invoke(): Boolean {
        try {
            val logoutResponse = repository.performLogout()
            return if (logoutResponse.status.equals(SUCCESS, true)) {
                repository.clearSharedPref()
                true
            } else {
                false
            }
        } catch (ex: Exception) {
            BaselineLogger.e("LogoutUseCase", "invoke", ex)
            return false
        }
    }
}
