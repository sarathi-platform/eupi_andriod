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

    override fun savePref(key: String, value: Int) {
        coreSharedPrefs.savePref(key, value)
    }

    override fun getPref(key: String, defaultValue: Int): Int {
        return coreSharedPrefs.getPref(key, defaultValue)
    }

    override fun savePref(key: String, value: String) {
        coreSharedPrefs.savePref(key, value)
    }

    override fun getPref(key: String, defaultValue: String): String {
        return coreSharedPrefs.getPref(key, defaultValue)
    }

    override fun savePref(key: String, value: Long) {
        coreSharedPrefs.savePref(key, value)
    }

    override fun getPref(key: String, defaultValue: Long): Long {
        return coreSharedPrefs.getPref(key, defaultValue)
    }

    override fun savePref(key: String, value: Boolean) {
        coreSharedPrefs.savePref(key, value)
    }

    override fun getPref(key: String, defaultValue: Boolean): Boolean {
        return coreSharedPrefs.getPref(key, defaultValue)
    }



}