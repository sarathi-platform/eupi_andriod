package com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository.EventsWriterRepository
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventType

class EventsWriterUserCase(private val repository: EventsWriterRepository) {

    suspend operator fun invoke(
        events: Events,
        eventDependencies: List<EventDependencyEntity> = emptyList(),
        eventType: EventType
    ) {
        if (events.id != BLANK_STRING)
            repository.saveEventToMultipleSources(events, eventDependencies, eventType)
    }

}
