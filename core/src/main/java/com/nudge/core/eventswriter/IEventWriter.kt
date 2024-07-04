package com.nudge.core.eventswriter

import android.content.Context
import android.net.Uri
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.dao.ImageStatusDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventWriterName

interface IEventWriter {
    suspend fun addEvent(
        context: Context,
        event: Events,
        mobileNo: String,
        uri: Uri?,
        dependencyEntity: List<EventDependencyEntity>,
        eventsDao: EventsDao,
        eventStatusDao: EventStatusDao,
        eventDependencyDao: EventDependencyDao,
        imageStatusDao: ImageStatusDao
    )


    suspend fun getEventWriteType(): EventWriterName

    suspend fun addFailedEventIntoFile(
        context: Context,
        event: Events,
        mobileNo: String,
        uri: Uri?,
        dependencyEntity: List<EventDependencyEntity>,
        eventsDao: EventsDao,
        eventStatusDao: EventStatusDao,
        eventDependencyDao: EventDependencyDao,
        imageStatusDao: ImageStatusDao,
        fileNameWithoutExtension: String
    )


}