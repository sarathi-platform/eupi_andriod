package com.nudge.communicationModule

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.NetworkSpeed
import kotlinx.coroutines.flow.Flow

interface EventObserverInterface {

    fun <T> onEventCallback(event: T)

    suspend fun addEvent(event: Events)

    suspend fun addEvents(events: List<Events>)

    suspend fun addEventDependency(eventDependency: EventDependencyEntity)

    suspend fun addEventDependencies(eventDependencies: List<EventDependencyEntity>)

    suspend fun syncPendingEvent(context: Context, networkSpeed: NetworkSpeed): Flow<WorkInfo>

    suspend fun getEvent():List<Events>
}