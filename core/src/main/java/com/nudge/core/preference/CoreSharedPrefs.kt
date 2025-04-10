package com.nudge.core.preference

import android.content.Context
import android.content.SharedPreferences
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName


class CoreSharedPrefs private constructor(val context: Context) :
    CorePrefRepo {
    companion object {
        const val PREFS_NAME = "secured_nudge_prefs"
        const val PREF_FILE_BACKUP_NAME = "file_backup_name"
        const val PREF_IMAGE_FILE_BACKUP_NAME = "image_file_backup_name"
        const val PREF_IMAGE_FILE_EXPORTED_NAME = "is_file_exorted"

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

}