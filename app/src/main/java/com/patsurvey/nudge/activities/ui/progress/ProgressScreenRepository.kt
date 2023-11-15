package com.patsurvey.nudge.activities.ui.progress

import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.AddWorkFlowRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.WorkFlowResponse
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.updateLastSyncTime
import javax.inject.Inject


class ProgressScreenRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val stepsListDao: StepsListDao,
    val villageListDao: VillageListDao,
    val tolaDao: TolaDao
) : BaseRepository() {

    fun getAccessToken(): String? {
        return prefRepo.getAccessToken()
    }

    fun getSelectedVillage(): VillageEntity {
        return prefRepo.getSelectedVillage()
    }

    fun getAppLanguageId(): Int? {
        return prefRepo.getAppLanguageId()
    }

    fun saveSelectedVillage(village: VillageEntity) {
        prefRepo.saveSelectedVillage(village)
    }

    fun updateLastSyncTime(lastSyncTime: String) {
        updateLastSyncTime(prefRepo, lastSyncTime)
    }

    fun getAllStepsForVillage(villageId: Int): List<StepListEntity> {
        return stepsListDao.getAllStepsForVillage(villageId)
    }

    fun fetchLastInProgressStep(villageId: Int, isComplete: Int): StepListEntity {
        return stepsListDao.fetchLastInProgressStep(villageId, isComplete)
    }

    fun markStepAsInProgress(orderNumber: Int, inProgress: Int = 1, villageId: Int) {
        stepsListDao.markStepAsInProgress(orderNumber, inProgress, villageId)
    }

    fun isStepCompleteLiveForCrp(id: Int, villageId: Int): LiveData<Int> {
        return stepsListDao.isStepCompleteLiveForCrp(id, villageId)
    }

    fun getStepForVillage(villageId: Int, stepId: Int): StepListEntity {
        return stepsListDao.getStepForVillage(villageId, stepId)
    }

    fun updateWorkflowId(stepId: Int, workflowId: Int, villageId: Int, status: String) {
        stepsListDao.updateWorkflowId(stepId, workflowId, villageId, status)
    }

    fun updateNeedToPost(id: Int, villageId: Int, needsToPost: Boolean) {
        stepsListDao.updateNeedToPost(id, villageId, needsToPost)
    }

    fun updateLastCompleteStep(villageId: Int, stepId: List<Int>) {
        villageListDao.updateLastCompleteStep(villageId, stepId)
    }

    fun getAllVillages(languageId: Int): List<VillageEntity> {
        return villageListDao.getAllVillages(languageId)
    }

    fun getAllTolasForVillage(villageId: Int): List<TolaEntity> {
        return tolaDao.getAllTolasForVillage(villageId)
    }

    fun getAllDidisForVillage(villageId: Int): List<DidiEntity> {
        return didiDao.getAllDidisForVillage(villageId)
    }

    suspend fun addWorkFlow(addWorkFlowRequest: List<AddWorkFlowRequest>): ApiResponseModel<List<WorkFlowResponse>> {
        NudgeLogger.d("ProgressScreenRepository","addWorkFlow Request=> ${Gson().toJson(addWorkFlowRequest)}")
        return apiInterface.addWorkFlow(addWorkFlowRequest)
    }

    fun isUserBPC(): Boolean {
        return prefRepo.isUserBPC()
    }

    fun savePref(key: String, value: String) {
        prefRepo.savePref(key, value)
    }

    fun savePref(key: String, value: Boolean) {
        prefRepo.savePref(key, value)
    }

    fun saveSettingOpenFrom(openFrom: Int) {
        prefRepo.saveSettingOpenFrom(openFrom)
    }

    fun getPref(key: String, defaultValue: String): String? {
        return prefRepo.getPref(
            key,
            defaultValue
        )
    }

    fun saveFromPage(pageFrom: String) {
        prefRepo.saveFromPage(pageFrom)
    }

}