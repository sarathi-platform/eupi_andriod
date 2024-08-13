package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.AssetCountUiModel
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel

interface FetchSubjectIncomeExpenseSummaryRepository {

    suspend fun getIncomeExpenseSummaryForSubject(
        subjectId: Int,
        assets: List<AssetEntity>
    ): IncomeExpenseSummaryUiModel

    suspend fun getTotalIncomeForSubject(subjectId: Int): Double

    suspend fun getTotalExpenseForSubject(subjectId: Int): Double

    suspend fun getAssetCountForAssets(subjectId: Int, assetIds: List<Int>): List<AssetCountUiModel>

    fun getUserId(): String

    suspend fun getIncomeExpenseSummaryForSubjectForDuration(
        subjectId: Int,
        assets: List<AssetEntity>,
        durationStart: Long,
        durationEnd: Long
    ): IncomeExpenseSummaryUiModel

    suspend fun getTotalIncomeForSubjectForDuration(
        subjectId: Int, durationStart: Long,
        durationEnd: Long
    ): Double

    suspend fun getTotalExpenseForSubjectForDuration(
        subjectId: Int, durationStart: Long,
        durationEnd: Long
    ): Double

    suspend fun getAssetCountForAssetsForDuration(
        subjectId: Int,
        assetIds: List<Int>,
        durationStart: Long,
        durationEnd: Long
    ): List<AssetCountUiModel>

}