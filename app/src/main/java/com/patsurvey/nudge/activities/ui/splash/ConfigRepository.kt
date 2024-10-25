package com.patsurvey.nudge.activities.ui.splash

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nudge.core.database.entities.language.LanguageEntity
import com.nudge.core.model.response.language.LanguageConfigModel
import com.nudge.core.usecase.language.LanguageConfigUseCase
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.BpcScorePercentageEntity
import com.patsurvey.nudge.database.dao.BpcScorePercentageDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import javax.inject.Inject

class ConfigRepository @Inject constructor(
    val apiService: ApiService,
    val bpcScorePercentageDao: BpcScorePercentageDao,
    val prefRepo: PrefRepo,
    val languageConfigUseCase: LanguageConfigUseCase
) : BaseRepository() {

    override fun onServerError(error: ErrorModel?) {
        languageConfigUseCase.addDefaultLanguage()
    }

    suspend fun getAllLanguages(): List<LanguageEntity> = languageConfigUseCase.getAllLanguage()
    suspend fun insertAllLanguages(configResponseModel: LanguageConfigModel) {
        languageConfigUseCase.saveLanguageConfig(configResponseModel.languageList)
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

    fun getAccessToken(): String? {
        return prefRepo.getAccessToken()
    }

    fun getUserType(): String? {
        return prefRepo.getPref(PREF_KEY_TYPE_NAME, BLANK_STRING)
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        languageConfigUseCase.addDefaultLanguage()
    }

    fun addDefaultLanguage() {
        languageConfigUseCase.addDefaultLanguage()
    }

    fun getLoggedInUserType(): String {
        return prefRepo.getLoggedInUserType()
    }

}