package com.nudge.syncmanager

import android.util.Log
import com.nudge.communicationModule.EventObserverInterface
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import javax.inject.Inject

class SyncManager@Inject constructor(val eventsDao: EventsDao,val eventDependencyDao: EventDependencyDao ){
    init {
        Log.d("SyncManager", " SyncManager:init ")
    }

    private var eventObserverInterface: EventObserverInterface? = null

    fun init(): EventObserverInterface? {
         eventObserverInterface = EventObserverInterfaceImpl(eventsDao, eventDependencyDao)
        return eventObserverInterface
    }

    fun removeObserver() {
        eventObserverInterface = null
    }

}