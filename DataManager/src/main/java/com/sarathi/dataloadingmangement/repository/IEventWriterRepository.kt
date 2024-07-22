package com.sarathi.dataloadingmangement.repository

import android.net.Uri
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.eventswriter.IEventFormatter
import com.sarathi.dataloadingmangement.BLANK_STRING

interface IEventWriterRepository {

    suspend fun createAndSaveEvent(
        eventName: EventName,
        eventType: EventType = EventType.STATEFUL,
        surveyName: String,
        requestPayload: String = BLANK_STRING
    ): Events?

    suspend fun saveEventToMultipleSources(
        event: Events,
        eventDependencies: List<EventDependencyEntity>,
        eventType: EventType
    )

    fun getEventFormatter(): IEventFormatter
    suspend fun saveImageEventToMultipleSources(event: Events, uri: Uri)

}