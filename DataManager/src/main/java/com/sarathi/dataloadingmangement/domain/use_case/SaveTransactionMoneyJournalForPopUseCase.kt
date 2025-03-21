package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.entities.livelihood.MoneyJournalEntity
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.repository.MoneyJournalForPopRepository
import getMoneyJournalEntryData

class SaveTransactionMoneyJournalForPopUseCase(private val moneyJournalForPopRepository: MoneyJournalForPopRepository) {

    suspend fun saveMoneyJournalForSurvey(
        subjectId: Int,
        subjectType: String,
        transactionId: String,
        referenceId: Int,
        referenceType: String,
        questionUiModels: List<QuestionUiModel>,
        transactionFlow: String,
        localTransactionId: String
    ): MoneyJournalEntity {
        val moneyJournalEntryData = getMoneyJournalEntryData(questionUiModels, subjectType)
        val amountInString = moneyJournalEntryData.first
        val date = moneyJournalEntryData.second
        var particulars = moneyJournalEntryData.third

        return moneyJournalForPopRepository.saveAndUpdateMoneyJournalTransaction(
            amount = amountInString?.toInt() ?: 0,
            date = date.value(),
            particulars = particulars,
            transactionId = transactionId,
            referenceId = referenceId,
            referenceType = referenceType,
            subjectType = subjectType,
            subjectId = subjectId,
            transactionFlow = transactionFlow,
            localTransactionId = localTransactionId
        )
    }

}