package com.patsurvey.nudge.activities.ui.progress.domain.useCase

import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.PreferenceProviderRepository
import javax.inject.Inject

class PreferenceProviderUseCase @Inject constructor(
    private val preferenceProviderRepository: PreferenceProviderRepository
) {

    fun saveSettingOpenFrom(openFrom: Int) {
        preferenceProviderRepository.saveSettingOpenFrom(openFrom)
    }

    fun savePageOpenFromOTPScreen(status: Boolean) {
        preferenceProviderRepository.savePageOpenFromOTPScreen(status)
    }

}