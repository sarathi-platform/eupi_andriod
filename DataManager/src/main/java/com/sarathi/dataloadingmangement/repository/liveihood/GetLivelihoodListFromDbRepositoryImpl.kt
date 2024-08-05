package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodDao
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.LivelihoodModel
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


}
