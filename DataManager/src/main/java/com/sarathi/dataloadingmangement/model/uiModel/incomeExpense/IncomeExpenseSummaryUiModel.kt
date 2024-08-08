package com.sarathi.dataloadingmangement.model.uiModel.incomeExpense

data class IncomeExpenseSummaryUiModel(
    val subjectId: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val livelihoodAssetMap: Map<Int, List<Int>>,
    val assetsCountWithValue: Triple<Int, Int, Double>
)