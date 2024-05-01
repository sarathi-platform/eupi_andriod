package com.nrlm.baselinesurvey.ui.splash.domain.repository

import android.text.TextUtils
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.LANGUAGE_OPEN_FROM_SETTING
import com.nrlm.baselinesurvey.base.BaseRepository
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nrlm.baselinesurvey.database.dao.LanguageListDao
import com.nrlm.baselinesurvey.database.entity.LanguageEntity
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.model.response.ConfigResponseModel
import com.nrlm.baselinesurvey.network.interfaces.BaseLineApiService
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.getDefaultLanguage
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import com.nudge.core.preference.CoreSharedPrefs
import javax.inject.Inject

class SplashScreenRepositoryImpl @Inject constructor(
    private val prefRepo: PrefRepo,
    private val apiService: BaseLineApiService,
    private val languageListDao: LanguageListDao,
    private val nudgeBaselineDatabase: NudgeBaselineDatabase
) : SplashScreenRepository, BaseRepository() {

    override suspend fun getLanguageConfigFromNetwork(): ApiResponseModel<ConfigResponseModel?> {
        return apiService.fetchLanguageConfigDetailsFromNetwork()
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
        prefRepo.savePref(LANGUAGE_OPEN_FROM_SETTING, false)
    }

    override fun isDataSynced(): Boolean {
        return prefRepo.getDataSyncStatus()
    }

    override fun setAllDataSynced() {
        prefRepo.setDataSyncStatus(true)
    }

    override fun performLogout(clearData: Boolean) {
        if (clearData) {
            clearLocalData()
        } else {
            prefRepo.saveAccessToken("")
            prefRepo.saveMobileNumber("")
        }
    }

    override fun getPreviousMobileNumber(): String {
        return prefRepo.getPreviousUserMobile()
    }

    override fun getMobileNumber(): String {
        return prefRepo.getMobileNumber() ?: BLANK_STRING
    }

    override fun clearLocalData() {
        nudgeBaselineDatabase.contentEntityDao().deleteContent()
        nudgeBaselineDatabase.didiDao().deleteSurveyees()
        nudgeBaselineDatabase.activityTaskEntityDao().deleteActivityTask(getBaseLineUserId())
        nudgeBaselineDatabase.missionEntityDao().deleteMissions(getBaseLineUserId())
        nudgeBaselineDatabase.missionActivityEntityDao().deleteActivities(getBaseLineUserId())
        nudgeBaselineDatabase.optionItemDao().deleteOptions(getBaseLineUserId())
        nudgeBaselineDatabase.questionEntityDao().deleteAllQuestions(getBaseLineUserId())
        nudgeBaselineDatabase.sectionAnswerEntityDao().deleteAllSectionAnswer(getBaseLineUserId())
        nudgeBaselineDatabase.inputTypeQuestionAnswerDao()
            .deleteAllInputTypeAnswers(getBaseLineUserId())
        nudgeBaselineDatabase.formQuestionResponseDao().deleteAllFormQuestions(getBaseLineUserId())
        nudgeBaselineDatabase.didiSectionProgressEntityDao()
            .deleteAllSectionProgress(getBaseLineUserId())
        nudgeBaselineDatabase.villageListDao().deleteAllVilleges()
        nudgeBaselineDatabase.surveyEntityDao().deleteAllSurvey(getBaseLineUserId())
        nudgeBaselineDatabase.didiInfoEntityDao().deleteAllDidiInfo(getBaseLineUserId())
        clearSharedPref()
    }

    override fun clearSharedPref() {
        val coreSharedPrefs = CoreSharedPrefs.getInstance(BaselineCore.getAppContext())
        val languageId = prefRepo.getAppLanguageId()
        val language = prefRepo.getAppLanguage()
        val accessToken = prefRepo.getAccessToken()
        val mobileNumber = prefRepo.getMobileNumber()
        prefRepo.clearSharedPreference()
        prefRepo.saveAccessToken(accessToken ?: BLANK_STRING)
        prefRepo.saveMobileNumber(mobileNumber ?: BLANK_STRING)
        prefRepo.saveAppLanguage(language)
        prefRepo.saveAppLanguageId(languageId)
        coreSharedPrefs.setBackupFileName(
            getDefaultBackUpFileName(
                prefRepo.getMobileNumber() ?: BLANK_STRING
            )
        )
        coreSharedPrefs.setImageBackupFileName(
            getDefaultImageBackUpFileName(
                prefRepo.getMobileNumber() ?: ""
            )
        )
    }

    fun getBaseLineUserId(): String {
        return this.prefRepo.getUniqueUserIdentifier()
    }

}