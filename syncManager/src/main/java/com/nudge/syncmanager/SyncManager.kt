package com.nudge.syncmanager

import android.util.Log
import com.nudge.communicationModule.EventObserverInterface
import com.nudge.core.Core
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import javax.inject.Inject

class SyncManager @Inject constructor(
    val eventsDao: EventsDao,
    val eventDependencyDao: EventDependencyDao,
) {

    private var core: Core? = null

    init {
        Log.d("SyncManager", " SyncManager:init ")
        initCore()
    }

    private fun initCore() {
        core = Core()
    }

    private var eventObserverInterface: EventObserverInterface? = null

    fun initEventObserver(): EventObserverInterface? {
         eventObserverInterface = EventObserverInterfaceImpl(eventsDao, eventDependencyDao)
        return eventObserverInterface
    }

    fun removeObserver() {
        eventObserverInterface = null
    }

}