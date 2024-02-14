package com.nudge.core.eventswriter

import android.content.Context
import android.net.Uri
import com.nudge.core.enums.EventWriterName
import com.nudge.core.eventWriters
import com.nudge.core.json
import com.nudge.core.eventswriter.entities.EventV1

class JsonEventWriter(val context:Context):IEventFormatter{


    override suspend fun saveAndFormatEvent(
        event: EventV1,
        selectedEventWriters: List<EventWriterName>,
        uri: Uri?,

        ) {
        selectedEventWriters.forEach(){eventName->

            val eventWriter=    eventWriters.filter {
                it.getEventWriteType() == eventName
            }

            eventWriter.firstOrNull()?.addEvent(context =context, event = event.json(),event.mobileNumber,uri)
        }    }
}