package com.nudge.core.eventswriter

import android.content.Context
import android.net.Uri
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.dao.ImageStatusDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventWriterName
import com.nudge.core.json
import com.nudge.core.model.request.toEventRequest
import com.nudge.core.utils.CoreLogger

class LogEventWriter() : IEventWriter {
    override suspend fun addEvent(
        context: Context,
        event: Events,
        mobileNo: String,
        uri: Uri?,
        dependencyEntity: List<EventDependencyEntity>,
        eventsDao: EventsDao,
        eventStatusDao: EventStatusDao,
        eventDependencyDao: EventDependencyDao,
        imageStatusDao: ImageStatusDao
    ) {

        CoreLogger.d(context, EventWriterName.LOG_EVENT_WRITER.name, event.toEventRequest().json())
        if (uri != null)
            CoreLogger.d(context, EventWriterName.LOG_EVENT_WRITER.name, uri.path.toString())
    }

    override suspend fun getEventWriteType(): EventWriterName {
        return EventWriterName.LOG_EVENT_WRITER
    }

    override suspend fun addFailedEventIntoFile(
        context: Context,
        event: Events,
        mobileNo: String,
        uri: Uri?,
        dependencyEntity: List<EventDependencyEntity>,
        eventsDao: EventsDao,
        eventStatusDao: EventStatusDao,
        eventDependencyDao: EventDependencyDao,
        imageStatusDao: ImageStatusDao,
        fileNameWithoutExtension: String
    ) {
        //("Not yet implemented")
    }
}