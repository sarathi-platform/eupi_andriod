package com.patsurvey.nudge.activities.ui.progress.domain.repository.impls

import com.nudge.core.preference.CorePrefRepo
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.FetchCasteListRepository
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.model.response.ApiResponseModel
import javax.inject.Inject

class FetchCasteListRepositoryImpl @Inject constructor(
    private val languageListDao: LanguageListDao,
    private val casteListDao: CasteListDao,
    private val corePrefRepo: CorePrefRepo
) : FetchCasteListRepository {

    override fun getAllCastesForLanguage(languageId: Int): List<CasteEntity> {
        TODO("Not yet implemented")
    }

    override fun fetchCasteListFromNetwork(languageId: Int): ApiResponseModel<List<CasteEntity>> {
        TODO("Not yet implemented")
    }

    override fun saveCasteListToDb(casteList: List<CasteEntity>) {
        TODO("Not yet implemented")
    }

}