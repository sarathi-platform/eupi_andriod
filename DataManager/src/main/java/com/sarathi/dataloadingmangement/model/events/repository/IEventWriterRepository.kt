package com.sarathi.dataloadingmangement.model.events.repository

import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType

interface IEventWriterRepository {

    suspend fun <T> createAndSaveEvent(
        eventItem: T,
        eventName: EventName,
        eventType: EventType,
        surveyName: String,
    ): Events?
}