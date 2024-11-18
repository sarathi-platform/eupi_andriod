package com.patsurvey.nudge.domain.usecases.tola

import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.patsurvey.nudge.activities.ui.transect_walk.TransectWalkRepository
import javax.inject.Inject

class TolaEventWriterUseCase @Inject constructor(
    private val transectWalkRepository: TransectWalkRepository
) {

    suspend fun <T> invoke(
        tolaItem: T,
        eventName: EventName,
        eventType: EventType
    ) {
        transectWalkRepository.saveEvent(tolaItem, eventName, eventType)
    }


}