package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel
import com.sarathi.dataloadingmangement.repository.liveihood.AssetRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.FetchSubjectIncomeExpenseSummaryRepository
import com.sarathi.dataloadingmangement.repository.liveihood.SubjectLivelihoodEventMappingRepositoryImpl
import javax.inject.Inject

class FetchSubjectIncomeExpenseSummaryUseCase @Inject constructor(
    private val fetchSubjectIncomeExpenseSummaryRepository: FetchSubjectIncomeExpenseSummaryRepository,
    private val subjectLivelihoodEventMappingRepository: SubjectLivelihoodEventMappingRepositoryImpl,
    private val assetRepositoryImpl: AssetRepositoryImpl
) {

    suspend operator fun invoke(
        subjectId: Int,
        subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity
    ): IncomeExpenseSummaryUiModel {

        val subjectLivelihoodEventMapping =
            subjectLivelihoodEventMappingRepository.getSubjectLivelihoodEventMappingListFromDb(
                subjectId
            )
        val assets = assetRepositoryImpl.getAssetsEntityForLivelihood(
            listOf(
                subjectLivelihoodMappingEntity.primaryLivelihoodId,
                subjectLivelihoodMappingEntity.secondaryLivelihoodId
            )
        )
        return fetchSubjectIncomeExpenseSummaryRepository.getIncomeExpenseSummaryForSubject(
            subjectId,
            subjectLivelihoodEventMapping,
            assets
        )

    }

}