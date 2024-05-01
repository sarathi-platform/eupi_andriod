package com.nrlm.baselinesurvey.ui.backup.domain.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_EMAIL
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nrlm.baselinesurvey.utils.BaselineLogger
import javax.inject.Inject

class ExportImportRepositoryImpl @Inject constructor(
    val prefRepo: PrefRepo,
    val nudgeBaselineDatabase:NudgeBaselineDatabase
):ExportImportRepository {
    override fun clearLocalData() {
        try {

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
        }catch (ex:Exception){
            ex.printStackTrace()
            BaselineLogger.d("ExportImportRepositoryImpl","clearLocalData: ${ex.message}")
        }

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

    override fun getUserName(): String {
        return prefRepo.getPref(PREF_KEY_NAME, BLANK_STRING) ?: BLANK_STRING
    }


}