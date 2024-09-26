package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.sarathi.dataloadingmangement.enums.LivelihoodEventTypeDataCaptureMapping
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

    suspend fun addOrEditEvent(
        eventData: LivelihoodEventScreenData,
        particular: String,
        createdDate: Long,
        modifiedDate: Long
    ) {

        subjectLivelihoodEventMappingRepository.addOrUpdateLivelihoodEvent(
            eventData,
            currentDateTime = createdDate,
            modifiedDateTime = modifiedDate
        )

        assetJournalRepository.softDeleteAssetJournalEvent(
            eventData.transactionId,
            eventData.subjectId
        )
        moneyJournalRepo.deleteMoneyJournalTransaction(
            transactionId = eventData.transactionId,
            eventData.subjectId
        )
        eventData.selectedEvent.assetJournalEntryFlowType?.let {
            assetJournalRepository.saveOrEditAssetJournal(
                particular = particular,
                eventData = eventData,
                createdDate = createdDate

            )
        }
        eventData.selectedEvent.moneyJournalEntryFlowType?.let {
            moneyJournalRepo.saveAndUpdateMoneyJournalTransaction(
                particular = particular,
                eventData = eventData,
                createdData = createdDate
            )
        }

    }

    suspend fun deleteLivelihoodEvent(
        transactionId: String,
        subjectId: Int,
        selectedEvent: LivelihoodEventTypeDataCaptureMapping,
        modifiedDate: Long
    ) {
        subjectLivelihoodEventMappingRepository.softDeleteLivelihoodEvent(
            transactionId,
            subjectId,
            modifiedDateTime = modifiedDate
        )
        selectedEvent.assetJournalEntryFlowType?.let {
            assetJournalRepository.softDeleteAssetJournalEvent(
                subjectId = subjectId,
                transactionId = transactionId
            )
        }
        selectedEvent.moneyJournalEntryFlowType?.let {
            moneyJournalRepo.deleteMoneyJournalTransaction(transactionId, subjectId)
        }
    }



}