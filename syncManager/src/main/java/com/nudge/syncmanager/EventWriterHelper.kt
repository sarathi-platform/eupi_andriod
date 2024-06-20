package com.nudge.syncmanager

import android.net.Uri
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.eventswriter.IEventFormatter

interface EventWriterHelper {

    suspend fun <T> createEvent(eventItem: T, eventName: EventName, eventType: EventType): Events

    suspend fun saveEventToMultipleSources(
        event: Events,
        eventDependencies: List<EventDependencyEntity>,
        eventType: EventType
    )

    suspend fun <T> createEventDependency(
        eventItem: T,
        eventName: EventName,
        dependentEvent: Events
    ): List<EventDependencyEntity>

    fun getEventFormatter(): IEventFormatter

    suspend fun saveImageEventToMultipleSources(event: Events, uri: Uri)

    fun getBaseLineUserId(): String

}