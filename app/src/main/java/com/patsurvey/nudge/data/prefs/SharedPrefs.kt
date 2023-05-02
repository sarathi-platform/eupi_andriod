package com.patsurvey.nudge.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.patsurvey.nudge.data.prefs.StrictModePermitter.permitDiskReads
import com.patsurvey.nudge.utils.ACCESS_TOKEN
import com.patsurvey.nudge.utils.DEFAULT_LANGUAGE_CODE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefs @Inject constructor(@ApplicationContext private val ctx: Context) :PrefRepo {
    companion object {
        const val PREFS_NAME = "secured_nudge_prefs"
        const val PREF_KEY_LANGUAGE_CODE = "language_code"
        const val SELECTED_VILLAGE_ID = "selected_village_id"
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

    override fun saveSelectedVillage(id: Int) {
        prefs.edit().putInt(SELECTED_VILLAGE_ID, id).apply()
    }

    override fun getSelectedVillage(): Int {
        return prefs.getInt(SELECTED_VILLAGE_ID, 0)
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
}
