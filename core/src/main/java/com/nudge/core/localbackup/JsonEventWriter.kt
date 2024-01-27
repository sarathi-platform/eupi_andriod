package com.nudge.core.localbackup

import android.content.Context
import com.nudge.core.enums.EventWriterName
import com.nudge.core.eventWriters
import com.nudge.core.json
import com.nudge.core.localbackup.entities.EventV1

class JsonEventWriter(val context:Context):IEventFormatter{


    override suspend fun saveAndFormatEvent(event: EventV1, selectedEventWriters: List<EventWriterName>) {
        selectedEventWriters.forEach(){eventName->

            val eventWriter=    eventWriters.filter {
                it.getEventWriteType() == eventName
            }

            eventWriter.firstOrNull()?.addEvent(context =context, event = event.json())
        }    }
}