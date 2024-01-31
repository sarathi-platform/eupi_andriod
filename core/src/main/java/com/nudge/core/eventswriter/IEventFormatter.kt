package com.nudge.core.eventswriter

import android.net.Uri
import com.nudge.core.enums.EventWriterName
import com.nudge.core.eventswriter.entities.EventV1

interface IEventFormatter {
    suspend fun saveAndFormatEvent(
        event: EventV1,
        selectedEventWriters: List<EventWriterName>,
        uri: Uri? = null
    )
}