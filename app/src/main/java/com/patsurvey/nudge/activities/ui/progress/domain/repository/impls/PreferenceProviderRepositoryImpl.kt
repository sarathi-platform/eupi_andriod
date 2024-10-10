package com.patsurvey.nudge.activities.ui.progress.domain.repository.impls

import com.nudge.core.preference.CoreSharedPrefs
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.PreferenceProviderRepository
import com.patsurvey.nudge.data.prefs.SharedPrefs
import javax.inject.Inject

class PreferenceProviderRepositoryImpl @Inject constructor(
    private val selectionSharedPrefs: SharedPrefs,
    private val coreSharedPrefs: CoreSharedPrefs
) : PreferenceProviderRepository {

    override fun saveSettingOpenFrom(openFrom: Int) {
        selectionSharedPrefs.saveSettingOpenFrom(openFrom)
    }

    override fun savePageOpenFromOTPScreen(status: Boolean) {
        selectionSharedPrefs.savePageOpenFromOTPScreen(status)
    }


}