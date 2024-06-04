package com.patsurvey.nudge.utils

import android.content.Context
import android.content.Intent
import com.nudge.communicationModule.EventObserverInterface
import com.nudge.syncmanager.SyncManager
import com.patsurvey.nudge.MyApplication
import com.patsurvey.nudge.data.prefs.PrefRepo
import javax.inject.Inject

object NudgeCore {

    private val TAG = NudgeCore::class.java.simpleName

    private var eventObserver: EventObserverInterface? = null

    fun getBengalString(context:Context, stateId :Int,value: Int): String {
      return  context.resources.getQuantityString(value,if (stateId == 34)1 else 2)
    }


    fun initEventObserver(syncManager: SyncManager) {
        eventObserver = syncManager.initEventObserver()
    }

    fun getEventObserver(): EventObserverInterface? {
        return eventObserver
    }

    fun removeEventObserver(syncManager: SyncManager) {
        eventObserver = null
        syncManager.removeObserver()
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

    fun getAppSystemService(name: String): Any? =
        MyApplication.applicationContext()?.getSystemService(name)

    private var isOnline: Boolean = false

    fun isOnline() = isOnline

    fun updateIsOnline(online: Boolean) {
        isOnline = online
    }

    fun cleanUp(syncManager: SyncManager) {
        removeEventObserver(syncManager)
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