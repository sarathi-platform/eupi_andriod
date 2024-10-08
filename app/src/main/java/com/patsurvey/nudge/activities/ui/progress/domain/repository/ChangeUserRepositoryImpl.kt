package com.patsurvey.nudge.activities.ui.progress.domain.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.syncmanager.database.SyncManagerDatabase
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.LastSelectedTolaDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.PoorDidiListDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.UserDao
import com.patsurvey.nudge.database.dao.VillageListDao
import javax.inject.Inject

class ChangeUserRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val casteListDao: CasteListDao,
    private val didiDao: DidiDao,
    private val stepsListDao: StepsListDao,
    private val tolaDao: TolaDao,
    private val lastSelectedTolaDao: LastSelectedTolaDao,
    private val numericAnswerDao: NumericAnswerDao,
    private val answerDao: AnswerDao,
    private val questionListDao: QuestionListDao,
    private val userDao: UserDao,
    private val villageListDao: VillageListDao,
    private val bpcSummaryDao: BpcSummaryDao,
    private val poorDidiListDao: PoorDidiListDao,
    private val syncManagerDatabase: SyncManagerDatabase,
) : ChangeUserRepository {

    override suspend fun clearDbForUser() {
        casteListDao.deleteCasteTable()
        tolaDao.deleteAllTola()
        didiDao.deleteAllDidi()
        lastSelectedTolaDao.deleteAllLastSelectedTola()
        numericAnswerDao.deleteAllNumericAnswers()
        answerDao.deleteAllAnswers()
        questionListDao.deleteQuestionTable()
        stepsListDao.deleteAllStepsFromDB()
        userDao.deleteAllUserDetail()
        villageListDao.deleteAllVilleges()
        bpcSummaryDao.deleteAllSummary()
        poorDidiListDao.deleteAllDidis()
        syncManagerDatabase.eventsDao().deleteAllEvents()
        syncManagerDatabase.eventsDependencyDao().deleteAllDependentEvents()
    }

    override suspend fun clearPrefsForUser() {
        val languageId = coreSharedPrefs.getAppLanguageId()
        val language = coreSharedPrefs.getAppLanguage()
        val accessToken = coreSharedPrefs.getAccessToken()
        val mobileNo = coreSharedPrefs.getMobileNo()
        coreSharedPrefs.clearSharedPreference()
        coreSharedPrefs.saveAppLanguage(language)
        coreSharedPrefs.saveAppLanguageId(languageId)
        coreSharedPrefs.saveAccessToken(accessToken ?: "")
        coreSharedPrefs.saveMobileNumber(mobileNo)
    }

}