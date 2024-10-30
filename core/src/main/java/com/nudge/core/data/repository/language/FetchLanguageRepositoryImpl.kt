package com.nudge.core.data.repository.language

import com.nudge.core.apiService.CoreApiService
import com.nudge.core.database.dao.language.LanguageListDao
import com.nudge.core.database.entities.language.LanguageEntity
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.response.language.LanguageConfigModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_TYPE_STATE_ID
import javax.inject.Inject

class FetchLanguageRepositoryImpl @Inject constructor(
    private val apiInterface: CoreApiService,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val languageListDao: LanguageListDao,
) : IFetchLanguageRepository {

    override suspend fun getLanguageV3FromNetwork(): ApiResponseModel<LanguageConfigModel> {
        val stateId = if (coreSharedPrefs.getStateId() == -1) coreSharedPrefs.getPref(
            PREF_KEY_TYPE_STATE_ID,
            -1
        ) else coreSharedPrefs.getStateId()
        return apiInterface.languageConfigV3(if (stateId == -1) null else stateId)
    }

    override fun saveLanguageDataToDB(languageList: List<LanguageEntity>) {
        languageListDao.insertAll(languageList)

    }

    override suspend fun deleteLanguageDataFromDB() {
        languageListDao.deleteAllLanguage()
    }

    override suspend fun getAllLanguages(): List<LanguageEntity> {
        return languageListDao.getAllLanguages()

    }
}