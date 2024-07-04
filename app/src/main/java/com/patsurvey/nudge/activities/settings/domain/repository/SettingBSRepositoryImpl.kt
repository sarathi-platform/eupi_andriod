package com.patsurvey.nudge.activities.settings.domain.repository

import android.content.Context
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.LANGUAGE_OPEN_FROM_SETTING
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import com.nudge.core.preference.CoreSharedPrefs
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.NudgeDatabase
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.service.csv.ExportHelper
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.LAST_UPDATE_TIME
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_KEY_EMAIL
import com.patsurvey.nudge.utils.PREF_KEY_NAME
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.UPCM_USER

class SettingBSRepositoryImpl(
    private val prefRepo: PrefRepo,
    private val apiService: ApiService,
    private val didiDao: DidiDao,
    private val stepsListDao: StepsListDao,
    private val casteListDao: CasteListDao,
    private val  exportHelper: ExportHelper,
    private val nudgeDatabase: NudgeDatabase
): SettingBSRepository {

    override suspend fun performLogout(): ApiResponseModel<String> {
        return apiService.performLogout()
    }

    override fun clearSharedPref() {
        prefRepo.saveAccessToken(BLANK_STRING)
        prefRepo.saveSettingOpenFrom(PageFrom.HOME_PAGE.ordinal)
        if(prefRepo.getPref(PREF_KEY_TYPE_NAME, BLANK_STRING).equals(UPCM_USER)) {
            prefRepo.savePref(PREF_KEY_TYPE_NAME, BLANK_STRING)
            val coreSharedPrefs = CoreSharedPrefs.getInstance(BaselineCore.getAppContext())
            coreSharedPrefs.setBackupFileName(
                getDefaultBackUpFileName(
                    prefRepo.getMobileNumber() ?: BLANK_STRING,
                    prefRepo.getLoggedInUserType() ?: BLANK_STRING
                )
            )
            coreSharedPrefs.setImageBackupFileName(
                getDefaultImageBackUpFileName(
                    prefRepo.getMobileNumber() ?: "", prefRepo.getLoggedInUserType() ?: BLANK_STRING
                )
            )
            coreSharedPrefs.setFileExported(false)
        }
        prefRepo.setPreviousUserMobile(prefRepo.getMobileNumber() ?: BLANK_STRING)
        prefRepo.savePref(PREF_KEY_TYPE_NAME, BLANK_STRING)
    }

    override fun saveLanguageScreenOpenFrom() {
        prefRepo.savePref(LANGUAGE_OPEN_FROM_SETTING,true)
    }

    override fun clearLocalData() {
        TODO("Not yet implemented")
    }

    override fun getUserType(): String? {
        return prefRepo.getPref(PREF_KEY_TYPE_NAME, BLANK_STRING)
    }

    override fun getVillageId(): Int {
        return prefRepo.getSelectedVillage().id
    }

    override fun getSettingOpenFrom(): Int {
        return prefRepo.settingOpenFrom()
    }

    override suspend fun getAllPoorDidiForVillage(villageId:Int): List<DidiEntity> {
        return didiDao.getAllPoorDidisForVillage(villageId = villageId)
    }

    override suspend fun getAllDidiForVillage(villageId: Int): List<DidiEntity> {
        return didiDao.getAllDidisForVillage(villageId)
    }

    override suspend fun getAllStepsForVillage(villageId: Int): List<StepListEntity> {
       return stepsListDao.getAllStepsForVillage(villageId)
    }

    override suspend fun exportAllFiles(context: Context) {
        exportHelper.exportAllData(context)
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

    override fun getAllCasteForLanguage(languageId: Int): List<CasteEntity> {
        return casteListDao.getAllCasteForLanguage(languageId)
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
           prefRepo.savePref(LAST_UPDATE_TIME, 0L)
       }catch (ex:Exception){
           NudgeLogger.d("SettingBSRepositoryImpl","clearSelectionLocalDB: ${ex.message}")
       }
    }
}