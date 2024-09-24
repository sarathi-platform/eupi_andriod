package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.model.uiModel.LivelihoodModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodDao
import javax.inject.Inject


class GetLivelihoodListFromDbRepositoryImpl @Inject constructor(
    val livelihoodDao: LivelihoodDao,
    val coreSharedPrefs: CoreSharedPrefs
) : GetLivelihoodListFromDbRepository {

    override suspend fun getLivelihoodListFromDb(): List<LivelihoodModel> {
        return livelihoodDao.getLivelihoodList(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            languageCode = coreSharedPrefs.getSelectedLanguageCode()
        )
    }

    override suspend fun getLivelihoodListFromDb(livelihoodIds: List<Int>): List<LivelihoodModel> {
        return livelihoodDao.getLivelihoodList(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            languageCode = coreSharedPrefs.getSelectedLanguageCode(),
            livelihoodIds
        )
    }

    override suspend fun getLivelihoodListForFilterFromDb(): List<LivelihoodModel> {
        return livelihoodDao.getLivelihoodListWithoutNotDecided(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            languageCode = coreSharedPrefs.getSelectedLanguageCode()
        )
    }


}
