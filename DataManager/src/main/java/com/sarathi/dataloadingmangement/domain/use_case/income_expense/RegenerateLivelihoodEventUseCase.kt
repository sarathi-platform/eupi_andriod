package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.sarathi.dataloadingmangement.model.events.incomeExpense.SaveAssetJournalEventDto
import com.sarathi.dataloadingmangement.model.events.incomeExpense.SaveMoneyJournalEventDto
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData
import com.sarathi.dataloadingmangement.repository.IEventWriterRepository
import com.sarathi.dataloadingmangement.repository.IMoneyJournalRepository
import com.sarathi.dataloadingmangement.repository.liveihood.IAssetJournalRepository
import com.sarathi.dataloadingmangement.repository.liveihood.ISubjectLivelihoodEventMapping
import javax.inject.Inject

class RegenerateLivelihoodEventUseCase @Inject constructor(
    private val assetJournalRepository: IAssetJournalRepository,
    private val moneyJournalRepo: IMoneyJournalRepository,
    private val subjectLivelihoodEventMappingRepository: ISubjectLivelihoodEventMapping,
    private val eventWriterRepository: IEventWriterRepository
) {

    suspend fun invoke(

    ) {
        assetJournalRepository.getAllAssetJournalForUser().forEach {
            val assetJournalPayload = SaveAssetJournalEventDto.getAssetJournalEventDto(it)


            writeEvent(assetJournalPayload, EventName.ASSET_JOURNAL_EVENT)
        }

        moneyJournalRepo.getMoneyJournalEventForUser().forEach {
            val moneyJournalPayload = SaveMoneyJournalEventDto.getMoneyJournalEventDto(it)
            writeEvent(moneyJournalPayload, EventName.MONEY_JOURNAL_RESPONSE_EVENT)


        }
        subjectLivelihoodEventMappingRepository.getLivelihoodEventForUser().forEach {
            val type = object : TypeToken<LivelihoodEventScreenData?>() {}.type
            val eventData = Gson().fromJson<LivelihoodEventScreenData>(it.surveyResponse, type)
            val livelihoodEventPayload =
                subjectLivelihoodEventMappingRepository.getLivelihoodEventDto(
                    eventData,
                    currentDateTime = it.createdDate,
                    modifiedDateTime = it.modifiedDate
                )
            writeEvent(livelihoodEventPayload, EventName.LIVELIHOOD_EVENT)
        }

    }


    private suspend fun <T> writeEvent(eventItem: T, eventName: EventName) {
        eventWriterRepository.createAndSaveEvent(
            eventItem,
            eventName,
            EventType.STATEFUL,
            surveyName = "Livelihood",
            isFromRegenerate = true
        )?.let {

            eventWriterRepository.saveEventToMultipleSources(
                it, listOf(), EventType.STATEFUL
            )


        }
    }


}