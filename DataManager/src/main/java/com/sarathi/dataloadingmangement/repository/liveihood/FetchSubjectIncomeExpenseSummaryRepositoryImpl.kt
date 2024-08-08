package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.AssetDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.AssetJournalDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodEventDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.MoneyJournalDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetEntity
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.AssetCountUiModel
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel
import javax.inject.Inject

class FetchSubjectIncomeExpenseSummaryRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val livelihoodEventDao: LivelihoodEventDao,
    private val moneyJournalDao: MoneyJournalDao,
    private val assetJournalDao: AssetJournalDao,
    private val livelihoodDao: LivelihoodDao,
    private val assetDao: AssetDao
) : FetchSubjectIncomeExpenseSummaryRepository {

    override suspend fun getIncomeExpenseSummaryForSubject(
        subjectId: Int,
        subjectLivelihoodEventMapping: List<SubjectLivelihoodEventMappingEntity>?,
        assets: List<AssetEntity>
    ): IncomeExpenseSummaryUiModel? {
        return null
    }

    override suspend fun getTotalIncomeForSubject(subjectId: Int): Double {
        return 0.0
    }

    override suspend fun getTotalExpenseForSubject(subjectId: Int): Double {
        return 0.0
    }


    override suspend fun getAssetCountForAssets(assetIds: List<Int>): List<AssetCountUiModel> {
        return
    }

}