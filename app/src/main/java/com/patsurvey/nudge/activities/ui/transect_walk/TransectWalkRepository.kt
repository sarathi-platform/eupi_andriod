package com.patsurvey.nudge.activities.ui.transect_walk

import com.google.gson.JsonArray
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.activities.settings.TransactionIdResponse
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
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.TolaApiResponse
import com.patsurvey.nudge.model.response.WorkFlowResponse
import javax.inject.Inject
import com.patsurvey.nudge.utils.*


class TransectWalkRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val didiDao: DidiDao,
    val villageListDao: VillageListDao
) : BaseRepository() {

    fun getSelectedVillage(): VillageEntity {
        return this.prefRepo.getSelectedVillage()
    }

    fun savePref(key: String, value: Int) {
        this.prefRepo.savePref(key, value)
    }


    fun updateLastSyncTime(lastSyncTime: String) {
        updateLastSyncTime(this.prefRepo, lastSyncTime)
    }

    fun getAppLanguageId(): Int? {
        return this.prefRepo.getAppLanguageId()
    }

    fun savePref(key: String, value: Boolean) {
        this.prefRepo.savePref(key, value)
    }

    fun savePref(key: String, value: String) {
        this.prefRepo.savePref(key, value)
    }

    fun savePref(key: String, value: Long) {
        this.prefRepo.savePref(key, value)
    }

    fun getPref(key: String, defaultValue: Long): Long {
        return this.prefRepo.getPref(key, defaultValue)
    }

    fun getTolaExist(name: String, villageId: Int): Int {
        return this.tolaDao.getTolaExist(name, villageId)
    }

    fun tolaInsert(tola: TolaEntity) {
        this.tolaDao.insert(tola)
    }

    fun getAllTolasForVillage(villageId: Int): List<TolaEntity> {
        return this.tolaDao.getAllTolasForVillage(villageId)
    }

    fun fetchTolaNeedToPost(
        needsToPost: Boolean,
        transactionId: String?,
        serverId: Int
    ): List<TolaEntity> {
        return this.tolaDao.fetchTolaNeedToPost(needsToPost, transactionId, serverId)
    }


    fun updateTolaDetailAfterSync(
        id: Int,
        serverId: Int,
        needsToPost: Boolean,
        transactionId: String,
        createdDate: Long,
        modifiedDate: Long
    ) {
        this.tolaDao.updateTolaDetailAfterSync(
            id,
            serverId,
            needsToPost,
            transactionId,
            createdDate,
            modifiedDate
        )
    }

    fun fetchAllTolaNeedToUpdate(
        needsToPost: Boolean,
        transactionId: String?,
        serverId: Int
    ): List<TolaEntity> {
        return this.tolaDao.fetchAllTolaNeedToUpdate(needsToPost, transactionId, serverId)
    }


    fun updateTolaTransactionId(id: Int, transactionId: String) {
        this.tolaDao.updateTolaTransactionId(id, transactionId)
    }

    fun fetchPendingTola(needsToPost: Boolean, transactionId: String?): List<TolaEntity> {
        return this.tolaDao.fetchPendingTola(needsToPost, transactionId)
    }

    fun fetchAllTolaNeedToDelete(status: Int): List<TolaEntity> {
        return this.tolaDao.fetchAllTolaNeedToDelete(status)
    }

    fun fetchAllPendingTolaNeedToDelete(status: Int, transactionId: String?): List<TolaEntity> {
        return this.tolaDao.fetchAllPendingTolaNeedToDelete(status, transactionId)
    }

    fun fetchAllPendingTolaNeedToUpdate(
        needsToPost: Boolean,
        transactionId: String?
    ): List<TolaEntity> {
        return this.tolaDao.fetchAllPendingTolaNeedToUpdate(needsToPost, transactionId)
    }

    fun deleteTola(id: Int) {
        this.tolaDao.deleteTola(id)
    }

    fun removeTola(id: Int) {
        this.tolaDao.removeTola(id)
    }


    fun setNeedToPost(ids: List<Int>, needsToPost: Boolean) {
        this.tolaDao.setNeedToPost(ids, needsToPost)
    }

    fun updateNeedToPost(id: Int, needsToPost: Boolean) {
        this.tolaDao.updateNeedToPost(id, needsToPost)
    }


    fun getTola(id: Int): TolaEntity {
        return this.tolaDao.getTola(id)
    }

    fun insertAll(tolas: List<TolaEntity>) {
        this.tolaDao.insertAll(tolas)
    }

    fun deleteTolaOffline(id: Int, status: Int) {
        this.tolaDao.deleteTolaOffline(id, status)
    }

    fun fetchSingleTola(id: Int): TolaEntity? {
        return this.tolaDao.fetchSingleTola(id)
    }

    fun getStepForVillage(villageId: Int, stepId: Int): StepListEntity {
        return this.stepsListDao.getStepForVillage(villageId, stepId)
    }

    fun updateWorkflowId(stepId: Int, workflowId: Int, villageId: Int, status: String) {
        this.stepsListDao.updateWorkflowId(stepId, workflowId, villageId, status)
    }

    fun updateWorkflowId(stepId: Int, workflowId: Int, status: String) {
        this.stepsListDao.updateWorkflowId(stepId, workflowId, status)
    }

    fun getAllCompleteStepsForVillage(villageId: Int): List<StepListEntity> {
        return this.stepsListDao.getAllCompleteStepsForVillage(villageId)
    }

    fun updateNeedToPost(id: Int, villageId: Int, needsToPost: Boolean) {
        this.stepsListDao.updateNeedToPost(id, villageId, needsToPost)
    }

    fun getAllStepsForVillage(villageId: Int): List<StepListEntity> {
        return this.stepsListDao.getAllStepsForVillage(villageId)
    }

    fun markStepAsCompleteOrInProgress(stepId: Int, isComplete: Int = 0, villageId: Int) {
        this.stepsListDao.markStepAsCompleteOrInProgress(stepId, isComplete, villageId)
    }

    fun markStepAsInProgress(orderNumber: Int, inProgress: Int = 1, villageId: Int) {
        this.stepsListDao.markStepAsInProgress(orderNumber, inProgress, villageId)
    }

    fun isStepComplete(id: Int, villageId: Int): Int {
        return this.stepsListDao.isStepComplete(id, villageId)
    }

    fun getDidisForTola(tolaId: Int): List<DidiEntity> {
        return this.didiDao.getDidisForTola(tolaId)
    }

    fun deleteDidisForTola(tolaId: Int, activeStatus: Int, needsToPostDeleteStatus: Boolean) {
        this.didiDao.deleteDidisForTola(tolaId, activeStatus, needsToPostDeleteStatus)
    }

    fun updateTolaName(id: Int, newName: String) {
        this.didiDao.updateTolaName(id, newName)
    }

    fun fetchVillageDetailsForLanguage(villageId: Int, languageId: Int): VillageEntity {
        return this.villageListDao.fetchVillageDetailsForLanguage(villageId, languageId)
    }

    fun getVillage(id: Int): VillageEntity {
        return this.villageListDao.getVillage(id)
    }

    fun updateLastCompleteStep(villageId: Int, stepId: List<Int>) {
        this.villageListDao.updateLastCompleteStep(villageId, stepId)
    }

    suspend fun addCohort(cohortList: JsonArray): ApiResponseModel<List<TolaApiResponse>> {
        return this.apiInterface.addCohort(cohortList)
    }

    suspend fun getPendingStatus(transactionIdRequest: TransactionIdRequest): ApiResponseModel<List<TransactionIdResponse>> {
        return this.apiInterface.getPendingStatus(transactionIdRequest)
    }

    suspend fun deleteCohort(deleteCohort: JsonArray): ApiResponseModel<List<TolaApiResponse?>> {
        return this.apiInterface.deleteCohort(deleteCohort)
    }

    suspend fun editCohort(updatedCohort: JsonArray): ApiResponseModel<List<TolaApiResponse>> {
        return this.apiInterface.editCohort(updatedCohort)
    }

    suspend fun deleteDidi(didiId: JsonArray): ApiResponseModel<List<DidiEntity>> {
        return this.apiInterface.deleteDidi(didiId)
    }

    suspend fun editWorkFlow(addWorkFlowRequest: List<EditWorkFlowRequest>): ApiResponseModel<List<WorkFlowResponse>> {
        return this.apiInterface.editWorkFlow(addWorkFlowRequest)
    }


}