package com.nudge.core

import android.util.SparseArray
import androidx.core.util.forEach
import com.nudge.core.utils.CoreLogger

object CoreObserverManager {

    private val tag = CoreObserverManager::class.java.simpleName

    val observers = SparseArray<CoreObserverInterface>()

    fun addObserver(observer: CoreObserverInterface) {
        val id = System.identityHashCode(observer)
        observers.put(id, observer)
    }

    fun removeObserver(observer: CoreObserverInterface) {
        val id = System.identityHashCode(observer)
        observers.remove(id)
    }

    /*fun notifyCoreObserversBaselineActivityStatusUpdated(activityId: Int, status: String, missionId: Int) {
        observers.forEach { id, observer ->
            try {
                observer.baselineActivityStatusUpdated(
                    activityId = activityId,
                    status = status,
                    missionId = missionId
                )
            } catch (ex: Exception) {
                CoreLogger.e(tag = tag, msg = "notifyCoreObserversBaselineActivityStatusUpdated: exception -> $ex", ex = ex, stackTrace = true)
            }

        }
    }*/

    fun notifyCoreObserversUpdateMissionActivityStatusOnGrantInit(onSuccess: (isSuccess: Boolean) -> Unit) {
        observers.forEach { id, observer ->
            try {
                observer.updateMissionActivityStatusOnGrantInit() {
                    onSuccess(it)
                }
            } catch (ex: Exception) {
                CoreLogger.e(
                    tag = tag,
                    msg = "notifyCoreObserversUpdateMissionActivityStatusOnGrantInit: exception -> $ex",
                    ex = ex,
                    stackTrace = true
                )
                onSuccess(false)
            }

        }
    }


}