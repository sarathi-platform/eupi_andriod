package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import com.sarathi.dataloadingmangement.model.events.incomeExpense.SaveLivelihoodEventDto
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.SubjectLivelihoodEventSummaryUiModel

interface ISubjectLivelihoodEventMapping {

    suspend fun getLivelihoodEventsWithAssetAndMoneyEntryForSubject(subjectId: Int): List<SubjectLivelihoodEventSummaryUiModel>
    suspend fun getLivelihoodEventsWithAssetAndMoneyEntryForDeletedSubject(subjectId: Int): List<SubjectLivelihoodEventSummaryUiModel>

    suspend fun getSubjectLivelihoodEventMappingListFromDb(
        subjectId: Int
    ): List<SubjectLivelihoodEventMappingEntity>?

    suspend fun getSavedEventFromDb(
        subjectId: Int,
        transactionId: String,
    ): SubjectLivelihoodEventMappingEntity?

    suspend fun addOrUpdateLivelihoodEvent(eventData: LivelihoodEventScreenData)

    suspend fun softDeleteLivelihoodEvent(transactionId: String, subjectId: Int)

    suspend fun getLivelihoodEventDto(eventData: LivelihoodEventScreenData): SaveLivelihoodEventDto
    suspend fun getUserId(): Int

    suspend fun getSubjectLivelihoodEventMappingListForTransactionIdFromDb(
        transactionId: String
    ): List<SubjectLivelihoodEventMappingEntity>?

}