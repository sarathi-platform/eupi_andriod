package com.nudge.syncmanager

import com.nudge.communicationModule.EventObserverInterface
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events

class EventObserverInterfaceImpl(val eventsDao: EventsDao, val eventDependencyDao: EventDependencyDao): EventObserverInterface {

    override fun <T> onEventCallback(event: T) {

    }

    override suspend fun addEvent(event: Events) {
        eventsDao.insert(event)
    }

    override suspend fun addEvents(events: List<Events>) {
        eventsDao.insertAll(events)
    }

    override suspend fun addEventDependency(eventDependency: EventDependencyEntity) {
        eventDependencyDao.insert(eventDependency)
    }

    override suspend fun addEventDependencies(eventDependencies: List<EventDependencyEntity>) {
        eventDependencyDao.insertAll(eventDependencies)
    }


}