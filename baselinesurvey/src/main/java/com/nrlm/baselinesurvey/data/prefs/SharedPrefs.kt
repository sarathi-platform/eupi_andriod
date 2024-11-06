package com.nrlm.baselinesurvey.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.nrlm.baselinesurvey.ACCESS_TOKEN
import com.nrlm.baselinesurvey.ARG_FROM_HOME
import com.nrlm.baselinesurvey.ARG_PAGE_FROM
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_IS_DATA_SYNC
import com.nrlm.baselinesurvey.PREF_KEY_LANGUAGE_CODE
import com.nrlm.baselinesurvey.PREF_KEY_LANGUAGE_ID
import com.nrlm.baselinesurvey.PREF_KEY_PAGE_FROM
import com.nrlm.baselinesurvey.PREF_KEY_SELECTED_VILLAGE
import com.nrlm.baselinesurvey.PREF_KEY_SETTING_OPEN_FROM
import com.nrlm.baselinesurvey.PREF_KEY_TYPE_NAME
import com.nrlm.baselinesurvey.PREF_KEY_USER_NAME
import com.nrlm.baselinesurvey.PREF_MOBILE_NUMBER
import com.nrlm.baselinesurvey.data.prefs.StrictModePermitter.permitDiskReads
import com.nrlm.baselinesurvey.database.entity.VillageEntity
import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_STATE_ID
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_USER_TYPE
import com.nudge.core.value
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefs @Inject constructor(@ApplicationContext private val ctx: Context) :PrefBSRepo {
    companion object {
//        const val PREFS_NAME = "secured_nudge_baseline_prefs"
        const val PREFS_NAME = "secured_nudge_prefs"
        const val PREF_KEY_PREVIOUS_USER_MOBILE = "previous_user_mobile"

    }

    val prefs: SharedPreferences by lazy {
        permitDiskReads {
            ctx.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
        }
    }


    override fun getLoginStatus(): Boolean {
        return !prefs.getString(ACCESS_TOKEN, null).isNullOrBlank()
    }


    override fun getAccessToken(): String? {
        return prefs.getString(ACCESS_TOKEN, "")
    }

    override fun saveAccessToken(token: String) {
        prefs.edit().putString(ACCESS_TOKEN, token).apply()
    }

    override fun getAppLanguage(): String? {
        return prefs.getString(PREF_KEY_LANGUAGE_CODE, DEFAULT_LANGUAGE_CODE)
    }

    override fun saveAppLanguage(code: String?) {
        prefs.edit().putString(PREF_KEY_LANGUAGE_CODE, code).apply()
    }

    override fun saveSelectedVillage(village: VillageEntity) {
        prefs.edit().putString(PREF_KEY_SELECTED_VILLAGE, Gson().toJson(village)).apply()
    }

    override fun getSelectedVillage(): VillageEntity {
        val defaultVillageEntity= VillageEntity(id = 0, languageId = 2, name = BLANK_STRING, federationName = BLANK_STRING, stateId = 0, steps_completed = listOf(), needsToPost = false, localVillageId = 0)
        return Gson().fromJson(prefs.getString(PREF_KEY_SELECTED_VILLAGE, Gson().toJson(defaultVillageEntity)), VillageEntity::class.java)

    }

    override fun getAppLanguageId(): Int? {
        return prefs.getInt(PREF_KEY_LANGUAGE_ID, 2) ?: 2
    }

    override fun saveAppLanguageId(languageId: Int?) {
        languageId?.let {
            prefs.edit().putInt(PREF_KEY_LANGUAGE_ID, languageId).apply()
        }
    }

    override fun getFromPage(): String {
        return prefs.getString(PREF_KEY_PAGE_FROM, ARG_FROM_HOME) ?: ARG_PAGE_FROM
    }

    override fun saveFromPage(pageFrom: String) {
        prefs.edit().putString(PREF_KEY_PAGE_FROM,pageFrom).apply()
    }

    override fun settingOpenFrom(): Int {
        return prefs.getInt(PREF_KEY_SETTING_OPEN_FROM,0)
    }

    override fun saveSettingOpenFrom(pageFrom: Int) {
      prefs.edit().putInt(PREF_KEY_SETTING_OPEN_FROM,pageFrom).apply()
    }

    override fun savePref(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun savePref(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    override fun savePref(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun savePref(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    override fun savePref(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    override fun getPref(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    override fun getPref(key: String, defaultValue: String): String? {
        return prefs.getString(key, defaultValue)
    }

    override fun getPref(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    override fun getPref(key: String, defaultValue: Long): Long {
        return prefs.getLong(key, defaultValue)
    }

    override fun getPref(key: String, defaultValue: Float): Float {
        return prefs.getFloat(key, defaultValue)
    }

    override fun saveMobileNumber(mobileNumber: String) {
        prefs.edit().putString(PREF_MOBILE_NUMBER, mobileNumber).apply()
    }

    override fun getMobileNumber(): String {
        return prefs.getString(PREF_MOBILE_NUMBER, BLANK_STRING) ?: BLANK_STRING
    }

    override fun getUserId(): String {
        return prefs.getString(PREF_KEY_USER_NAME, "") ?: ""
    }

    override fun clearSharedPreference() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    override fun setDataSyncStatus(status: Boolean) {
        prefs.edit().putBoolean(PREF_KEY_IS_DATA_SYNC + getMobileNumber(), status).apply()
    }

    override fun getDataSyncStatus(): Boolean {
        return prefs.getBoolean(PREF_KEY_IS_DATA_SYNC + getMobileNumber(), false)
    }

    override fun setPreviousUserMobile(mobileNumber: String) {
        prefs.edit().putString(PREF_KEY_PREVIOUS_USER_MOBILE, mobileNumber).apply()
    }

    override fun getPreviousUserMobile(): String {
        return prefs.getString(PREF_KEY_PREVIOUS_USER_MOBILE, "") ?: ""
    }

    override fun getUniqueUserIdentifier(): String {
        val userType = prefs.getString(PREF_KEY_TYPE_NAME, BLANK_STRING) ?: BLANK_STRING
        val userMobile = prefs.getString(PREF_MOBILE_NUMBER, BLANK_STRING) ?: BLANK_STRING
        return "${userType}_${userMobile}"
    }

    override fun getStateId(): Int {
        return getPref(PREF_STATE_ID, -1)
    }

    override fun getUserType(): String {
        return prefs.getString(
            PREF_USER_TYPE,
            BLANK_STRING
        ).value() ?: BLANK_STRING
    }
}
