package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData

interface ISubjectLivelihoodEventMapping {
    suspend fun getSubjectLivelihoodEventMappingListFromDb(
        subjectId: Int
    ): List<SubjectLivelihoodEventMappingEntity>?

    suspend fun addOrUpdateLivelihoodEvent(eventData: LivelihoodEventScreenData)

}