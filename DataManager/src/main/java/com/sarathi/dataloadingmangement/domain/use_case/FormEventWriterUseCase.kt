package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.json
import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.repository.EventWriterRepositoryImpl
import com.sarathi.dataloadingmangement.repository.FormEventRepositoryImpl
import javax.inject.Inject

class FormEventWriterUseCase @Inject constructor(
    private val repository: FormEventRepositoryImpl,
    private val eventWriterRepositoryImpl: EventWriterRepositoryImpl
) {

    suspend fun writeFormEvent(surveyName: String, formEntity: FormEntity) {

        val saveAnswerEventDto = repository.getSaveFormAnswerEventDto(formEntity)
        eventWriterRepositoryImpl.createAndSaveEvent(
            eventName = EventName.UPDATE_FORM_DETAILS_EVENT,
            eventType = EventType.STATEFUL,
            surveyName = surveyName,
            requestPayload = saveAnswerEventDto.json()
        )?.let {

            eventWriterRepositoryImpl.saveEventToMultipleSources(
                it, listOf(), EventType.STATEFUL
            )


        }

    }
}
