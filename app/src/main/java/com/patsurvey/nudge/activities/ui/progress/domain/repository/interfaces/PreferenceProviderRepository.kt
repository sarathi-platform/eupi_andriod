package com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces

interface PreferenceProviderRepository {

    fun saveSettingOpenFrom(openFrom: Int)
    fun savePageOpenFromOTPScreen(status: Boolean)

    fun savePref(key: String, value: Int)
    fun getPref(key: String, defaultValue: Int): Int

    fun savePref(key: String, value: String)
    fun getPref(key: String, defaultValue: String): String


    fun savePref(key: String, value: Long)
    fun getPref(key: String, defaultValue: Long): Long


    fun savePref(key: String, value: Boolean)
    fun getPref(key: String, defaultValue: Boolean): Boolean

}