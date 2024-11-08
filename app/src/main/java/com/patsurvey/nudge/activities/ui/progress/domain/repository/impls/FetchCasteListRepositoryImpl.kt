package com.patsurvey.nudge.activities.ui.progress.domain.repository.impls

import com.nudge.core.preference.CorePrefRepo
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.FetchCasteListRepository
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService
import javax.inject.Inject

class FetchCasteListRepositoryImpl @Inject constructor(
    private val casteListDao: CasteListDao,
    private val apiService: ApiService,
    private val corePrefRepo: CorePrefRepo
) : FetchCasteListRepository {

    override fun getAllCastesForLanguage(languageId: Int): List<CasteEntity> {
        return casteListDao.getAllCasteForLanguage(languageId)
    }

    override suspend fun fetchCasteListFromNetwork(languageId: Int): ApiResponseModel<List<CasteEntity>>? {
        val localCasteList = getAllCastesForLanguage(languageId)
        return if (localCasteList.isEmpty()) {
            try {
                apiService.getCasteList(languageId)

            } catch (ex: Exception) {
                throw ex
            }
        } else {
            null
        }
    }

    override suspend fun saveCasteListToDb(casteList: List<CasteEntity>) {
        casteListDao.insertAll(casteList)
    }

    override suspend fun deleteCasteForLanguage(languageId: Int) {
        casteListDao.deleteCasteTableForLanguage(languageId)
    }


}