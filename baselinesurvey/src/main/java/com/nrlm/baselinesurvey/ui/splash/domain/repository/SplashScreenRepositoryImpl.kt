package com.nrlm.baselinesurvey.ui.splash.domain.repository

import android.text.TextUtils
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.LANGUAGE_OPEN_FROM_SETTING
import com.nrlm.baselinesurvey.base.BaseRepository
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
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
    private val prefBSRepo: PrefBSRepo,
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
        return !TextUtils.isEmpty(prefBSRepo.getAccessToken())
    }

    override fun saveLanguageOpenFrom() {
        prefBSRepo.savePref(LANGUAGE_OPEN_FROM_SETTING, false)
    }

    override fun isDataSynced(): Boolean {
        return prefBSRepo.getDataSyncStatus()
    }

    override fun setAllDataSynced() {
        prefBSRepo.setDataSyncStatus(true)
    }

    override fun performLogout(clearData: Boolean) {
        if (clearData) {
            clearLocalData()
        } else {
            prefBSRepo.saveAccessToken("")
            prefBSRepo.saveMobileNumber("")
        }
    }

    override fun getPreviousMobileNumber(): String {
        return prefBSRepo.getPreviousUserMobile()
    }

    override fun getMobileNumber(): String {
        return prefBSRepo.getMobileNumber() ?: BLANK_STRING
    }

    override fun clearLocalData() {
        nudgeBaselineDatabase.contentEntityDao().deleteContent()
        nudgeBaselineDatabase.didiDao().deleteSurveyees(getBaseLineUserId())
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
        val languageId = prefBSRepo.getAppLanguageId()
        val language = prefBSRepo.getAppLanguage()
        val accessToken = prefBSRepo.getAccessToken()
        val mobileNumber = prefBSRepo.getMobileNumber()
        prefBSRepo.clearSharedPreference()
        prefBSRepo.saveAccessToken(accessToken ?: BLANK_STRING)
        prefBSRepo.saveMobileNumber(mobileNumber ?: BLANK_STRING)
        prefBSRepo.saveAppLanguage(language)
        prefBSRepo.saveAppLanguageId(languageId)
        coreSharedPrefs.setBackupFileName(
            getDefaultBackUpFileName(
                prefBSRepo.getMobileNumber() ?: BLANK_STRING
            )
        )
        coreSharedPrefs.setImageBackupFileName(
            getDefaultImageBackUpFileName(
                prefBSRepo.getMobileNumber() ?: ""
            )
        )
    }

    fun getBaseLineUserId(): String {
        return this.prefBSRepo.getUniqueUserIdentifier()
    }

}