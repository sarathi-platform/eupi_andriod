package com.nudge.core.eventswriter

import android.content.Context
import android.net.Uri
import com.nudge.core.APP_VERSION
import com.nudge.core.STATE_ID
import com.nudge.core.USER_TYPE
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.dao.ImageStatusDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventWriterName
import com.nudge.core.eventWriters
import com.nudge.core.findUserTypeForMetadata
import com.nudge.core.json
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.getMetaDataDtoFromString
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.value

class JsonEventWriter(
    val context: Context,
    val eventsDao: EventsDao,
    val eventStatusDao: EventStatusDao,
    val eventDependencyDao: EventDependencyDao,
    val imageStatusDao: ImageStatusDao
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
                val data = metadata?.data?.toMutableMap()
                data?.putAll(
                    mapOf(
                        STATE_ID to coreSharedPrefs.getStateId().toString(),
                        USER_TYPE to findUserTypeForMetadata(coreSharedPrefs.getUserType()),
                        APP_VERSION to CoreAppDetails.getApplicationDetails()?.buildVersion.value()
                    )
                )

                val updatedMetaData = metadata?.copy(
                    data = data?.toMap() ?: emptyMap()
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
                    dependencyEntity = dependencyEntity,
                    eventStatusDao = eventStatusDao,
                    imageStatusDao = imageStatusDao
                )
        }
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