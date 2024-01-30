package com.nudge.syncmanager

import android.util.Log
import com.nudge.communicationModule.EventObserverInterface
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao

class SyncManager {
    init {
        Log.d("SyncManager", " SyncManager:init ")
    }

    private var eventObserverInterface: EventObserverInterface? = null

    fun init(eventsDao: EventsDao, eventDependencyDao: EventDependencyDao): EventObserverInterface? {
         eventObserverInterface = EventObserverInterfaceImpl(eventsDao, eventDependencyDao)
        return eventObserverInterface
    }

    fun removeObserver() {
        eventObserverInterface = null
    }

}