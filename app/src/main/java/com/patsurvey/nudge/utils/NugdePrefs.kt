package com.patsurvey.nudge.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.patsurvey.nudge.MyApplication

object NugdePrefs {

    private val TAG = NugdePrefs::class.java.simpleName



    private const val PREF_FILE = "NudgePrefs"

    @Volatile
    private var appSharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        try {
            appSharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        } catch (ex: Exception) {
            ex.message?.let { Log.e(TAG, it) }
        }
    }

    fun clearPrefs() {
        appSharedPreferences?.edit()?.let { editor ->
            editor.clear()
            editor.apply()
        }
    }

    fun cleanup() {
        if (appSharedPreferences != null) {
            appSharedPreferences = null
        }
    }

    fun getPref(name: String, defaultValue: Boolean): Boolean {
        return appSharedPreferences?.getBoolean(name, defaultValue) ?: defaultValue
    }

    fun getPref(name: String, defaultValue: Int): Int {
        return try {
            appSharedPreferences?.getInt(name, defaultValue) ?: defaultValue
        } catch (fault: ClassCastException) {
            Log.e(TAG, "getPref($name, Int)", fault)
            appSharedPreferences?.getLong(name, defaultValue.toLong())?.toInt() ?: defaultValue
        } catch (ex: Exception) {
            Log.e(TAG, "getPref($name, Int)", ex)
            defaultValue
        }
    }

    fun getPref(name: String, defaultValue: Long): Long {
        return try {
            appSharedPreferences?.getLong(name, defaultValue) ?: defaultValue
        } catch (fault: ClassCastException) {
            Log.e(TAG, "getPref($name, Long)", fault)
            appSharedPreferences?.getInt(name, defaultValue.toInt())?.toLong() ?: defaultValue
        } catch (ex: Exception) {
            Log.e(TAG, "getPref($name, Long)", ex)
            defaultValue
        }
    }

    fun getPref(name: String, defaultValue: Float): Float {
        return try {
            appSharedPreferences?.getFloat(name, defaultValue) ?: defaultValue
        } catch (fault: ClassCastException) {
            Log.e(TAG, "getPref($name, Float)", fault)
            getPref(name, defaultValue.toLong()).toFloat()
        } catch (ex: Exception) {
            Log.e(TAG, "getPref($name, Float)", ex)
            defaultValue
        }
    }

    fun getPref(name: String, defaultValue: String = ""): String {
        return appSharedPreferences?.getString(name, defaultValue) ?: defaultValue
    }

    fun setPref(name: String, value: Boolean): Boolean {
        appSharedPreferences?.edit()?.putBoolean(name, value)?.apply()
        return value
    }

    fun setPref(name: String, value: Int): Int {
        appSharedPreferences?.edit()?.putInt(name, value)?.apply()
        return value
    }

    fun setPref(name: String, value: Long): Long {
        appSharedPreferences?.edit()?.putLong(name, value)?.apply()
        return value
    }

    fun setPref(name: String, value: Float): Float {
        appSharedPreferences?.edit()?.putFloat(name, value)?.apply()
        return value
    }

    fun setPref(name: String, value: String): String {
        appSharedPreferences?.edit()?.putString(name, value)?.apply()
        return value
    }

}