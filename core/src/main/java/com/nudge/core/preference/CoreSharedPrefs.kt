package com.nudge.core.preference

import android.content.Context
import android.content.SharedPreferences
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.nudge.core.DEFAULT_LANGUAGE_ID
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoreSharedPrefs @Inject constructor(@ApplicationContext private val context: Context) :
    CorePrefRepo {
    companion object {
        const val PREFS_NAME = "secured_nudge_prefs"
        const val PREF_FILE_BACKUP_NAME = "file_backup_name"
        const val PREF_IMAGE_FILE_BACKUP_NAME = "image_file_backup_name"
        const val PREF_IMAGE_FILE_EXPORTED_NAME = "is_file_exorted"
        const val PREF_MOBILE_NO_NAME = "pref_mobile_number"
        const val PREF_USER_TYPE = "type_name"
        const val PREF_KEY_NAME = "key_name"
        const val PREF_KEY_EMAIL = "key_email"
        const val PREF_KEY_IDENTITY_NUMBER = "key_identity_number"
        const val PREF_KEY_PROFILE_IMAGE = "profile_image"
        const val PREF_KEY_ROLE_NAME = "role_name"
        const val PREF_KEY_TYPE_NAME = "type_name"
        const val PREF_STATE_ID = "stateId"
        const val PREF_KEY_USER_NAME = "key_user_name"
        const val PREF_MOBILE_NUMBER = "pref_mobile_number"
        const val PREF_KEY_LANGUAGE_CODE = "language_code"
        const val PREF_KEY_LANGUAGE_ID = "language_id"

        const val PREF_CASTE_LIST = "caste_list"
        const val PREF_KEY_USER_ID = "user_id"
        const val PREF_KEY_DATA_LOADED = "is_data_loaded"
        const val PREF_KEY_DIDI_TAB_DATA_LOADED = "is_didi_tab_data_loaded"
        const val PREF_KEY_DATA_TAB_DATA_LOADED = "is_data_tab_data_loaded"


        @Volatile
        private var INSTANCE: CoreSharedPrefs? = null

        fun getInstance(context: Context): CoreSharedPrefs {
            return CoreSharedPrefs.INSTANCE ?: synchronized(this) {
                val instance = CoreSharedPrefs(context.applicationContext)
                CoreSharedPrefs.INSTANCE = instance
                instance
            }
        }
    }

    val prefs: SharedPreferences by lazy {

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    }

    override fun getBackupFileName(mobileNo: String): String {
        return prefs.getString(
            PREF_FILE_BACKUP_NAME,
            getDefaultBackUpFileName(mobileNo, getUserName())
        )!!

    }

    override fun setBackupFileName(fileName: String) {
        savePref(PREF_FILE_BACKUP_NAME, fileName)
    }

    override fun getImageBackupFileName(mobileNo: String): String {
        return prefs.getString(
            PREF_IMAGE_FILE_BACKUP_NAME,
            getDefaultImageBackUpFileName(mobileNo, getUserType())
        )!!
    }

    override fun setImageBackupFileName(fileName: String) {
        prefs.edit().putString(PREF_IMAGE_FILE_BACKUP_NAME, fileName).apply()
    }

    override fun isFileExported(): Boolean {
        return prefs.getBoolean(
            PREF_IMAGE_FILE_EXPORTED_NAME,
            false
        )
    }

    override fun setFileExported(isExported: Boolean) {
        prefs.edit().putBoolean(PREF_IMAGE_FILE_EXPORTED_NAME, isExported).apply()
    }

    override fun setMobileNo(mobileNo: String) {
        prefs.edit().putString(PREF_MOBILE_NO_NAME, mobileNo).apply()
    }

    override fun getMobileNo(): String {
        return prefs.getString(PREF_MOBILE_NO_NAME, BLANK_STRING) ?: BLANK_STRING
    }

    override fun getUserType(): String {
        return prefs.getString(PREF_USER_TYPE, BLANK_STRING) ?: BLANK_STRING
    }

    override fun setUserType(userTypes: String) {
        prefs.edit().putString(PREF_USER_TYPE, userTypes).apply()
    }

    override fun getUniqueUserIdentifier(): String {
        val userType = getUserType()
        val userMobile = getMobileNo()
        return "${userType}_${userMobile}"
    }

    override fun getAppLanguage(): String {
        return prefs.getString(PREF_KEY_LANGUAGE_CODE, DEFAULT_LANGUAGE_CODE)
            ?: DEFAULT_LANGUAGE_CODE
    }

    override fun saveAppLanguage(code: String?) {
        prefs.edit().putString(PREF_KEY_LANGUAGE_CODE, code).apply()
    }

    override fun getUserId(): String {
        return prefs.getString(PREF_KEY_USER_ID, BLANK_STRING)
            ?: BLANK_STRING
    }

    override fun saveUserId(userId: String) {
        prefs.edit().putString(PREF_KEY_USER_ID, userId).apply()
    }

    override fun setUserId(userId: String) {
        prefs.edit().putString(PREF_KEY_USER_ID, userId).apply()
    }

    override fun setDataLoaded(isDataLoaded: Boolean) {
        savePref(PREF_KEY_DATA_LOADED + getMobileNo(), isDataLoaded)
    }

    override fun isDataLoaded(): Boolean {
        return getPref(PREF_KEY_DATA_LOADED + getMobileNo(), false)
    }

    override fun isDidiTabDataLoaded(): Boolean {
        return getPref(PREF_KEY_DIDI_TAB_DATA_LOADED + getMobileNo(), false)
    }

    override fun setDidiTabDataLoaded(isDataLoaded: Boolean) {
        savePref(PREF_KEY_DIDI_TAB_DATA_LOADED + getMobileNo(), isDataLoaded)
    }

    override fun getSelectedLanguageCode(): String {
        return prefs.getString(PREF_KEY_LANGUAGE_CODE, DEFAULT_LANGUAGE_CODE)
            ?: DEFAULT_LANGUAGE_CODE
    }

    override fun getSelectedLanguageId(): Int {
        return prefs.getInt(PREF_KEY_LANGUAGE_ID, DEFAULT_LANGUAGE_ID) ?: 2
    }

    fun setUserName(userName: String) {
        savePref(PREF_KEY_USER_NAME, userName)
    }

    fun getUserName(): String {
        return getPref(PREF_KEY_USER_NAME, BLANK_STRING)
    }

    override fun getUserNameInInt(): Int {
        return getPref(PREF_KEY_USER_NAME, BLANK_STRING).toInt()
    }

    fun setUserEmail(email: String) {
        savePref(PREF_KEY_EMAIL, email)
    }

    fun getUserEmail(): String {
        return getPref(PREF_KEY_EMAIL, BLANK_STRING)
    }

    fun setStateId(stateId: Int) {
        savePref(PREF_STATE_ID, stateId)
    }

    fun getStateId(): Int {
        return getPref(PREF_STATE_ID, -1)
    }

    fun setUserRole(role: String) {
        savePref(PREF_KEY_ROLE_NAME, role)
    }

    fun getUserRole(): String {
        return getPref(PREF_KEY_ROLE_NAME, BLANK_STRING)
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
        TODO("Not yet implemented")
    }

    override fun savePref(key: String, value: Float) {
        TODO("Not yet implemented")
    }

    override fun getPref(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    override fun getPref(key: String, defaultValue: String): String {
        return prefs.getString(key, defaultValue) ?: BLANK_STRING
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

    override fun isDataTabDataLoaded(): Boolean {
        return prefs.getBoolean(PREF_KEY_DATA_TAB_DATA_LOADED, false)
    }

    override fun setDataTabDataLoaded(isDataLoaded: Boolean) {
        savePref(PREF_KEY_DATA_TAB_DATA_LOADED, isDataLoaded)
    }

}