package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData
import com.sarathi.dataloadingmangement.repository.IMoneyJournalRepository
import com.sarathi.dataloadingmangement.repository.liveihood.IAssetJournalRepository
import com.sarathi.dataloadingmangement.repository.liveihood.ISubjectLivelihoodEventMapping
import javax.inject.Inject

class SaveLivelihoodEventUseCase @Inject constructor(
    private val assetJournalRepository: IAssetJournalRepository,
    private val moneyJournalRepo: IMoneyJournalRepository,
    private val subjectLivelihoodEventMappingRepository: ISubjectLivelihoodEventMapping
) {

    suspend fun addOrEditEvent(eventData: LivelihoodEventScreenData) {

        subjectLivelihoodEventMappingRepository.addOrUpdateLivelihoodEvent(eventData)
        eventData.selectedEvent.assetJournalEntryFlowType?.let {
            assetJournalRepository.saveOrEditAssetJournal(particular = "", eventData = eventData)
        }
        eventData.selectedEvent.moneyJournalEntryFlowType?.let {
            moneyJournalRepo.saveAndUpdateMoneyJournalTransaction(
                particular = "",
                eventData = eventData
            )
        }

    }

}