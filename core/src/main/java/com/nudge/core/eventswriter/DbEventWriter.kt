package com.nudge.core.eventswriter

import android.content.Context
import android.net.Uri
import android.util.Log
import com.nudge.core.BLANK_STRING
import com.nudge.core.EventSyncStatus
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.EventStatusEntity
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
        eventStatusDao: EventStatusDao,
        eventDependencyDao: EventDependencyDao
    ) {
        Log.d("TAG", "addEvent DbEventWrite: ${event.id}")

        eventsDao.insert(event)

            eventStatusDao.insert(
                EventStatusEntity(
                    clientId = event.id,
                    name = event.name,
                    errorMessage = BLANK_STRING,
                    type = event.type,
                    status = EventSyncStatus.OPEN.eventSyncStatus,
                    mobileNumber = event.mobile_number,
                    createdBy = event.createdBy,
                    eventStatusId = 0
                )
            )
        if (dependencyEntity.isNotEmpty())
            eventDependencyDao.insertAll(dependencyEntity)
    }

    override suspend fun getEventWriteType(): EventWriterName {
        return EventWriterName.DB_EVENT_WRITER
    }
}