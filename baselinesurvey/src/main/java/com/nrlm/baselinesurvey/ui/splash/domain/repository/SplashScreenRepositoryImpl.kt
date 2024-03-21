package com.nrlm.baselinesurvey.ui.splash.domain.repository

import android.text.TextUtils
import com.nrlm.baselinesurvey.LANGUAGE_OPEN_FROM_SETTING
import com.nrlm.baselinesurvey.base.BaseRepository
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.LanguageListDao
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.ConfigResponseModel
import com.nrlm.baselinesurvey.network.interfaces.BaseLineApiService
import com.nrlm.baselinesurvey.utils.getDefaultLanguage
import javax.inject.Inject

class SplashScreenRepositoryImpl @Inject constructor(
    private val prefRepo: PrefRepo,
    private val baseLineApiService: BaseLineApiService,
    private val languageListDao: LanguageListDao
) : SplashScreenRepository, BaseRepository() {

    override suspend fun getLanguageConfigFromNetwork(): ApiResponseModel<ConfigResponseModel?> {
        return baseLineApiService.fetchLanguageConfigDetailsFromNetwork()
    }

    override suspend fun saveLanguageIntoDatabase(languageEntityList: List<LanguageEntity>) {
        languageListDao.insertAll(languageEntityList)
    }

    override suspend fun checkAndAddDefaultLanguage() {
        val localLanguage = languageListDao.getAllLanguages()
        if (localLanguage.isEmpty()) {
            val defaultLanguage = getDefaultLanguage()
            languageListDao.insertLanguage(defaultLanguage)
        }

    }

    override fun isLoggedIn(): Boolean {
        return !TextUtils.isEmpty(prefRepo.getAccessToken())
    }

    override fun saveLanguageOpenFrom() {
        prefRepo.savePref(LANGUAGE_OPEN_FROM_SETTING,false)
    }

    override fun isDataSynced(): Boolean {
        return prefRepo.getDataSyncStatus()
    }

    override fun setAllDataSynced() {
        prefRepo.setDataSyncStatus(true)
    }

}