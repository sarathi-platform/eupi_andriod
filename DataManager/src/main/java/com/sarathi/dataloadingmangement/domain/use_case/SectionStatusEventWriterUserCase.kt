package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.value
import com.sarathi.dataloadingmangement.repository.EventWriterRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SectionStatusEventWriterRepository
import javax.inject.Inject

class SectionStatusEventWriterUserCase @Inject constructor(
    private val sectionStatusEventWriterRepository: SectionStatusEventWriterRepository,
    private val eventWriterRepositoryImpl: EventWriterRepositoryImpl
) {

    suspend operator fun invoke(surveyId: Int, sectionId: Int, taskId: Int, status: String) {

        val event = sectionStatusEventWriterRepository.writeSectionStatusEvent(
            surveyId,
            sectionId,
            taskId,
            status
        )
        val survey = sectionStatusEventWriterRepository.getSurveyForId(surveyId)

        writeEventInFile(
            eventItem = event,
            eventName = EventName.UPDATE_SECTION_PROGRESS_FOR_DIDI_EVENT,
            surveyName = survey?.surveyName.value()
        )

    }


    private suspend fun <T> writeEventInFile(
        eventItem: T,
        eventName: EventName,
        surveyName: String,
    ) {
        eventWriterRepositoryImpl.createAndSaveEvent(
            eventItem,
            eventName,
            EventType.STATEFUL,
            surveyName
        )?.let {

            eventWriterRepositoryImpl.saveEventToMultipleSources(
                it,
                listOf(),
                EventType.STATEFUL
            )

        }
    }

}