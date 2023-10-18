package com.nrlm.baselinesurvey.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.nrlm.baselinesurvey.ACCESS_TOKEN
import com.nrlm.baselinesurvey.data.prefs.StrictModePermitter.permitDiskReads
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefs @Inject constructor(@ApplicationContext private val ctx: Context) :PrefRepo {
    companion object {
        const val PREFS_NAME = "secured_nudge_baseline_prefs"
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


}
