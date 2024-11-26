package com.nudge.core.eventswriter

import android.content.Context
import android.net.Uri
import com.nudge.core.STATE_ID
import com.nudge.core.USER_TYPE
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventWriterName
import com.nudge.core.eventWriters
import com.nudge.core.findUserTypeForMetadata
import com.nudge.core.json
import com.nudge.core.model.getMetaDataDtoFromString
import com.nudge.core.preference.CoreSharedPrefs

class JsonEventWriter(
    val context: Context,
    val eventsDao: EventsDao,
    val eventDependencyDao: EventDependencyDao
) : IEventFormatter {
    val coreSharedPrefs = CoreSharedPrefs.getInstance(context)
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

            if (event.request_payload?.isNotEmpty() == true) {
                val metadata = event.metadata?.getMetaDataDtoFromString()
                val updatedMetaData = metadata?.copy(
                    data = mapOf(
                        STATE_ID to coreSharedPrefs.getStateId().toString(),
                        USER_TYPE to findUserTypeForMetadata(coreSharedPrefs.getUserType())
                    )
                )
                val updatedEvent = event.copy(metadata = updatedMetaData?.json())

            eventWriter.firstOrNull()
                ?.addEvent(
                    context = context,
                    event = updatedEvent,
                    updatedEvent.mobile_number,
                    uri,
                    eventsDao = eventsDao,
                    eventDependencyDao = eventDependencyDao,
                    dependencyEntity = dependencyEntity
                )
        }
        }
    }
}