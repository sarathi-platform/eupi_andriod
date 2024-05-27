package com.nudge.core.preference

import android.content.Context
import android.content.SharedPreferences
import com.nudge.core.BLANK_STRING
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
        const val PREF_MOBILE_NO_NAME = "mobileNo"
        const val PREF_USER_TYPE = "userType"


        const val PREF_KEY_USER_NAME = "key_user_name"

        const val PREF_USER_TYPE = "pref_user_type"
        const val PREF_MOBILE_NUMBER = "pref_mobile_number"


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
            getDefaultBackUpFileName(mobileNo)
        )!!

    }

    override fun setBackupFileName(fileName: String) {

        prefs.edit().putString(PREF_FILE_BACKUP_NAME, fileName).apply()

    }

    override fun getImageBackupFileName(mobileNo: String): String {
        return prefs.getString(
            PREF_IMAGE_FILE_BACKUP_NAME,
            getDefaultImageBackUpFileName(mobileNo)
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

    override fun getUserId(): String {
        return prefs.getString(PREF_KEY_USER_NAME, BLANK_STRING) ?: BLANK_STRING
    }

    override fun saveUserId(userId: String) {
        prefs.edit().putString(PREF_KEY_USER_NAME, userId).apply()
    }

    override fun getUniqueUserIdentifier(): String {
        val userType = prefs.getString(PREF_USER_TYPE, BLANK_STRING) ?: BLANK_STRING
        val userMobile = prefs.getString(PREF_MOBILE_NUMBER, BLANK_STRING) ?: BLANK_STRING
        return "${userType}_${userMobile}"
    }

}