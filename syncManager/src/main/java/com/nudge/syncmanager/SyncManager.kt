package com.nudge.syncmanager

import android.util.Log
import androidx.work.WorkManager
import com.nudge.communicationModule.EventObserverInterface
import com.nudge.core.Core
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.preference.CorePrefRepo
import javax.inject.Inject

class SyncManager @Inject constructor(
    val eventsDao: EventsDao,
    val eventDependencyDao: EventDependencyDao,
    val eventStatusDao: EventStatusDao,
    val syncWorkManager: WorkManager
) {

    private var core: Core? = null

    init {
        initCore()
    }

    private fun initCore() {
        core = Core()
    }

    private var eventObserverInterface: EventObserverInterface? = null

    fun initEventObserver(): EventObserverInterface? {
        eventObserverInterface = EventObserverInterfaceImpl(eventsDao, eventDependencyDao,eventStatusDao,syncWorkManager)
        return eventObserverInterface
    }

    fun removeObserver() {
        eventObserverInterface = null
    }

}