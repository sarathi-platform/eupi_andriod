package com.nudge.core.eventswriter

import android.content.Context
import android.net.Uri
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventWriterName
import com.nudge.core.eventWriters
import com.nudge.core.json

class JsonEventWriter(val context:Context):IEventFormatter{


    override suspend fun saveAndFormatEvent(
        event: Events,
        selectedEventWriters: List<EventWriterName>,
        uri: Uri?,

        ) {
        selectedEventWriters.forEach(){eventName->

            val eventWriter=    eventWriters.filter {
                it.getEventWriteType() == eventName
            }

            eventWriter.firstOrNull()
                ?.addEvent(context = context, event = event.json(), event.mobile_number, uri)
        }    }
}