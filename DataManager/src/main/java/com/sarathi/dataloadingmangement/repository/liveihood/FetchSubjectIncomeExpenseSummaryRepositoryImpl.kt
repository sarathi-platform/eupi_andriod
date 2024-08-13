package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.dao.livelihood.AssetDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.AssetJournalDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodEventDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.MoneyJournalDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetEntity
import com.sarathi.dataloadingmangement.enums.EntryFlowTypeEnum
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.AssetCountUiModel
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.AssetsCountWithValueUiModel
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.find
import javax.inject.Inject

class FetchSubjectIncomeExpenseSummaryRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val livelihoodEventDao: LivelihoodEventDao,
    private val moneyJournalDao: MoneyJournalDao,
    private val assetJournalDao: AssetJournalDao,
    private val livelihoodDao: LivelihoodDao,
    private val assetDao: AssetDao
) : FetchSubjectIncomeExpenseSummaryRepository {

    private val LIVELIHOOD_EVENT_REFERENCE_TYPE: String = "LivelihoodEvent"

    override suspend fun getIncomeExpenseSummaryForSubject(
        subjectId: Int,
        assets: List<AssetEntity>
    ): IncomeExpenseSummaryUiModel {

        val totalIncome = getTotalIncomeForSubject(subjectId = subjectId)
        val totalExpense = getTotalExpenseForSubject(subjectId = subjectId)
        val assetCounts = getAssetCountForAssets(subjectId, assets.map { it.assetId })

        val livelihoodAssetMap = assets.groupBy { it.livelihoodId }

        val assetsCountWithValue = AssetsCountWithValueUiModel
            .getAssetsCountWithValueUiModelList(assetsList = assets, assetCounts)

        val totalAssetCountForLivelihood = hashMapOf<Int, Int>()

        livelihoodAssetMap.forEach { mapEntry ->

            var totalAssetCount = 0
            mapEntry.value.forEach {
                totalAssetCount += (assetsCountWithValue.find(it.assetId)?.assetCount ?: 0)
            }

            totalAssetCountForLivelihood.put(mapEntry.key, totalAssetCount)
        }

        return IncomeExpenseSummaryUiModel
            .getIncomeExpenseSummaryUiModel(
                subjectId = subjectId,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                livelihoodAssetMap = livelihoodAssetMap,
                totalAssetCountForLivelihood = totalAssetCountForLivelihood,
                assetsCountWithValue = assetsCountWithValue
            )
    }

    override suspend fun getTotalIncomeForSubject(subjectId: Int): Double {
        return moneyJournalDao.getTotalIncomeExpenseForSubject(
            transactionFlow = EntryFlowTypeEnum.Inflow.name,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            subjectId = subjectId.value(),
            referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE
        )?.totalIncome.value()
    }

    override suspend fun getTotalExpenseForSubject(subjectId: Int): Double {
        return moneyJournalDao.getTotalIncomeExpenseForSubject(
            transactionFlow = EntryFlowTypeEnum.OutFlow.name,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            subjectId = subjectId.value(),
            referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE
        )?.totalIncome.value()
    }


    override suspend fun getAssetCountForAssets(
        subjectId: Int,
        assetIds: List<Int>
    ): List<AssetCountUiModel> {
        val assetCountUiModelList = ArrayList<AssetCountUiModel>()
        assetIds.forEach { assetId ->
            val inflowAssetCount = assetJournalDao.getAssetCountForAsset(
                assetId = assetId,
                subjectId = subjectId,
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                transactionFlow = EntryFlowTypeEnum.Inflow.name,
                referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE
            )
            val outFlowAssetCount = assetJournalDao.getAssetCountForAsset(
                assetId = assetId,
                subjectId = subjectId,
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                transactionFlow = EntryFlowTypeEnum.OutFlow.name,
                referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE
            )

            val totalCount = AssetCountUiModel.getAssetCountUiModel(
                subjectId = subjectId,
                livelihoodId = inflowAssetCount?.livelihoodId.value(),
                assetId = inflowAssetCount?.assetId.value(),
                totalAssetCountForFlow = (inflowAssetCount?.totalAssetCountForFlow ?: 0)
                        - (outFlowAssetCount?.totalAssetCountForFlow?.value() ?: 0),
            )
            assetCountUiModelList.add(totalCount)
        }

        return assetCountUiModelList
    }

    override suspend fun getIncomeExpenseSummaryForSubjectForDuration(
        subjectId: Int,
        assets: List<AssetEntity>,
        durationStart: Long,
        durationEnd: Long
    ): IncomeExpenseSummaryUiModel {
        val totalIncome =
            getTotalIncomeForSubjectForDuration(subjectId = subjectId, durationStart, durationEnd)
        val totalExpense =
            getTotalExpenseForSubjectForDuration(subjectId = subjectId, durationStart, durationEnd)
        val assetCounts = getAssetCountForAssetsForDuration(
            subjectId,
            assets.map { it.assetId },
            durationStart,
            durationEnd
        )

        val livelihoodAssetMap = assets.groupBy { it.livelihoodId }

        val assetsCountWithValue = AssetsCountWithValueUiModel
            .getAssetsCountWithValueUiModelList(assetsList = assets, assetCounts)

        val totalAssetCountForLivelihood = hashMapOf<Int, Int>()

        livelihoodAssetMap.forEach { mapEntry ->

            var totalAssetCount = 0
            mapEntry.value.forEach {
                totalAssetCount += (assetsCountWithValue.find(it.assetId)?.assetCount ?: 0)
            }

            totalAssetCountForLivelihood.put(mapEntry.key, totalAssetCount)
        }

        return IncomeExpenseSummaryUiModel
            .getIncomeExpenseSummaryUiModel(
                subjectId = subjectId,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                livelihoodAssetMap = livelihoodAssetMap,
                totalAssetCountForLivelihood = totalAssetCountForLivelihood,
                assetsCountWithValue = assetsCountWithValue
            )
    }

    override suspend fun getTotalIncomeForSubjectForDuration(
        subjectId: Int,
        durationStart: Long,
        durationEnd: Long
    ): Double {
        return moneyJournalDao.getTotalIncomeExpenseForSubjectForDuration(
            transactionFlow = EntryFlowTypeEnum.Inflow.name,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            subjectId = subjectId.value(),
            referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE,
            durationStart = durationStart,
            durationEnd = durationEnd
        )?.totalIncome.value()
    }

    override suspend fun getTotalExpenseForSubjectForDuration(
        subjectId: Int,
        durationStart: Long,
        durationEnd: Long
    ): Double {
        return moneyJournalDao.getTotalIncomeExpenseForSubjectForDuration(
            transactionFlow = EntryFlowTypeEnum.OutFlow.name,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            subjectId = subjectId.value(),
            referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE,
            durationStart = durationStart,
            durationEnd = durationEnd
        )?.totalIncome.value()
    }

    override suspend fun getAssetCountForAssetsForDuration(
        subjectId: Int,
        assetIds: List<Int>,
        durationStart: Long,
        durationEnd: Long
    ): List<AssetCountUiModel> {
        val assetCountUiModelList = ArrayList<AssetCountUiModel>()
        assetIds.forEach { assetId ->
            val inflowAssetCount = assetJournalDao.getAssetCountForAssetForDuration(
                assetId = assetId,
                subjectId = subjectId,
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                transactionFlow = EntryFlowTypeEnum.Inflow.name,
                referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE,
                durationStart = durationStart,
                durationEnd = durationEnd
            )
            val outFlowAssetCount = assetJournalDao.getAssetCountForAssetForDuration(
                assetId = assetId,
                subjectId = subjectId,
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                transactionFlow = EntryFlowTypeEnum.OutFlow.name,
                referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE,
                durationStart = durationStart,
                durationEnd = durationEnd
            )

            val totalCount = AssetCountUiModel.getAssetCountUiModel(
                subjectId = subjectId,
                livelihoodId = inflowAssetCount?.livelihoodId.value(),
                assetId = inflowAssetCount?.assetId.value(),
                totalAssetCountForFlow = (inflowAssetCount?.totalAssetCountForFlow ?: 0)
                        - (outFlowAssetCount?.totalAssetCountForFlow?.value() ?: 0),
            )
            assetCountUiModelList.add(totalCount)
        }

        return assetCountUiModelList
    }

    override fun getUserId() = coreSharedPrefs.getUniqueUserIdentifier()

}