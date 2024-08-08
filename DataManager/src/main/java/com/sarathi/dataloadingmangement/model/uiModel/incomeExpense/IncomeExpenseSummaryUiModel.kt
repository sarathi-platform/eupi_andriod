package com.sarathi.dataloadingmangement.model.uiModel.incomeExpense

import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetEntity

data class IncomeExpenseSummaryUiModel(
    val subjectId: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val livelihoodAssetMap: Map<Int, List<AssetEntity>>,
    val assetsCountWithValue: List<AssetsCountWithValueUiModel>
) {

    companion object {

        fun getIncomeExpenseSummaryUiModel(
            subjectId: Int,
            totalIncome: Double,
            totalExpense: Double,
            livelihoodAssetMap: Map<Int, List<AssetEntity>>,
            assetsCountWithValue: List<AssetsCountWithValueUiModel>
        ): IncomeExpenseSummaryUiModel {

            return IncomeExpenseSummaryUiModel(
                subjectId = subjectId,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                livelihoodAssetMap = livelihoodAssetMap,
                assetsCountWithValue = assetsCountWithValue
            )

        }

    }

}