package com.nudge.core.eventswriter

import android.net.Uri
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventWriterName

interface IEventFormatter {
    suspend fun saveAndFormatEvent(
        event: Events,
        selectedEventWriters: List<EventWriterName>,
        uri: Uri? = null
    )
}