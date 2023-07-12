package com.patsurvey.nudge.utils

import android.content.Context
import android.content.Intent
import com.patsurvey.nudge.MyApplication

object NudgeCore {

    private val TAG = NudgeCore::class.java.simpleName

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

}