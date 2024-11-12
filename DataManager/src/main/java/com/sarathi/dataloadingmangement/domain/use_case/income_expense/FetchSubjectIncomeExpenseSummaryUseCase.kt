package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetEntity
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
        subjectLivelihoodMappingEntity: List<SubjectLivelihoodMappingEntity>
    ): IncomeExpenseSummaryUiModel {

        val assets = assetRepositoryImpl.getAssetsEntityForLivelihood(
            subjectLivelihoodMappingEntity.map { it.livelihoodId }
        )
        return fetchSubjectIncomeExpenseSummaryRepository.getIncomeExpenseSummaryForSubject(
            subjectId,
            assets
        )

    }

    suspend fun invoke(
        subjectId: Int,
        subjectLivelihoodMappingEntity: List<SubjectLivelihoodMappingEntity>,
        durationStart: Long,
        durationEnd: Long
    ): IncomeExpenseSummaryUiModel {
        val assets = assetRepositoryImpl.getAssetsEntityForLivelihood(
            subjectLivelihoodMappingEntity.map { it.livelihoodId }
        ) as ArrayList<AssetEntity>

        // We are handling if livelihoods does not have any assets then we are adding dummy asset to show count 0 to maintain ui consistency

        // livelihoodIds is list of ids have assets
        val livelihoodIds = assets.map {
            it.livelihoodId
        }.distinct()
        val subjectLivelihood =
            subjectLivelihoodMappingEntity.map { it.livelihoodId } // selected livelihood bey subject

        //We finding livelihoodIds tha have not any assets
        if (livelihoodIds.size != subjectLivelihood.size && subjectLivelihood.size > livelihoodIds.size || subjectLivelihood.size == 1) {
            subjectLivelihood.toSet().minus(livelihoodIds).forEach {
                assets.add(
                    AssetEntity.getDefaultAssetEntity(
                        userId = getUserId(),
                        livelihoodId = it
                    )
                )

            }
            //Here handling if user selected not decided for any subject
            if (subjectLivelihood.size == 1) {
                assets.add(
                    AssetEntity.getDefaultAssetEntity(
                        userId = getUserId(),
                        livelihoodId = -1
                    )
                )

            }
        }
        return fetchSubjectIncomeExpenseSummaryRepository.getIncomeExpenseSummaryForSubjectForDuration(
            subjectId = subjectId,
            assets = assets,
            durationStart = durationStart,
            durationEnd = durationEnd
        )
    }

    suspend fun getLivelihoodIncomeExpenseSummaryMap(
        subjectId: Int,
        subjectLivelihoodMappingEntity: List<SubjectLivelihoodMappingEntity>
    ): Map<Int, IncomeExpenseSummaryUiModel> {
        val livelihoodIncomeExpenseMap: MutableMap<Int, IncomeExpenseSummaryUiModel> = HashMap()
        val livelihoods = subjectLivelihoodMappingEntity.map { it.livelihoodId }
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
        subjectLivelihoodMappingEntityList.groupBy { it.subjectId }.entries.forEach {

            summaryMap[it.key] =
                invoke(it.key, it.value.getSubjectLivelihoodMappingEntity(getUserId()))
        }
        return summaryMap
    }

    suspend fun getSummaryForSubjectForDuration(
        subjectLivelihoodMappingEntityList: List<SubjectEntityWithLivelihoodMappingUiModel>,
        durationStart: Long,
        durationEnd: Long
    ): Map<Int, IncomeExpenseSummaryUiModel> {
        val summaryMap: HashMap<Int, IncomeExpenseSummaryUiModel> = hashMapOf()
        subjectLivelihoodMappingEntityList.groupBy { it.subjectId }.entries.forEach {
            summaryMap[it.key] = invoke(
                subjectId = it.key,
                subjectLivelihoodMappingEntity = it.value.getSubjectLivelihoodMappingEntity(
                    getUserId()
                ),
                    durationStart = durationStart,
                    durationEnd = durationEnd
                )
        }
        return summaryMap
    }

    private suspend fun getUserId() = fetchSubjectIncomeExpenseSummaryRepository.getUserId()

}
