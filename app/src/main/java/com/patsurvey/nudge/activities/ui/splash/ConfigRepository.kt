package com.patsurvey.nudge.activities.ui.splash

import com.nrlm.baselinesurvey.BLANK_STRING
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.BpcScorePercentageEntity
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.dao.BpcScorePercentageDao
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.ConfigResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.addDefaultLanguage
import javax.inject.Inject

class ConfigRepository @Inject constructor(
    val apiService: ApiService,
    val languageListDao: LanguageListDao,
    val baselineLanguageDao:com.nrlm.baselinesurvey.database.dao.LanguageListDao,
    val bpcScorePercentageDao: BpcScorePercentageDao,
    val prefRepo: PrefRepo
) : BaseRepository() {

    suspend fun fetchLanguageFromAPI(): ApiResponseModel<ConfigResponseModel> {
        return apiService.configDetails()
    };

    override fun onServerError(error: ErrorModel?) {
        addDefaultLanguage(languageListDao)
    }

    fun getAllLanguages(): List<LanguageEntity> = languageListDao.getAllLanguages()
    fun insertAllLanguages(configResponseModel: ConfigResponseModel){
       languageListDao.insertAll(configResponseModel.languageList)
        configResponseModel.languageList?.let {
            val bsLangList=ArrayList<com.nrlm.baselinesurvey.database.entity.LanguageEntity>()
            it.forEach {
                bsLangList.add(com.nrlm.baselinesurvey.database.entity.LanguageEntity(
                    id = it.id,
                    language = it.language,
                    localName = it.localName,
                    orderNumber = it.orderNumber,
                    langCode = it.langCode
                ))
            }
            baselineLanguageDao.insertAll(bsLangList)
        }
        configResponseModel.bpcSurveyPercentage.forEach { bpcScorePercentage ->
            bpcScorePercentageDao.insert(
                BpcScorePercentageEntity(
                    percentage = bpcScorePercentage.percentage,
                    name = bpcScorePercentage.name,
                    stateId = bpcScorePercentage.id
                )
            )
        }
    }

    fun getAccessToken(): String?{
        return prefRepo.getAccessToken()
    }
    fun getUserType(): String?{
        return prefRepo.getPref(PREF_KEY_TYPE_NAME, BLANK_STRING)
    }
    override fun onServerError(errorModel: ErrorModelWithApi?) {
        addDefaultLanguage(languageListDao)
    }
    fun addDefaultLanguage(){
        addDefaultLanguage(languageListDao)
    }


}