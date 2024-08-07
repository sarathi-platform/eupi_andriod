package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodEventDao
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventUiModel
import javax.inject.Inject

class LivelihoodEventRepositoryImpl @Inject constructor(
    private val livelihoodEventDao: LivelihoodEventDao,
    private val coreSharedPrefs: CoreSharedPrefs
) : ILivelihoodEventRepository {

    override suspend fun getEventsForLivelihood(livelihoodId: Int): List<LivelihoodEventUiModel> {
        return livelihoodEventDao.getEventsForLivelihood(
            livelihoodId = livelihoodId,
            languageCode = coreSharedPrefs.getAppLanguage(),
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

}