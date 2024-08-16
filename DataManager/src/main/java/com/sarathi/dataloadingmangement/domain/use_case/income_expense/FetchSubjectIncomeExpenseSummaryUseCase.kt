package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.SubjectEntityWithLivelihoodMappingUiModel
import com.sarathi.dataloadingmangement.repository.liveihood.AssetRepositoryImpl
import com.sarathi.dataloadingmangement.repository.liveihood.FetchSubjectIncomeExpenseSummaryRepository
import getSubjectLivelihoodMappingEntity
import javax.inject.Inject

class FetchSubjectIncomeExpenseSummaryUseCase @Inject constructor(
    private val fetchSubjectIncomeExpenseSummaryRepository: FetchSubjectIncomeExpenseSummaryRepository,
    private val assetRepositoryImpl: AssetRepositoryImpl
) {

    suspend operator fun invoke(
        subjectId: Int,
        subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity
    ): IncomeExpenseSummaryUiModel {

        val assets = assetRepositoryImpl.getAssetsEntityForLivelihood(
            listOf(
                subjectLivelihoodMappingEntity.primaryLivelihoodId,
                subjectLivelihoodMappingEntity.secondaryLivelihoodId
            )
        )
        return fetchSubjectIncomeExpenseSummaryRepository.getIncomeExpenseSummaryForSubject(
            subjectId,
            assets
        )

    }

    suspend fun invoke(
        subjectId: Int,
        subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity,
        durationStart: Long,
        durationEnd: Long
    ): IncomeExpenseSummaryUiModel {
        val assets = assetRepositoryImpl.getAssetsEntityForLivelihood(
            listOf(
                subjectLivelihoodMappingEntity.primaryLivelihoodId,
                subjectLivelihoodMappingEntity.secondaryLivelihoodId
            )
        )
        return fetchSubjectIncomeExpenseSummaryRepository.getIncomeExpenseSummaryForSubjectForDuration(
            subjectId = subjectId,
            assets = assets,
            durationStart = durationStart,
            durationEnd = durationEnd
        )
    }

    suspend fun getLivelihoodIncomeExpenseSummaryMap(
        subjectId: Int,
        subjectLivelihoodMappingEntity: SubjectLivelihoodMappingEntity
    ): Map<Int, IncomeExpenseSummaryUiModel> {
        val livelihoodIncomeExpenseMap: MutableMap<Int, IncomeExpenseSummaryUiModel> = HashMap()
        val livelihoods = listOf(
            subjectLivelihoodMappingEntity.primaryLivelihoodId,
            subjectLivelihoodMappingEntity.secondaryLivelihoodId
        )
        livelihoods.forEach {
            val assets = assetRepositoryImpl.getAssetsEntityForLivelihood(
                listOf(
                    it
                )
            )
            livelihoodIncomeExpenseMap.put(
                it, fetchSubjectIncomeExpenseSummaryRepository.getIncomeExpenseSummaryForSubject(
                    subjectId,
                    assets,
                    it
                )
            )
        }

        return livelihoodIncomeExpenseMap

    }

    suspend fun getSummaryForSubjects(subjectLivelihoodMappingEntityList: List<SubjectEntityWithLivelihoodMappingUiModel>): Map<Int, IncomeExpenseSummaryUiModel> {
        val summaryMap: HashMap<Int, IncomeExpenseSummaryUiModel> = hashMapOf()
        subjectLivelihoodMappingEntityList.forEach {
            summaryMap[it.subjectId] =
                invoke(it.subjectId, it.getSubjectLivelihoodMappingEntity(getUserId()))
        }
        return summaryMap
    }

    suspend fun getSummaryForSubjectForDuration(
        subjectLivelihoodMappingEntityList: List<SubjectEntityWithLivelihoodMappingUiModel>,
        durationStart: Long,
        durationEnd: Long
    ): Map<Int, IncomeExpenseSummaryUiModel> {
        val summaryMap: HashMap<Int, IncomeExpenseSummaryUiModel> = hashMapOf()
        subjectLivelihoodMappingEntityList.forEach {
            summaryMap[it.subjectId] =
                invoke(
                    subjectId = it.subjectId,
                    subjectLivelihoodMappingEntity = it.getSubjectLivelihoodMappingEntity(getUserId()),
                    durationStart = durationStart,
                    durationEnd = durationEnd
                )
        }
        return summaryMap
    }

    private suspend fun getUserId() = fetchSubjectIncomeExpenseSummaryRepository.getUserId()

}
