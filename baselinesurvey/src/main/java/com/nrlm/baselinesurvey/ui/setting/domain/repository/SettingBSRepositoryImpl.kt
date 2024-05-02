package com.nrlm.baselinesurvey.ui.setting.domain.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.LANGUAGE_OPEN_FROM_SETTING
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nrlm.baselinesurvey.PREF_KEY_EMAIL
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import com.nudge.core.preference.CoreSharedPrefs

class SettingBSRepositoryImpl(private val prefRepo: PrefRepo,
                              private val apiService: ApiService,
                              private val nudgeBaselineDatabase: NudgeBaselineDatabase
    ):SettingBSRepository {

    override suspend fun performLogout(): ApiResponseModel<String> {
        return apiService.performLogout()
    }

    override fun clearSharedPref() {
        prefRepo.saveAccessToken(BLANK_STRING)
        val coreSharedPrefs = CoreSharedPrefs.getInstance(BaselineCore.getAppContext())
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
        coreSharedPrefs.setFileExported(false)
        prefRepo.setPreviousUserMobile(prefRepo.getMobileNumber() ?: BLANK_STRING)

    }

    override fun saveLanguageScreenOpenFrom() {
        prefRepo.savePref(LANGUAGE_OPEN_FROM_SETTING,true)
    }

    override fun getUserName(): String {
        return prefRepo.getPref(PREF_KEY_NAME, BLANK_STRING) ?: BLANK_STRING

    }

    override fun clearLocalData() {
        nudgeBaselineDatabase.contentEntityDao().deleteContent()
        nudgeBaselineDatabase.didiDao().deleteSurveyees(prefRepo.getUniqueUserIdentifier())
        nudgeBaselineDatabase.activityTaskEntityDao().deleteActivityTask(prefRepo.getUniqueUserIdentifier())
        nudgeBaselineDatabase.missionEntityDao().deleteMissions(prefRepo.getUniqueUserIdentifier())
        nudgeBaselineDatabase.missionActivityEntityDao().deleteActivities(prefRepo.getUniqueUserIdentifier())
        nudgeBaselineDatabase.optionItemDao().deleteOptions(prefRepo.getUniqueUserIdentifier())
        nudgeBaselineDatabase.questionEntityDao().deleteAllQuestions(prefRepo.getUniqueUserIdentifier())
        nudgeBaselineDatabase.sectionAnswerEntityDao().deleteAllSectionAnswer(prefRepo.getUniqueUserIdentifier())
        nudgeBaselineDatabase.inputTypeQuestionAnswerDao().deleteAllInputTypeAnswers(prefRepo.getUniqueUserIdentifier())
        nudgeBaselineDatabase.formQuestionResponseDao().deleteAllFormQuestions(prefRepo.getUniqueUserIdentifier())
        nudgeBaselineDatabase.didiSectionProgressEntityDao().deleteAllSectionProgress(prefRepo.getUniqueUserIdentifier())
        nudgeBaselineDatabase.villageListDao().deleteAllVilleges()
        nudgeBaselineDatabase.surveyEntityDao().deleteAllSurvey(prefRepo.getUniqueUserIdentifier())
        nudgeBaselineDatabase.didiInfoEntityDao().deleteAllDidiInfo(prefRepo.getUniqueUserIdentifier())
    }

    override fun setAllDataSynced() {
        prefRepo.setDataSyncStatus(false)
    }

    override fun getUserMobileNumber(): String {
        return prefRepo.getMobileNumber()?: BLANK_STRING
    }

    override fun getUserID(): String {
       return prefRepo.getUserId()
    }

    override fun getUserEmail(): String {
        return prefRepo.getPref(PREF_KEY_EMAIL, BLANK_STRING)?: BLANK_STRING
    }
}