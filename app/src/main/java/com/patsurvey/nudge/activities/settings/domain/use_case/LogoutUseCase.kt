package com.patsurvey.nudge.activities.settings.domain.use_case

import com.nrlm.baselinesurvey.SUCCESS
import com.patsurvey.nudge.activities.settings.domain.repository.SettingBSRepository
import com.patsurvey.nudge.utils.NudgeLogger

class LogoutUseCase(
    private val repository: SettingBSRepository
) {
    suspend operator fun invoke():Boolean{
        try {
         val logoutResponse = repository.performLogout()
            return if (logoutResponse.status.equals(SUCCESS, true)) {
                    repository.clearSharedPref()
                true
            } else {
                false
            }
        }catch (ex: Exception) {
            NudgeLogger.e("LogoutUseCase", "invoke", ex)
            return false
        }
    }
}
