package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.database.dao.ApiConfigDao
import com.nudge.core.enums.AppConfigKeysEnum
import com.nudge.core.fromJson
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
    private val assetDao: AssetDao,
    private val appConfigDao: ApiConfigDao,
) : FetchSubjectIncomeExpenseSummaryRepository {

    private val LIVELIHOOD_EVENT_REFERENCE_TYPE: String = "LivelihoodEvent"

    override suspend fun getIncomeExpenseSummaryForSubject(
        subjectId: Int,
        assets: List<AssetEntity>
    ): IncomeExpenseSummaryUiModel {
        val exclusionEventIds = appConfigDao.getConfig(
            AppConfigKeysEnum.EXCLUDE_IN_INCOME_SUMMARY.name,
            coreSharedPrefs.getUniqueUserIdentifier()
        )?.value.fromJson<List<Int>?>().value()

        val totalIncome = getTotalIncomeForSubject(subjectId = subjectId, exclusionEventIds)
        val totalExpense = getTotalExpenseForSubject(subjectId = subjectId, exclusionEventIds)
        val assetCounts =
            getAssetCountForAssets(subjectId, assets.map { Pair(it.livelihoodId, it.assetId) })

        val livelihoodAssetMap = assets.groupBy { it.livelihoodId }

        val assetsCountWithValue = AssetsCountWithValueUiModel
            .getAssetsCountWithValueUiModelList(assetsList = assets, assetCounts)

        val totalAssetCountForLivelihood = hashMapOf<Int, Int>()
        val imageUriForLivelihood = hashMapOf<Int, String>()

        livelihoodAssetMap.forEach { mapEntry ->
            setLivelihoodImageMapping(mapEntry, imageUriForLivelihood)
            var totalAssetCount = 0
            mapEntry.value.forEach { asset ->
                totalAssetCount += (assetsCountWithValue.find { it.assetId == asset.assetId && it.livelihoodId == asset.livelihoodId }?.assetCount
                    ?: 0)
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
                assetsCountWithValue = assetsCountWithValue,
                imageUriForLivelihood = imageUriForLivelihood
            )
    }

    private fun setLivelihoodImageMapping(
        mapEntry: Map.Entry<Int, List<AssetEntity>>,
        imageUriForLivelihood: HashMap<Int, String>
    ) {
        val image = livelihoodDao.getLivelihoodImageForUser(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            livelihoodId = mapEntry.key
        )?.image
        image?.let {
            imageUriForLivelihood[mapEntry.key] = it
        }
    }


    override suspend fun getIncomeExpenseSummaryForSubject(
        subjectId: Int,
        assets: List<AssetEntity>,
        livelihoodId: Int
    ): IncomeExpenseSummaryUiModel {
        val exclusionEventIds = appConfigDao.getConfig(
            AppConfigKeysEnum.EXCLUDE_IN_INCOME_SUMMARY.name,
            coreSharedPrefs.getUniqueUserIdentifier()
        )?.value.fromJson<List<Int>?>().value()

        val totalIncome = getTotalIncomeForSubjectLivelihood(
            subjectId = subjectId,
            livelihoodId,
            exclusionEventIds
        )
        val totalExpense = getTotalExpenseForSubjectLivelihood(
            subjectId = subjectId,
            livelihoodId,
            exclusionEventIds
        )
        val assetCounts =
            getAssetCountForAssets(subjectId, assets.map { Pair(it.livelihoodId, it.assetId) })

        val livelihoodAssetMap = assets.groupBy { it.livelihoodId }

        val assetsCountWithValue = AssetsCountWithValueUiModel
            .getAssetsCountWithValueUiModelList(assetsList = assets, assetCounts)

        val totalAssetCountForLivelihood = hashMapOf<Int, Int>()
        val imageUriForLivelihood = hashMapOf<Int, String>()

        livelihoodAssetMap.forEach { mapEntry ->
            setLivelihoodImageMapping(mapEntry, imageUriForLivelihood)

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
                assetsCountWithValue = assetsCountWithValue,
                imageUriForLivelihood = imageUriForLivelihood
            )
    }

    override suspend fun getTotalIncomeForSubject(
        subjectId: Int,
        exclusionEventIds: List<Int>
    ): Double {
        return moneyJournalDao.getTotalIncomeExpenseForSubject(
            transactionFlow = EntryFlowTypeEnum.INFLOW.name,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            subjectId = subjectId.value(),
            referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE,
            exclusionEventIds = exclusionEventIds
        )?.totalIncome.value()
    }

    override suspend fun getTotalIncomeForSubjectLivelihood(
        subjectId: Int,
        livelihoodId: Int,
        exclusionEventIds: List<Int>
    ): Double {
        return moneyJournalDao.getTotalIncomeExpenseForSubject(
            transactionFlow = EntryFlowTypeEnum.INFLOW.name,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            subjectId = subjectId.value(),
            referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE,
            referenceId = livelihoodId,
            exclusionEventIds = exclusionEventIds
        )?.totalIncome.value()
    }

    override suspend fun getTotalExpenseForSubject(
        subjectId: Int,
        exclusionEventIds: List<Int>
    ): Double {
        return moneyJournalDao.getTotalIncomeExpenseForSubject(
            transactionFlow = EntryFlowTypeEnum.OUTFLOW.name,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            subjectId = subjectId.value(),
            referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE,
            exclusionEventIds = exclusionEventIds
        )?.totalIncome.value()
    }

    override suspend fun getTotalExpenseForSubjectLivelihood(
        subjectId: Int,
        livelihoodId: Int,
        exclusionEventIds: List<Int>
    ): Double {
        return moneyJournalDao.getTotalIncomeExpenseForSubject(
            transactionFlow = EntryFlowTypeEnum.OUTFLOW.name,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            subjectId = subjectId.value(),
            referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE,
            referenceId = livelihoodId,
            exclusionEventIds = exclusionEventIds
        )?.totalIncome.value()
    }

    override suspend fun getAssetCountForAssets(
        subjectId: Int,
        assetIds: List<Pair<Int, Int>>,
    ): List<AssetCountUiModel> {
        val assetCountUiModelList = ArrayList<AssetCountUiModel>()
        assetIds.forEach { assetId ->
            val INFLOWAssetCount = assetJournalDao.getAssetCountForAsset(
                livelihoodId = assetId.first,
                assetId = assetId.second,
                subjectId = subjectId,
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                transactionFlow = EntryFlowTypeEnum.INFLOW.name,
                referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE
            )
            val outFlowAssetCount = assetJournalDao.getAssetCountForAsset(
                livelihoodId = assetId.first,
                assetId = assetId.second,
                subjectId = subjectId,
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                transactionFlow = EntryFlowTypeEnum.OUTFLOW.name,
                referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE
            )

            val totalCount = AssetCountUiModel.getAssetCountUiModel(
                subjectId = subjectId,
                livelihoodId = INFLOWAssetCount?.livelihoodId.value(),
                assetId = INFLOWAssetCount?.assetId.value(),
                totalAssetCountForFlow = (INFLOWAssetCount?.totalAssetCountForFlow ?: 0)
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
            assets.map { Pair(it.livelihoodId, it.assetId) },
            durationStart,
            durationEnd
        )

        val livelihoodAssetMap = assets.groupBy { it.livelihoodId }

        val assetsCountWithValue = AssetsCountWithValueUiModel
            .getAssetsCountWithValueUiModelList(assetsList = assets, assetCounts)

        val totalAssetCountForLivelihood = hashMapOf<Int, Int>()
        val imageUriForLivelihood = hashMapOf<Int, String>()

        livelihoodAssetMap.forEach { mapEntry ->
            setLivelihoodImageMapping(mapEntry, imageUriForLivelihood)
            var totalAssetCount = 0
            mapEntry.value.forEach { asset ->
                totalAssetCount += (assetsCountWithValue.find { it.livelihoodId == mapEntry.key && asset.assetId == it.assetId }?.assetCount
                    ?: 0)
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
                assetsCountWithValue = assetsCountWithValue,
                imageUriForLivelihood = imageUriForLivelihood
            )
    }

    override suspend fun getTotalIncomeForSubjectForDuration(
        subjectId: Int,
        durationStart: Long,
        durationEnd: Long
    ): Double {
        return moneyJournalDao.getTotalIncomeExpenseForSubjectForDuration(
            transactionFlow = EntryFlowTypeEnum.INFLOW.name,
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
            transactionFlow = EntryFlowTypeEnum.OUTFLOW.name,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            subjectId = subjectId.value(),
            referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE,
            durationStart = durationStart,
            durationEnd = durationEnd
        )?.totalIncome.value()
    }

    override suspend fun getAssetCountForAssetsForDuration(
        subjectId: Int,
        assetIds: List<Pair<Int, Int>>,
        durationStart: Long,
        durationEnd: Long
    ): List<AssetCountUiModel> {
        val assetCountUiModelList = ArrayList<AssetCountUiModel>()
        assetIds.forEach { assetId ->
            val INFLOWAssetCount = assetJournalDao.getAssetCountForAssetForDuration(
                livelihoodId = assetId.first,
                assetId = assetId.second,
                subjectId = subjectId,
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                transactionFlow = EntryFlowTypeEnum.INFLOW.name,
                referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE,
                durationStart = durationStart,
                durationEnd = durationEnd
            )
            val outFlowAssetCount = assetJournalDao.getAssetCountForAssetForDuration(
                livelihoodId = assetId.first,
                assetId = assetId.second,
                subjectId = subjectId,
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                transactionFlow = EntryFlowTypeEnum.OUTFLOW.name,
                referenceType = LIVELIHOOD_EVENT_REFERENCE_TYPE,
                durationStart = durationStart,
                durationEnd = durationEnd
            )

            val totalCount = AssetCountUiModel.getAssetCountUiModel(
                subjectId = subjectId,
                livelihoodId = INFLOWAssetCount?.livelihoodId.value(),
                assetId = INFLOWAssetCount?.assetId.value(),
                totalAssetCountForFlow = (INFLOWAssetCount?.totalAssetCountForFlow ?: 0)
                        - (outFlowAssetCount?.totalAssetCountForFlow?.value() ?: 0),
            )
            assetCountUiModelList.add(totalCount)
        }

        return assetCountUiModelList
    }

    override fun getUserId() = coreSharedPrefs.getUniqueUserIdentifier()
}