package com.patsurvey.nudge.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.patsurvey.nudge.data.prefs.StrictModePermitter.permitDiskReads
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefs @Inject constructor(@ApplicationContext private val ctx: Context) :PrefRepo {
    companion object {
        const val PREFS_NAME = "secured_nudge_prefs"
        const val PREF_KEY_LANGUAGE_CODE = "language_code"
        const val PREF_KEY_LANGUAGE_ID = "language_id"
        const val PREF_KEY_PAGE_FROM = "page_from"
        const val PREF_KEY_STEP_ID = "step_id"
        const val PREF_KEY_LAST_TOLA_ID = "last_tola_id"
        const val PREF_KEY_LAST_TOLA_NAME = "last_tola_name"
        const val SELECTED_VILLAGE_ID = "selected_village_id"
        const val PREF_KEY_SELECTED_VILLAGE = "selected_village"
        const val PREF_KEY_USER_BPC = "is_user_bpc"
    }

    val prefs: SharedPreferences by lazy {
        permitDiskReads {
            ctx.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
        }
    }
    override fun getAppLanguage(): String? {
        return prefs.getString(PREF_KEY_LANGUAGE_CODE, DEFAULT_LANGUAGE_CODE)
    }

    override fun saveAppLanguage(code: String?) {
        prefs.edit().putString(PREF_KEY_LANGUAGE_CODE, code).apply()
    }


    override fun isPermissionGranted(): Boolean {
        /*TODO("Not yet implemented")*/
        return false
    }

    override fun savePermissionGranted(isGranted: Boolean?) {
        /*TODO("Not yet implemented")*/
    }

    override fun saveSelectedVillage(village: VillageEntity) {
        prefs.edit().putString(PREF_KEY_SELECTED_VILLAGE, Gson().toJson(village)).apply()
    }

    override fun getSelectedVillage(): VillageEntity {
        return Gson().fromJson(prefs.getString(PREF_KEY_SELECTED_VILLAGE, "{}"), VillageEntity::class.java)
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

    override fun setOnlineStatus(isOnline: Boolean) {
        prefs.edit().putBoolean(ONLINE_STATUS, isOnline).apply()
    }

    override fun getOnlinceStatus(): Boolean {
        return prefs.getBoolean(ONLINE_STATUS, false)
    }

    override fun saveMobileNumber(mobileNumber: String) {
        prefs.edit().putString(PREF_MOBILE_NUMBER, mobileNumber).apply()
    }

    override fun getMobileNumber(): String? {
        return prefs.getString(PREF_MOBILE_NUMBER, "")
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

    override fun getAppLanguageId(): Int? {
        return prefs.getInt(PREF_KEY_LANGUAGE_ID, 1)
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

    override fun getStepId(): Int {
        return prefs.getInt(PREF_KEY_STEP_ID, 0)
    }

    override fun saveStepId(stepId: Int) {
        prefs.edit().putInt(PREF_KEY_STEP_ID,stepId).apply()
    }

    override fun clearSharedPreference() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    override fun setIsUserBPC(isOnline: Boolean) {
       prefs.edit().putBoolean(PREF_KEY_USER_BPC,isOnline).apply()
    }

    override fun isUserBPC(): Boolean {
       return prefs.getBoolean(PREF_KEY_USER_BPC,false)
    }
}
