package com.nudge.communicationModule

import android.content.Context
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.NetworkSpeed

interface EventObserverInterface {

    fun <T> onEventCallback(event: T)

    suspend fun addEvent(event: Events)

    suspend fun addEvents(events: List<Events>)

    suspend fun addEventDependency(eventDependency: EventDependencyEntity)

    suspend fun addEventDependencies(eventDependencies: List<EventDependencyEntity>)

    suspend fun syncPendingEvent(context: Context, networkSpeed: NetworkSpeed)

    suspend fun getEvent():List<Events>
}