package com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces

interface PreferenceProviderRepository {

    fun saveSettingOpenFrom(openFrom: Int)
    fun savePageOpenFromOTPScreen(status: Boolean)

}