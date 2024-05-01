package com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository

import android.net.Uri
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.getDependentEventsId
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.eventswriter.IEventFormatter
import com.nudge.core.json
import com.nudge.core.model.getMetaDataDtoFromString

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

    suspend fun <T> getDependsOnForEvent(
        eventItem: T,
        event: Events,
        eventName: EventName
    ): Events {

        var mEvent = event

        if (eventName.depends_on.isNotEmpty()) {
            val dependsOn = createEventDependency(eventItem, eventName, event)
            val metadata = event.metadata?.getMetaDataDtoFromString()
            val updatedMetaData =
                metadata?.copy(depends_on = dependsOn.getDependentEventsId())
            mEvent = event.copy(
                metadata = updatedMetaData?.json()
            )
        }

        return mEvent
    }

    suspend fun saveImageEventToMultipleSources(event: Events, uri: Uri)
    fun getBaseLineUserId(): String

}