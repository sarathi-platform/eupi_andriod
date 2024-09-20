package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.getCurrentTimeInMillis
import com.sarathi.dataloadingmangement.model.events.incomeExpense.DeleteLivelihoodEvent
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData
import com.sarathi.dataloadingmangement.repository.IEventWriterRepository
import com.sarathi.dataloadingmangement.repository.IMoneyJournalRepository
import com.sarathi.dataloadingmangement.repository.liveihood.IAssetJournalRepository
import com.sarathi.dataloadingmangement.repository.liveihood.ISubjectLivelihoodEventMapping
import javax.inject.Inject


class WriteLivelihoodEventUseCase @Inject constructor(
    private val assetJournalRepository: IAssetJournalRepository,
    private val moneyJournalRepo: IMoneyJournalRepository,
    private val subjectLivelihoodEventMappingRepository: ISubjectLivelihoodEventMapping,
    private val eventWriterRepository: IEventWriterRepository
) {

    suspend fun writeLivelihoodEvent(
        eventData: LivelihoodEventScreenData,
        particular: String,
        createdDateTime: Long
    ) {
        val livelihoodPayload =
            subjectLivelihoodEventMappingRepository.getLivelihoodEventDto(
                eventData,
                currentDateTime = createdDateTime,
                modifiedDateTime = getCurrentTimeInMillis()
            )
        writeEvent(livelihoodPayload, EventName.LIVELIHOOD_EVENT)

        eventData.selectedEvent.assetJournalEntryFlowType?.let {

            val assetJournalPayload = assetJournalRepository.getSaveAssetJournalEventDto(
                particular,
                eventData,
                currentDateTime = createdDateTime,
                modifiedDateTIme = getCurrentTimeInMillis()
            )
            writeEvent(assetJournalPayload, EventName.ASSET_JOURNAL_EVENT)

        }
        eventData.selectedEvent.moneyJournalEntryFlowType?.let {
            val moneyJournalPayload =
                moneyJournalRepo.getMoneyJournalEventDto(
                    particular,
                    eventData,
                    currentDateTime = createdDateTime,
                    modifiedDateTime = getCurrentTimeInMillis()
                )
            writeEvent(moneyJournalPayload, EventName.MONEY_JOURNAL_RESPONSE_EVENT)

        }

    }

    suspend fun writeDeleteLivelihoodEvent(transactionId: String, subjectId: Int) {
        val deleteLivelihoodEventPayload = DeleteLivelihoodEvent(
            doerId = subjectLivelihoodEventMappingRepository.getUserId(),
            transactionId = transactionId,
            subjectId = subjectId,
            subjectType = "Didi"
        )
        writeEvent(deleteLivelihoodEventPayload, EventName.DELETE_RESPONSE_EVENT)


    }

    private suspend fun <T> writeEvent(eventItem: T, eventName: EventName) {
        eventWriterRepository.createAndSaveEvent(
            eventItem,
            eventName,
            EventType.STATEFUL,
            surveyName = "Livelihood",
            isFromRegenerate = false
        )?.let {

            eventWriterRepository.saveEventToMultipleSources(
                it, listOf(), EventType.STATEFUL
            )


        }
    }


}