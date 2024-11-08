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

    fun savePref(key: String, value: Int) {
        preferenceProviderRepository.savePref(key, value)
    }

    fun getPref(key: String, defaultValue: Int): Int {
        return preferenceProviderRepository.getPref(key, defaultValue)
    }

    fun savePref(key: String, value: String) {
        preferenceProviderRepository.savePref(key, value)
    }

    fun getPref(key: String, defaultValue: String): String {
        return preferenceProviderRepository.getPref(key, defaultValue)
    }

    fun savePref(key: String, value: Long) {
        preferenceProviderRepository.savePref(key, value)
    }

    fun getPref(key: String, defaultValue: Long): Long {
        return preferenceProviderRepository.getPref(key, defaultValue)
    }

    fun savePref(key: String, value: Boolean) {
        preferenceProviderRepository.savePref(key, value)
    }

    fun getPref(key: String, defaultValue: Boolean): Boolean {
        return preferenceProviderRepository.getPref(key, defaultValue)
    }

}