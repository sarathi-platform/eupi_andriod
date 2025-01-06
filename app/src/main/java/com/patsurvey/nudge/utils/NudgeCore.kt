package com.patsurvey.nudge.utils

import android.content.Context
import android.content.Intent
import android.util.SparseArray
import androidx.core.util.forEach
import com.nrlm.baselinesurvey.utils.numberInEnglishFormat
import com.nudge.communicationModule.EventObserverInterface
import com.nudge.syncmanager.SyncManager
import com.patsurvey.nudge.MyApplication

object NudgeCore {

    private val TAG = NudgeCore::class.java.simpleName

    private var eventObserver: EventObserverInterface? = null
    private var eventObservations = SparseArray<EventObserverInterface>()

    fun getVoNameForState(
        context: Context,
        stateId: Int,
        value: Int,
        formatArgs: Int? = null
    ): String {
        return context.resources.getQuantityString(
            value,
            if (stateId == BENGAL_STATE_ID) BENGAL_STRING_VALUE else BENGAL_STRING_VALUE_OTHER,
            numberInEnglishFormat(formatArgs ?: 0, null)
        )
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

    fun addCommunicationObserver(observer: EventObserverInterface, name: String) {
        val id = System.identityHashCode(observer)
        eventObservations.put(id, observer)
    }

    fun <T> notifyEventObservers(event: T) {
        eventObservations.forEach { id, observer ->
            observer.onEventCallback(event)
        }
    }
}