package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.nudge.core.LIVELIHOOD
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.sarathi.dataloadingmangement.model.events.LivelihoodPlanActivityEventDto
import com.sarathi.dataloadingmangement.repository.EventWriterRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.LivelihoodEventRepositoryImpl
import javax.inject.Inject

class LivelihoodEventWriterUseCase @Inject constructor(
    private val repository: LivelihoodEventRepositoryImpl,
    private val eventWriterRepositoryImpl: EventWriterRepositoryImpl
) {

    suspend fun writeLivelihoodEvent(livelihoodPlanActivityEventDto: LivelihoodPlanActivityEventDto) {

        val saveLivelihoodEventDto = repository.getSaveLivelihoodEventDto(livelihoodPlanActivityEventDto)
        eventWriterRepositoryImpl.createAndSaveEvent(
            saveLivelihoodEventDto,
            EventName.LIVELIHOOD_OPTION_EVENT,
            EventType.STATEFUL,
            LIVELIHOOD


        )?.let {

            eventWriterRepositoryImpl.saveEventToMultipleSources(
                it, listOf(), EventType.STATEFUL
            )


        }

    }
}
