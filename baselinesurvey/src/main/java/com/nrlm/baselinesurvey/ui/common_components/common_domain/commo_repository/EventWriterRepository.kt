package com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository

import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.eventswriter.IEventFormatter

interface EventsWriterRepository {


    suspend fun <T> createEvent(eventItem: T, eventName: EventName, eventType: EventType): Events?

    suspend fun <T> createEventDependency(
        eventItem: T,
        eventName: EventName,
        dependentEvent: Events
    ): List<EventDependencyEntity>

    suspend fun saveEventToMultipleSources(
        event: Events,
        eventDependencies: List<EventDependencyEntity>,
        eventType: EventType
    )

    fun getEventFormatter(): IEventFormatter

    suspend fun isSectionProgressForDidiAlreadyAdded(
        surveyId: Int,
        sectionId: Int,
        didiId: Int
    ): Boolean

}