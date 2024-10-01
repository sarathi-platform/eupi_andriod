package com.patsurvey.nudge.activities.backup.domain.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_EMAIL
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.syncmanager.database.SyncManagerDatabase
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
    val nudgeDatabase: NudgeDatabase,
    val syncManagerDatabase: SyncManagerDatabase
):ExportImportRepository {
    override fun clearLocalData() {
        try {
            val userId = prefBSRepo.getUniqueUserIdentifier()

            nudgeBaselineDatabase.apply {
                contentEntityDao().deleteContent()
                didiDao().deleteSurveyees(userId)
                activityTaskEntityDao().deleteActivityTask(userId)
                missionEntityDao().deleteMissions(userId)
                missionActivityEntityDao().deleteActivities(userId)
                optionItemDao().deleteOptions(userId)
                questionEntityDao().deleteAllQuestions(userId)
                sectionAnswerEntityDao().deleteAllSectionAnswer(userId)
                inputTypeQuestionAnswerDao().deleteAllInputTypeAnswers(userId)
                formQuestionResponseDao().deleteAllFormQuestions(userId)
                didiSectionProgressEntityDao().deleteAllSectionProgress(userId)
                villageListDao().deleteAllVilleges()
                surveyEntityDao().deleteAllSurvey(userId)
                didiInfoEntityDao().deleteAllDidiInfo(userId)

            }

        }catch (ex:Exception){
            NudgeLogger.d("ExportImportRepositoryImpl","clearLocalData: ${ex.message}")
        }

    }

    override fun setAllDataSynced() {
        prefBSRepo.setDataSyncStatus(false)
        coreSharedPrefs.setDataLoaded(isDataLoaded = false)
        coreSharedPrefs.setDidiTabDataLoaded(false)
        coreSharedPrefs.setDataTabDataLoaded(false)

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

    override fun clearAPIStatusTableData() {
        try {
            syncManagerDatabase.apply {
                apiStatusDao().deleteApiStatus()
            }
        } catch (ex: Exception) {
            NudgeLogger.d("ExportImportRepositoryImpl", "clearAPIStatusTableData: ${ex.message}")

        }
    }


}