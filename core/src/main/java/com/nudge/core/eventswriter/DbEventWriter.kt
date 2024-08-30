package com.nudge.core.eventswriter

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import com.nudge.core.BLANK_STRING
import com.nudge.core.EventSyncStatus
import com.nudge.core.FORM_C_TOPIC
import com.nudge.core.FORM_D_TOPIC
import com.nudge.core.IMAGE_EVENT_STRING
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.dao.ImageStatusDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.EventStatusEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.ImageStatusEntity
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventWriterName
import com.nudge.core.toDate

class DbEventWrite() : IEventWriter {
    override suspend fun addEvent(
        context: Context,
        event: Events,
        mobileNo: String,
        uri: Uri?,
        dependencyEntity: List<EventDependencyEntity>,
        eventsDao: EventsDao,
        eventStatusDao: EventStatusDao,
        eventDependencyDao: EventDependencyDao,
        imageStatusDao: ImageStatusDao
    ) {
        eventsDao.insert(event)
        if (event.name.contains(IMAGE_EVENT_STRING) || event.name == FORM_C_TOPIC || event.name == FORM_D_TOPIC) {
            uri?.let {
                if (it != Uri.EMPTY) {
                    imageStatusDao.insert(
                        ImageStatusEntity(
                            errorMessage = BLANK_STRING,
                            retryCount = 0,
                            mobileNumber = event.mobile_number,
                            createdBy = event.createdBy,
                            fileName = it.toFile().name,
                            filePath = it.path ?: BLANK_STRING,
                            name = EventName.BLOB_UPLOAD_TOPIC.topicName,
                            type = EventName.BLOB_UPLOAD_TOPIC.topicName,
                            status = EventSyncStatus.OPEN.eventSyncStatus,
                            imageEventId = event.id,
                            modifiedDate = System.currentTimeMillis().toDate()
                        )
                    )
                }
            }

        }
            eventStatusDao.insert(
                EventStatusEntity(
                    clientId = event.id,
                    errorMessage = BLANK_STRING,
                    status = EventSyncStatus.OPEN.eventSyncStatus,
                    mobileNumber = event.mobile_number,
                    createdBy = event.createdBy,
                    eventStatusId = 0
                )
            )
        if (dependencyEntity.isNotEmpty())
            eventDependencyDao.insertAll(dependencyEntity)
    }

    override suspend fun getEventWriteType(): EventWriterName {
        return EventWriterName.DB_EVENT_WRITER
    }

    override suspend fun addFailedEventIntoFile(
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
    ) {
        // "Not yet implemented"
    }
}