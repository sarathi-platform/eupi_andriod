package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData
import com.sarathi.dataloadingmangement.repository.IMoneyJournalRepository
import com.sarathi.dataloadingmangement.repository.liveihood.IAssetJournalRepository
import com.sarathi.dataloadingmangement.repository.liveihood.ISubjectLivelihoodEventMapping
import javax.inject.Inject

class FetchSavedEventUseCase @Inject constructor(
    private val assetJournalRepository: IAssetJournalRepository,
    private val moneyJournalRepo: IMoneyJournalRepository,
    private val subjectLivelihoodEventMappingRepository: ISubjectLivelihoodEventMapping
) {

    suspend fun fetchEvent(transactionId: String, subjectId: Int): LivelihoodEventScreenData? {

        val savedEvent = subjectLivelihoodEventMappingRepository.getSavedEventFromDb(
            subjectId = subjectId,
            transactionId = transactionId
        )
        savedEvent?.let {
            val type = object : TypeToken<LivelihoodEventScreenData?>() {}.type

            return Gson().fromJson<LivelihoodEventScreenData>(savedEvent.surveyResponse, type)

        }
        return null
    }


}