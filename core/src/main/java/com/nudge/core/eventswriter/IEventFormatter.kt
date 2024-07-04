package com.nudge.core.eventswriter

import android.net.Uri
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventWriterName

interface IEventFormatter {
    suspend fun saveAndFormatEvent(
        event: Events,
        dependencyEntity: List<EventDependencyEntity>,
        selectedEventWriters: List<EventWriterName>,
        uri: Uri? = null
    )

    suspend fun saveAndFormatEventWithFileName(
        event: Events,
        dependencyEntity: List<EventDependencyEntity>,
        selectedEventWriters: List<EventWriterName>,
        uri: Uri? = null,
        fileNameWithoutExtension: String
    )
}