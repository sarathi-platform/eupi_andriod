package com.patsurvey.nudge.utils

import android.content.Context
import android.content.Intent
import com.nudge.communicationModule.EventObserverInterface
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.syncmanager.SyncManager
import com.patsurvey.nudge.MyApplication

object NudgeCore {

    private val TAG = NudgeCore::class.java.simpleName

    private var eventObserver: EventObserverInterface? = null

    private var syncManager: SyncManager? = null

    fun init (context: Context) {
        syncManager = SyncManager()
    }

    fun initEventObserver(eventsDao: EventsDao, eventDependencyDao: EventDependencyDao) {
        eventObserver = syncManager?.init(eventsDao, eventDependencyDao)
    }

    fun getEventObserver(): EventObserverInterface? {
        return eventObserver
    }

    fun removeEventObserver() {
        eventObserver = null
        syncManager?.removeObserver()
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
        removeEventObserver()
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