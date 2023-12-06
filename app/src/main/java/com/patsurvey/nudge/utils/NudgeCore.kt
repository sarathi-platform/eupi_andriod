package com.patsurvey.nudge.utils

import android.content.Context
import android.content.Intent
import com.patsurvey.nudge.MyApplication
import java.util.ArrayList

object NudgeCore {

    private val TAG = NudgeCore::class.java.simpleName

    fun init (context: Context) {

    }

    fun getAppContext(): Context {
        return MyApplication.applicationContext()
    }

    fun startExternalApp(intent: Intent) {
        try {
            NudgeLogger.i(TAG, "startExternalApp() action: ${intent.action}")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            getAppContext().startActivity(intent)
        } catch (ex: Exception) {
            NudgeLogger.e(TAG, "startExternalActivity exception: ${ex.message}")
        }
    }

    fun getAppSystemService(name: String): Any? = MyApplication.applicationContext()?.getSystemService(name)

    private var isOnline: Boolean = false

    fun isOnline() = isOnline

    fun updateIsOnline(online: Boolean) {
        isOnline = online
    }

    fun cleanUp() {
    }

    fun preRequestCheck(): Boolean {
        try {
            if (!isOnline()) {
                NudgeLogger.e(TAG, "preRequestCheck Internet offline exception")
                return false
            }
        } catch (ex: Exception) {
            NudgeLogger.e(TAG, "preRequestCheck", ex)
            return false
        }
        return true
    }

}