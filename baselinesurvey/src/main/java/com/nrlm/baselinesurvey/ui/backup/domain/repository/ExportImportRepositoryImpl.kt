package com.nrlm.baselinesurvey.ui.backup.domain.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_EMAIL
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import javax.inject.Inject

class ExportImportRepositoryImpl @Inject constructor(
    val prefRepo: PrefRepo,
    val nudgeBaselineDatabase:NudgeBaselineDatabase
):ExportImportRepository {
    override fun clearLocalData() {
        nudgeBaselineDatabase.contentEntityDao().deleteContent()
        nudgeBaselineDatabase.didiDao().deleteSurveyees()
        nudgeBaselineDatabase.activityTaskEntityDao().deleteActivityTask(prefRepo.getUserId())
        nudgeBaselineDatabase.missionEntityDao().deleteMissions(prefRepo.getUserId())
        nudgeBaselineDatabase.missionActivityEntityDao().deleteActivities(prefRepo.getUserId())
        nudgeBaselineDatabase.optionItemDao().deleteOptions(prefRepo.getUserId())
        nudgeBaselineDatabase.questionEntityDao().deleteAllQuestions(prefRepo.getUserId())
        nudgeBaselineDatabase.sectionAnswerEntityDao().deleteAllSectionAnswer(prefRepo.getUserId())
        nudgeBaselineDatabase.inputTypeQuestionAnswerDao().deleteAllInputTypeAnswers(prefRepo.getUserId())
        nudgeBaselineDatabase.formQuestionResponseDao().deleteAllFormQuestions(prefRepo.getUserId())
        nudgeBaselineDatabase.didiSectionProgressEntityDao().deleteAllSectionProgress(prefRepo.getUserId())
        nudgeBaselineDatabase.villageListDao().deleteAllVilleges()
        nudgeBaselineDatabase.surveyEntityDao().deleteAllSurvey(prefRepo.getUserId())
        nudgeBaselineDatabase.didiInfoEntityDao().deleteAllDidiInfo(prefRepo.getUserId())
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