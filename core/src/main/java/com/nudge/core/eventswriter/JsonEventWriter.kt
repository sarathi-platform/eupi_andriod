package com.nudge.core.eventswriter

import android.content.Context
import android.net.Uri
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventWriterName
import com.nudge.core.eventWriters

class JsonEventWriter(
    val context: Context,
    val eventsDao: EventsDao,
    val eventDependencyDao: EventDependencyDao
) : IEventFormatter {


    override suspend fun saveAndFormatEvent(
        event: Events,
        dependencyEntity: List<EventDependencyEntity>,
        selectedEventWriters: List<EventWriterName>,
        uri: Uri?,

        ) {
        selectedEventWriters.forEach(){eventName->

            val eventWriter=    eventWriters.filter {
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
                    dependencyEntity = dependencyEntity
                )
        }    }
}