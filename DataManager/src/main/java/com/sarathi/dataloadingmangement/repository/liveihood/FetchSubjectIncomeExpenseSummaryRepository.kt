package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.AssetCountUiModel
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel

interface FetchSubjectIncomeExpenseSummaryRepository {

    suspend fun getIncomeExpenseSummaryForSubject(
        subjectId: Int,
        subjectLivelihoodEventMapping: List<SubjectLivelihoodEventMappingEntity>?,
        assets: List<AssetEntity>
    ): IncomeExpenseSummaryUiModel

    suspend fun getTotalIncomeForSubject(subjectId: Int): Double

    suspend fun getTotalExpenseForSubject(subjectId: Int): Double

    suspend fun getAssetCountForAssets(subjectId: Int, assetIds: List<Int>): List<AssetCountUiModel>

}