package com.patsurvey.nudge.data.prefs

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.nrlm.baselinesurvey.PREF_KEY_IS_DATA_SYNC
import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.nudge.core.PREF_BUILD_ENVIRONMENT
import com.nudge.core.REMOTE_CONFIG_SYNC_OPTION_ENABLE
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_SYNC_BATCH_SIZE
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_SYNC_ENABLED
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_SYNC_RETRY_COUNT
import com.patsurvey.nudge.data.prefs.StrictModePermitter.permitDiskReads
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.utils.ACCESS_TOKEN
import com.patsurvey.nudge.utils.ARG_FROM_HOME
import com.patsurvey.nudge.utils.ARG_PAGE_FROM
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DEFAULT_STATE_ID
import com.patsurvey.nudge.utils.ONLINE_STATUS
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_STATE_ID
import com.patsurvey.nudge.utils.PREF_KEY_USER_NAME
import com.patsurvey.nudge.utils.PREF_MOBILE_NUMBER
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
        const val PREF_KEY_SELECTED_VILLAGE = "selected_village"
        const val PREF_KEY_USER_BPC = "is_user_bpc"
        const val PREF_KEY_LAST_SYNC_TIME = "last_sync_time"
        const val PREF_KEY_SETTING_OPEN_FROM = "setting_open_from"
        const val PREF_KEY_QUESTIONS_OPEN_FROM = "questions_open_from"
        const val PREF_KEY_VO_SUMMARY_OPEN_FROM = "questions_open_from"
        const val PREF_KEY_QUESTIONS_SUMMARY_OPEN_FROM = "questions_summary_open_from"
        const val PREF_KEY_NEED_TO_SCROLL = "questions_need_to_scroll"
        const val PREF_KEY_PREVIOUS_USER_MOBILE = "previous_user_mobile"
        const val PREF_KEY_FROM_OTP_SCREEN = "from_otp_screen"

        const val PREF_DATA_TAB_VISIBILITY = "data_tab_visibility"

    }

    val prefs: SharedPreferences by lazy {
        permitDiskReads {
            ctx.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
        }
    }

    override fun getStateId(): Int {
        return prefs.getInt(PREF_KEY_TYPE_STATE_ID, DEFAULT_STATE_ID)
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
        val defaultVillageEntity= VillageEntity(id = 0, languageId = 2, name = BLANK_STRING, federationName = BLANK_STRING, stateId = 0, steps_completed = listOf(), needsToPost = false, localVillageId = 0)
        return Gson().fromJson(prefs.getString(PREF_KEY_SELECTED_VILLAGE, Gson().toJson(defaultVillageEntity)), VillageEntity::class.java)
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

    override fun getMobileNumber(): String {
        return prefs.getString(PREF_MOBILE_NUMBER, "") ?: ""
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

    override fun setIsUserBPC(isBpcUser: Boolean) {
       prefs.edit().putBoolean(PREF_KEY_USER_BPC, isBpcUser).apply()
    }

    override fun isUserBPC(): Boolean {
       return prefs.getBoolean(PREF_KEY_USER_BPC,false)
    }

    override fun setLastSyncTime(lastSyncTime: Long) {
        prefs.edit().putLong(PREF_KEY_LAST_SYNC_TIME,lastSyncTime).apply()
    }

    override fun getLastSyncTime(): Long {
        return prefs.getLong(PREF_KEY_LAST_SYNC_TIME,0)
    }

    override fun saveSettingOpenFrom(openFrom: Int) {
        prefs.edit().putInt(PREF_KEY_SETTING_OPEN_FROM,openFrom).apply()

    }

    override fun settingOpenFrom(): Int {
        return prefs.getInt(PREF_KEY_SETTING_OPEN_FROM,0)
    }

    override fun saveQuestionScreenOpenFrom(openFrom: Int) {
        prefs.edit().putInt(PREF_KEY_QUESTIONS_OPEN_FROM,openFrom).apply()

    }

    override fun questionScreenOpenFrom(): Int {
        return prefs.getInt(PREF_KEY_QUESTIONS_OPEN_FROM,0)

    }

    override fun saveSummaryScreenOpenFrom(openFrom: Int) {
         prefs.edit().putInt(PREF_KEY_QUESTIONS_SUMMARY_OPEN_FROM,openFrom).apply()
    }

    override fun summaryScreenOpenFrom(): Int {
        return prefs.getInt(PREF_KEY_QUESTIONS_SUMMARY_OPEN_FROM,0)
    }

    override fun saveNeedQuestionToScroll(needToScroll: Boolean) {
        Log.d("TAG", "saveNeedQuestionToScroll: $needToScroll")
        prefs.edit().putBoolean(PREF_KEY_NEED_TO_SCROLL,needToScroll).apply()
    }

    override fun isNeedQuestionToScroll(): Boolean {
        return prefs.getBoolean(PREF_KEY_NEED_TO_SCROLL,false)
    }

    override fun getUserId(): String {
        return prefs.getString(PREF_KEY_USER_NAME, "") ?: ""
    }

    override fun saveIsSyncEnabled(isEnabled: Boolean) {
        prefs.edit().putBoolean(PREF_KEY_SYNC_ENABLED, isEnabled).apply()
    }

    override fun getISSyncEnabled(): Boolean {
        return prefs.getBoolean(PREF_KEY_SYNC_ENABLED, false)

    }

    override fun getPreviousUserMobile(): String {
        return prefs.getString(PREF_KEY_PREVIOUS_USER_MOBILE, "") ?: ""
    }

    override fun setPreviousUserMobile(mobileNumber: String) {
        prefs.edit().putString(PREF_KEY_PREVIOUS_USER_MOBILE, mobileNumber).apply()
    }

    override fun setDataSyncStatus(status: Boolean) {
        prefs.edit().putBoolean(PREF_KEY_IS_DATA_SYNC + getMobileNumber(), status).apply()
    }

    override fun getLoggedInUserType(): String {
        return prefs.getString(PREF_KEY_TYPE_NAME, BLANK_STRING) ?: BLANK_STRING
    }

    override fun savePageOpenFromOTPScreen(status: Boolean) {
        prefs.edit().putBoolean(PREF_KEY_FROM_OTP_SCREEN, status).apply()

    }

    override fun getPageOpenFromOTPScreen(): Boolean {
        return prefs.getBoolean(PREF_KEY_FROM_OTP_SCREEN, false)
    }

    override fun saveSyncBatchSize(batchSize: Long) {
        prefs.edit().putLong(PREF_KEY_SYNC_BATCH_SIZE, batchSize).apply()
    }

    override fun saveSyncRetryCount(retryCount: Long) {
        prefs.edit().putLong(PREF_KEY_SYNC_RETRY_COUNT, retryCount).apply()
    }

    override fun setSyncOptionEnabled(isEnabled: Boolean) {
        prefs.edit().putBoolean(REMOTE_CONFIG_SYNC_OPTION_ENABLE, isEnabled).apply()
    }

    override fun iSSyncOptionEnabled(): Boolean {
        return prefs.getBoolean(REMOTE_CONFIG_SYNC_OPTION_ENABLE, false)
    }

    override fun saveDataTabVisibility(isEnabled: Boolean) {
        savePref(PREF_DATA_TAB_VISIBILITY, isEnabled)
    }

    override fun isDataTabVisible(): Boolean = getPref(PREF_DATA_TAB_VISIBILITY, false)

    override fun getBuildEnvironment(): String {
        return getPref(PREF_BUILD_ENVIRONMENT, "uat") ?: "uat"
    }

    override fun saveBuildEnvironment(buildEnv: String) {
        savePref(PREF_BUILD_ENVIRONMENT, buildEnv)
    }

}
