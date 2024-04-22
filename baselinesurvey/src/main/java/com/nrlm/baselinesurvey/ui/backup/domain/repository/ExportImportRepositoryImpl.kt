package com.nrlm.baselinesurvey.ui.backup.domain.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_EMAIL
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
        nudgeBaselineDatabase.activityTaskEntityDao().deleteActivityTask()
        nudgeBaselineDatabase.missionEntityDao().deleteMissions()
        nudgeBaselineDatabase.missionActivityEntityDao().deleteActivities()
        nudgeBaselineDatabase.optionItemDao().deleteOptions()
        nudgeBaselineDatabase.questionEntityDao().deleteAllQuestions()
        nudgeBaselineDatabase.sectionAnswerEntityDao().deleteAllSectionAnswer()
        nudgeBaselineDatabase.inputTypeQuestionAnswerDao().deleteAllInputTypeAnswers()
        nudgeBaselineDatabase.formQuestionResponseDao().deleteAllFormQuestions()
        nudgeBaselineDatabase.didiSectionProgressEntityDao().deleteAllSectionProgress()
        nudgeBaselineDatabase.villageListDao().deleteAllVilleges()
        nudgeBaselineDatabase.surveyEntityDao().deleteAllSurvey()
        nudgeBaselineDatabase.didiInfoEntityDao().deleteAllDidiInfo()
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