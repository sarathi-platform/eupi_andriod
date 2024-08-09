package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.SubjectLivelihoodEventSummaryUiModel
import com.sarathi.dataloadingmangement.repository.liveihood.SubjectLivelihoodEventMappingRepositoryImpl
import javax.inject.Inject

class FetchSubjectLivelihoodEventMappingUseCase @Inject constructor(
    private val subjectLivelihoodEventMappingRepositoryImpl: SubjectLivelihoodEventMappingRepositoryImpl
) {

    suspend operator fun invoke(subjectId: Int): List<SubjectLivelihoodEventSummaryUiModel> {

        return subjectLivelihoodEventMappingRepositoryImpl.getLivelihoodEventsWithAssetAndMoneyEntryForSubject(
            subjectId
        )

    }

    suspend fun getSubjectLivelihoodEventMappingListFromDb(
        subjectId: Int
    ): List<SubjectLivelihoodEventMappingEntity>? {
        return subjectLivelihoodEventMappingRepositoryImpl.getSubjectLivelihoodEventMappingListFromDb(
            subjectId = subjectId
        )
    }

}