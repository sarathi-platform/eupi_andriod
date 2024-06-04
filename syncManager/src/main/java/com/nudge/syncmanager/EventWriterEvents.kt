package com.nudge.syncmanager

import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events

sealed class EventWriterEvents {

    data class SaveAttendanceEvent(
        val events: Events,
        val dependencyEntityList: List<EventDependencyEntity>
    ) : EventWriterEvents()

}