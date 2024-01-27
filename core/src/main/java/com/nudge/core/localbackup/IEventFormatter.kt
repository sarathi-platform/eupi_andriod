package com.nudge.core.localbackup

import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventWriterName
import com.nudge.core.localbackup.entities.EventV1

interface IEventFormatter {
suspend fun   saveAndFormatEvent(event: EventV1, selectedEventWriters:List<EventWriterName> )
}