package com.patsurvey.nudge.activities.ui.splash

import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.ConfigResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.addDefaultLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ConfigRepository @Inject constructor(
    val apiService: ApiService,
    val languageListDao: LanguageListDao
) : BaseRepository() {

    var job: Job? = null
    suspend fun fetchLanguageFromAPI(): ApiResponseModel<ConfigResponseModel> =
        withContext(Dispatchers.IO + exceptionHandler) {
            return@withContext apiService.configDetails()
        }
    override fun onServerError(error: ErrorModel?) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            addDefaultLanguage(languageListDao)
        }
    }

    fun getAllLanguages(): List<LanguageEntity> = languageListDao.getAllLanguages()
    fun insertAllLanguages(languageList:List<LanguageEntity>){
       languageListDao.insertAll(languageList)
    }
    override fun onServerError(errorModel: ErrorModelWithApi?) {
        addDefaultLanguage(languageListDao)
    }

}