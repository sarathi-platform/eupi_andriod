package com.nudge.core.eventswriter

import android.content.Context
import android.net.Uri
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventWriterName

class DbEventWrite() : IEventWriter {
    override suspend fun addEvent(
        context: Context,
        event: Events,
        mobileNo: String,
        uri: Uri?,
        dependencyEntity: List<EventDependencyEntity>,
        eventsDao: EventsDao,
        eventDependencyDao: EventDependencyDao
    ) {
        eventsDao.insert(event)
        if (dependencyEntity.isNotEmpty())
            eventDependencyDao.insertAll(dependencyEntity)
    }

    override suspend fun getEventWriteType(): EventWriterName {
        return EventWriterName.DB_EVENT_WRITER
    }
}