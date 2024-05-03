package com.patsurvey.nudge.activities.backup.domain.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_EMAIL
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nrlm.baselinesurvey.utils.BaselineLogger
import javax.inject.Inject

class ExportImportRepositoryImpl @Inject constructor(
    val prefBSRepo: PrefBSRepo,
    val nudgeBaselineDatabase:NudgeBaselineDatabase
):ExportImportRepository {
    override fun clearLocalData() {
        try {

            nudgeBaselineDatabase.contentEntityDao().deleteContent()
            nudgeBaselineDatabase.didiDao().deleteSurveyees(prefBSRepo.getUniqueUserIdentifier())
            nudgeBaselineDatabase.activityTaskEntityDao().deleteActivityTask(prefBSRepo.getUniqueUserIdentifier())
            nudgeBaselineDatabase.missionEntityDao().deleteMissions(prefBSRepo.getUniqueUserIdentifier())
            nudgeBaselineDatabase.missionActivityEntityDao().deleteActivities(prefBSRepo.getUniqueUserIdentifier())
            nudgeBaselineDatabase.optionItemDao().deleteOptions(prefBSRepo.getUniqueUserIdentifier())
            nudgeBaselineDatabase.questionEntityDao().deleteAllQuestions(prefBSRepo.getUniqueUserIdentifier())
            nudgeBaselineDatabase.sectionAnswerEntityDao().deleteAllSectionAnswer(prefBSRepo.getUniqueUserIdentifier())
            nudgeBaselineDatabase.inputTypeQuestionAnswerDao().deleteAllInputTypeAnswers(prefBSRepo.getUniqueUserIdentifier())
            nudgeBaselineDatabase.formQuestionResponseDao().deleteAllFormQuestions(prefBSRepo.getUniqueUserIdentifier())
            nudgeBaselineDatabase.didiSectionProgressEntityDao().deleteAllSectionProgress(prefBSRepo.getUniqueUserIdentifier())
            nudgeBaselineDatabase.villageListDao().deleteAllVilleges()
            nudgeBaselineDatabase.surveyEntityDao().deleteAllSurvey(prefBSRepo.getUniqueUserIdentifier())
            nudgeBaselineDatabase.didiInfoEntityDao().deleteAllDidiInfo(prefBSRepo.getUniqueUserIdentifier())
        }catch (ex:Exception){
            ex.printStackTrace()
            BaselineLogger.d("ExportImportRepositoryImpl","clearLocalData: ${ex.message}")
        }

    }

    override fun setAllDataSynced() {
        prefBSRepo.setDataSyncStatus(false)
    }

    override fun getUserMobileNumber(): String {
        return prefBSRepo.getMobileNumber()?: BLANK_STRING
    }

    override fun getUserID(): String {
        return prefBSRepo.getUserId()
    }

    override fun getUserEmail(): String {
        return prefBSRepo.getPref(PREF_KEY_EMAIL, BLANK_STRING)?: BLANK_STRING
    }

    override fun getUserName(): String {
        return prefBSRepo.getPref(PREF_KEY_NAME, BLANK_STRING) ?: BLANK_STRING
    }


}