package com.patsurvey.nudge.activities.backup.domain.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_EMAIL
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nudge.core.preference.CoreSharedPrefs
import com.patsurvey.nudge.database.NudgeDatabase
import com.patsurvey.nudge.utils.CRP_USER_TYPE
import com.patsurvey.nudge.utils.LAST_UPDATE_TIME
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import javax.inject.Inject

class ExportImportRepositoryImpl @Inject constructor(
    val prefBSRepo: PrefBSRepo,
    val coreSharedPrefs: CoreSharedPrefs,
    val nudgeBaselineDatabase:NudgeBaselineDatabase,
    val nudgeDatabase: NudgeDatabase
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
            NudgeLogger.d("ExportImportRepositoryImpl","clearLocalData: ${ex.message}")
        }

    }

    override fun setAllDataSynced() {
        prefBSRepo.setDataSyncStatus(false)
        coreSharedPrefs.setDataLoaded(isDataLoaded = false)

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

    override fun getLoggedInUserType(): String {
        return prefBSRepo.getPref(PREF_KEY_TYPE_NAME, CRP_USER_TYPE) ?: CRP_USER_TYPE
    }

    override fun clearSelectionLocalDB() {
        try {
            nudgeDatabase.tolaDao().deleteAllTola()
            nudgeDatabase.didiDao().deleteAllDidi()
            nudgeDatabase.lastSelectedTola().deleteAllLastSelectedTola()
            nudgeDatabase.numericAnswerDao().deleteAllNumericAnswers()
            nudgeDatabase.answerDao().deleteAllAnswers()
            nudgeDatabase.questionListDao().deleteQuestionTable()
            nudgeDatabase.stepsListDao().deleteAllStepsFromDB()
            nudgeDatabase.villageListDao().deleteAllVilleges()
            nudgeDatabase.bpcSummaryDao().deleteAllSummary()
            nudgeDatabase.poorDidiListDao().deleteAllDidis()
            prefBSRepo.savePref(LAST_UPDATE_TIME, 0L)
        }catch (ex:Exception){
            NudgeLogger.d("ExportImportRepositoryImpl","clearSelectionLocalDB: ${ex.message}")
        }
    }


}