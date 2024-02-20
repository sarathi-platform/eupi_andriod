package com.nudge.core.preference

import android.content.Context
import android.content.SharedPreferences
import com.nudge.core.LOCAL_BACKUP_FILE_NAME
import com.nudge.core.toDate


class CoreSharedPrefs private constructor(val context: Context) :
    CorePrefRepo {
    companion object {
        const val PREFS_NAME = "secured_nudge_prefs"
        const val PREF_FILE_BACKUP_NAME = "file_backup_name"

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
            LOCAL_BACKUP_FILE_NAME + "_" + mobileNo + System.currentTimeMillis().toDate().toString()
        )!!

    }

    override fun setBackupFileName(fileName: String) {

        prefs.edit().putString(PREF_FILE_BACKUP_NAME, fileName).apply()

    }
}