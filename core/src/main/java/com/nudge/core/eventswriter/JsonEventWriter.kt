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
import com.nudge.core.eventWriters

class JsonEventWriter(
    val context: Context,
    val eventsDao: EventsDao,
    val eventStatusDao: EventStatusDao,
    val eventDependencyDao: EventDependencyDao,
    val imageStatusDao: ImageStatusDao
) : IEventFormatter {


    override suspend fun saveAndFormatEvent(
        event: Events,
        dependencyEntity: List<EventDependencyEntity>,
        selectedEventWriters: List<EventWriterName>,
        uri: Uri?,

        ) {
        selectedEventWriters.forEach() { eventName ->

            val eventWriter = eventWriters.filter {
                it.getEventWriteType() == eventName
            }

            eventWriter.firstOrNull()
                ?.addEvent(
                    context = context,
                    event = event,
                    event.mobile_number,
                    uri,
                    eventsDao = eventsDao,
                    eventDependencyDao = eventDependencyDao,
                    dependencyEntity = dependencyEntity,
                    eventStatusDao = eventStatusDao,
                    imageStatusDao = imageStatusDao
                )
        }
    }

    override suspend fun saveAndFormatEventWithFileName(
        event: Events,
        dependencyEntity: List<EventDependencyEntity>,
        selectedEventWriters: List<EventWriterName>,
        uri: Uri?,
        fileNameWithoutExtension: String
    ) {
        selectedEventWriters.forEach() { eventName ->

            val eventWriter = eventWriters.filter {
                it.getEventWriteType() == eventName
            }
            eventWriter.firstOrNull()?.addFailedEventIntoFile(
                context = context,
                event = event,
                mobileNo = event.mobile_number,
                uri = uri,
                eventsDao = eventsDao,
                eventDependencyDao = eventDependencyDao,
                dependencyEntity = dependencyEntity,
                eventStatusDao = eventStatusDao,
                imageStatusDao = imageStatusDao,
                fileNameWithoutExtension = fileNameWithoutExtension
            )
        }
    }
}