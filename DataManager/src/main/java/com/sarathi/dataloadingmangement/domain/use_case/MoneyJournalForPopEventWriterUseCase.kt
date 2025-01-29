package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity
import com.sarathi.dataloadingmangement.repository.IEventWriterRepository
import com.sarathi.dataloadingmangement.repository.MoneyJournalForPopEventWriterRepository
import javax.inject.Inject

class MoneyJournalForPopEventWriterUseCase @Inject constructor(
    private val moneyJournalForPopEventWriterRepository: MoneyJournalForPopEventWriterRepository,
    private val eventWriterRepository: IEventWriterRepository
) {

    suspend fun writeMoneyJournalEventForPop(
        moneyJournalEntry: MoneyJournalEntity,
        surveyName: String,
    ) {

        val moneyJournalEventDto = moneyJournalForPopEventWriterRepository
            .getMoneyJournalEventDto(
                moneyJournalEntry
            )
        writeEvent(moneyJournalEventDto, EventName.MONEY_JOURNAL_RESPONSE_EVENT, surveyName)
    }

    private suspend fun <T> writeEvent(eventItem: T, eventName: EventName, surveyName: String) {
        val eventType = EventType.STATEFUL
        eventWriterRepository.createAndSaveEvent(
            eventItem,
            eventName,
            eventType,
            surveyName = surveyName,
            isFromRegenerate = false
        )?.let {
            eventWriterRepository.saveEventToMultipleSources(it, listOf(), eventType)
        }

    }

}