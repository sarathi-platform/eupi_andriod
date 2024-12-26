package com.nudge.core.data.repository.caste

import com.nudge.core.BLANK_STRING
import com.nudge.core.apiService.CoreApiService
import com.nudge.core.database.dao.CasteListDao
import com.nudge.core.database.entities.CasteEntity
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.CasteModel
import com.nudge.core.preference.CoreSharedPrefs
import javax.inject.Inject

class CasteConfigRepositoryImpl @Inject constructor(
    val coreApiService: CoreApiService,
    val casteListDao: CasteListDao,
    val coreSharedPrefs: CoreSharedPrefs
) : ICasteConfigRepository {
    override suspend fun getAllCaste(): List<CasteEntity> {
        return casteListDao.getAllCaste()
    }

    override suspend fun getAllCasteForLanguage(): List<CasteEntity> {
        return casteListDao.getAllCasteForLanguage(languageId = coreSharedPrefs.getSelectedLanguageId())
    }

    override suspend fun getCaste(id: Int): CasteEntity {
        return casteListDao.getCaste(id = id, languageId = coreSharedPrefs.getSelectedLanguageId())
    }

    override suspend fun insertCaste(caste: CasteEntity) {
        casteListDao.insertCaste(caste)
    }

    override suspend fun insertAll(castes: List<CasteEntity>) {
        casteListDao.insertAll(castes)
    }

    override suspend fun deleteCasteTable() {
        casteListDao.deleteCasteTable()
    }

    override suspend fun getCasteIdValue(casteId: Int): String? {
        return casteListDao.getCasteValue(
            id = casteId,
            languageId = coreSharedPrefs.getSelectedLanguageId()
        ) ?: BLANK_STRING
    }

    override suspend fun deleteCasteTableForLanguage() {
        casteListDao.deleteCasteTableForLanguage(coreSharedPrefs.getSelectedLanguageId())
    }

    override suspend fun getCasteConfigFromNetwork(): ApiResponseModel<List<CasteModel>> {
        return coreApiService.getCasteList()
    }
}