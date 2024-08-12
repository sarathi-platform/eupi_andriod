package com.sarathi.dataloadingmangement.model.uiModel.incomeExpense

import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetEntity

data class IncomeExpenseSummaryUiModel(
    val subjectId: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val livelihoodAssetMap: Map<Int, List<AssetEntity>>,
    val totalAssetCountForLivelihood: Map<Int, Int>,
    val assetsCountWithValue: List<AssetsCountWithValueUiModel>
) {

    companion object {
        const val DEFAULT_INCOME_EXPENSE_VALUE: Double = 0.0
        fun getDefaultIncomeExpenseSummaryUiModel(subjectId: Int): IncomeExpenseSummaryUiModel {
            return IncomeExpenseSummaryUiModel(
                subjectId = subjectId,
                totalIncome = DEFAULT_INCOME_EXPENSE_VALUE,
                totalExpense = DEFAULT_INCOME_EXPENSE_VALUE,
                livelihoodAssetMap = mapOf(),
                totalAssetCountForLivelihood = mapOf(),
                assetsCountWithValue = listOf()
            )
        }

        fun getIncomeExpenseSummaryUiModel(
            subjectId: Int,
            totalIncome: Double,
            totalExpense: Double,
            livelihoodAssetMap: Map<Int, List<AssetEntity>>,
            totalAssetCountForLivelihood: Map<Int, Int>,
            assetsCountWithValue: List<AssetsCountWithValueUiModel>
        ): IncomeExpenseSummaryUiModel {

            return IncomeExpenseSummaryUiModel(
                subjectId = subjectId,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                livelihoodAssetMap = livelihoodAssetMap,
                totalAssetCountForLivelihood = totalAssetCountForLivelihood,
                assetsCountWithValue = assetsCountWithValue
            )

        }

    }

}