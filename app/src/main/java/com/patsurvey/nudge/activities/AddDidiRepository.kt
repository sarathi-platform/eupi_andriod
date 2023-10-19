package com.patsurvey.nudge.activities

import com.google.gson.JsonArray
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.activities.settings.TransactionIdResponse
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.LastTolaSelectedEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.LastSelectedTolaDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.EditDidiRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.DidiApiResponse
import com.patsurvey.nudge.model.response.WorkFlowResponse
import com.patsurvey.nudge.utils.updateLastSyncTime
import javax.inject.Inject


class AddDidiRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val casteListDao: CasteListDao,
    val tolaDao: TolaDao,
    val didiDao: DidiDao,
    val stepsListDao: StepsListDao,
    val villageListDao: VillageListDao,
    val lastSelectedTolaDao: LastSelectedTolaDao,
    val questionListDao: QuestionListDao,
    val answerDao: AnswerDao
) : BaseRepository() {
    fun getSelectedVillage(): VillageEntity {
        return this.prefRepo.getSelectedVillage()
    }

    fun getAppLanguageId(): Int? {
        return this.prefRepo.getAppLanguageId()
    }

    fun updateLastSyncTime(lastSyncTime: String) {
        updateLastSyncTime(this.prefRepo, lastSyncTime)
    }

    fun savePref(key: String, value: Int) {
        this.prefRepo.savePref(key, value)
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

    fun getPref(key: String, defaultValue: Int): Int {
        return this.prefRepo.getPref(key, defaultValue)
    }

    fun getPref(key: String, defaultValue: String): String? {
        return this.prefRepo.getPref(key, defaultValue)
    }

    fun getPref(key: String, defaultValue: Boolean): Boolean {
        return this.prefRepo.getPref(key, defaultValue)
    }

    fun getPref(key: String, defaultValue: Long): Long {
        return this.prefRepo.getPref(key, defaultValue)
    }

    fun getPref(key: String, defaultValue: Float): Float {
        return this.prefRepo.getPref(key, defaultValue)
    }

    fun getAllCasteForLanguage(languageId: Int): List<CasteEntity> {
        return this.casteListDao.getAllCasteForLanguage(languageId)
    }

    fun fetchSingleTola(id: Int): TolaEntity? {
        return this.tolaDao.fetchSingleTola(id)
    }

    fun getAllTolasForVillage(villageId: Int): List<TolaEntity> {
        return this.tolaDao.getAllTolasForVillage(villageId)
    }

    fun fetchSingleTolaFromServerId(id: Int): TolaEntity? {
        return this.tolaDao.fetchSingleTolaFromServerId(id)
    }


    fun getAllDidis(): List<DidiEntity> {
        return this.didiDao.getAllDidis()
    }

    fun getAllDidisForVillage(villageId: Int): List<DidiEntity> {
        return this.didiDao.getAllDidisForVillage(villageId)
    }

    fun getDidi(id: Int): DidiEntity {
        return this.didiDao.getDidi(id)
    }

    fun getDidiExist(
        name: String,
        address: String,
        guardianName: String,
        tolaId: Int,
        villageId: Int
    ): Int {
        return this.didiDao.getDidiExist(name, address, guardianName, tolaId, villageId)
    }

    fun insertDidi(didi: DidiEntity) {
        this.didiDao.insertDidi(didi)
    }

    fun updateDidi(didi: DidiEntity) {
        this.didiDao.updateDidi(didi)
    }

    fun deleteDidiTable() {
        this.didiDao.deleteDidiTable()
    }

    fun deleteDidiForVillage(villageId: Int) {
        this.didiDao.deleteDidiForVillage(villageId)
    }

    fun setNeedToPost(ids: List<Int>, needsToPost: Boolean) {
        this.didiDao.setNeedToPost(ids, needsToPost)
    }

    fun updateNeedToPost(id: Int, needsToPost: Boolean) {
        this.didiDao.updateNeedToPost(id, needsToPost)
    }

    fun updateDidiDetailAfterSync(
        id: Int,
        serverId: Int,
        needsToPost: Boolean,
        transactionId: String,
        createdDate: Long,
        modifiedDate: Long
    ) {
        this.didiDao.updateDidiDetailAfterSync(
            id,
            serverId,
            needsToPost,
            transactionId,
            createdDate,
            modifiedDate
        )
    }

    fun setNeedToPostRanking(id: Int, needsToPostRanking: Boolean) {
        this.didiDao.setNeedToPostRanking(id, needsToPostRanking)
    }

    fun setNeedToPostRankingServerId(id: Int, needsToPostRanking: Boolean) {
        this.didiDao.setNeedToPostRankingServerId(id, needsToPostRanking)
    }

    fun updateDidiRank(didiId: Int, rank: String) {
        this.didiDao.updateDidiRank(didiId, rank)
    }

    fun fetchPendingVOStatusStatusDidi(
        needsToPostVo: Boolean,
        transactionId: String?
    ): List<DidiEntity> {
        return this.didiDao.fetchPendingVOStatusStatusDidi(needsToPostVo, transactionId)
    }

    fun deleteAllDidi() {
        this.didiDao.deleteAllDidi()
    }

    fun fetchAllDidiNeedToAdd(
        needsToPost: Boolean,
        transactionId: String?,
        serverId: Int
    ): List<DidiEntity> {
        return this.didiDao.fetchAllDidiNeedToAdd(needsToPost, transactionId, serverId)
    }

    fun fetchAllDidiNeedToUpdate(
        needsToPost: Boolean,
        transactionId: String?,
        serverId: Int
    ): List<DidiEntity> {
        return this.didiDao.fetchAllDidiNeedToUpdate(needsToPost, transactionId, serverId)
    }

    fun fetchAllDidiNeedToDelete(status: Int): List<DidiEntity> {
        return this.didiDao.fetchAllDidiNeedToDelete(status)
    }

    fun fetchAllPendingDidiNeedToUpdate(
        needsToPost: Boolean,
        transactionId: String?,
        serverId: Int
    ): List<DidiEntity> {
        return didiDao.fetchAllPendingDidiNeedToUpdate(needsToPost, transactionId, serverId)
    }

    fun fetchAllPendingDidiNeedToDelete(
        status: Int,
        transactionId: String?,
        serverId: Int
    ): List<DidiEntity> {
        return this.didiDao.fetchAllPendingDidiNeedToDelete(status, transactionId, serverId)
    }

    fun deleteDidi(id: Int) {
        this.didiDao.deleteDidi(id)
    }

    fun updateDidiScore(score: Double, comment: String, isDidiAccepted: Boolean, didiId: Int) {
        this.didiDao.updateDidiScore(score, comment, isDidiAccepted, didiId)
    }

    fun fetchVOEndorseStatusDidi(villageId: Int): List<DidiEntity> {
        return this.didiDao.fetchVOEndorseStatusDidi(villageId)
    }

    fun updateModifiedDate(localModifiedDate: Long, didiId: Int) {
        this.didiDao.updateModifiedDate(localModifiedDate, didiId)
    }

    fun updateModifiedDateServerId(localModifiedDate: Long, didiId: Int) {
        this.didiDao.updateModifiedDateServerId(localModifiedDate, didiId)
    }

    fun getAllNeedToPostVoDidi(needsToPostVo: Boolean, villageId: Int): List<DidiEntity> {
        return this.didiDao.getAllNeedToPostVoDidi(needsToPostVo, villageId)
    }

    fun updateExclusionStatus(didiId: Int, patExclusionStatus: Int, crpComment: String) {
        this.didiDao.updateExclusionStatus(didiId, patExclusionStatus, crpComment)
    }

    fun updateImageLocalPath(didiId: Int, localPath: String) {
        this.didiDao.updateImageLocalPath(didiId, localPath)
    }

    fun updateNeedsToPostImage(id: Int, needsToPostImage: Boolean) {
        this.didiDao.updateNeedsToPostImage(id, needsToPostImage)
    }

    fun fetchAllDidiNeedsToPostImage(needsToPostImage: Boolean): List<DidiEntity> {
        return this.didiDao.fetchAllDidiNeedsToPostImage(needsToPostImage)
    }

    fun updateRankEditFlag(villageId: Int, rankingEdit: Boolean) {
        this.didiDao.updateRankEditFlag(villageId, rankingEdit)
    }

    fun updatePatEditFlag(villageId: Int, patEdit: Boolean) {
        this.didiDao.updatePatEditFlag(villageId, patEdit)
    }

    fun updateVoEndorsementEditFlag(villageId: Int, voEndorsementEdit: Boolean) {
        this.didiDao.updateVoEndorsementEditFlag(villageId, voEndorsementEdit)
    }

    fun getDidiScoreFromDb(didiId: Int): Double {
        return this.didiDao.getDidiScoreFromDb(didiId)
    }

    fun fetchAllDidiNeedsToPostImageWithLimit(needsToPostImage: Boolean): List<DidiEntity> {
        return this.didiDao.fetchAllDidiNeedsToPostImageWithLimit(needsToPostImage)
    }

    fun updatePATEditStatus(didiId: Int, patEdit: Boolean) {
        this.didiDao.updatePATEditStatus(didiId, patEdit)
    }

    fun updateDidiAbleBodiedStatus(didiId: Int, ableBodiedFlag: Int) {
        this.didiDao.updateDidiAbleBodiedStatus(didiId, ableBodiedFlag)
    }

    fun fetchPendingVerificationDidiCount(villageId: Int): Int {
        return this.didiDao.fetchPendingVerificationDidiCount(villageId)
    }

    fun deleteDidisForTola(tolaId: Int, activeStatus: Int, needsToPostDeleteStatus: Boolean) {
        this.didiDao.deleteDidisForTola(tolaId, activeStatus, needsToPostDeleteStatus)
    }

    fun updateBeneficiaryProcessStatus(didiId: Int, status: List<BeneficiaryProcessStatusModel>) {
        this.didiDao.updateBeneficiaryProcessStatus(didiId, status)
    }

    fun updateBeneficiaryProcessStatusServerId(
        didiId: Int,
        status: List<BeneficiaryProcessStatusModel>
    ) {
        this.didiDao.updateBeneficiaryProcessStatusServerId(didiId, status)
    }


    fun updateQuesSectionStatus(didiId: Int, patSurveyProgress: Int) {
        this.didiDao.updateQuesSectionStatus(didiId, patSurveyProgress)
    }

    fun getDidisForTola(tolaId: Int): List<DidiEntity> {
        return this.didiDao.getDidisForTola(tolaId)
    }

    fun deleteDidisForTola(tolaId: Int) {
        this.didiDao.deleteDidisForTola(tolaId)
    }

    fun getAllPendingPATDidisCount(villageId: Int): Int {
        return this.didiDao.getAllPendingPATDidisCount(villageId)
    }

    fun updateDidiTransactionId(id: Int, transactionId: String) {
        this.didiDao.updateDidiTransactionId(id, transactionId)
    }

    fun fetchLastDidiDetails(): DidiEntity {
        return this.didiDao.fetchLastDidiDetails()
    }

    fun fetchPendingDidi(needsToPost: Boolean, transactionId: String?): List<DidiEntity> {
        return this.didiDao.fetchPendingDidi(needsToPost, transactionId)
    }

    fun updateNeedToPostPAT(needsToPostPAT: Boolean, didiId: Int, villageId: Int) {
        this.didiDao.updateNeedToPostPAT(needsToPostPAT, didiId, villageId)
    }

    fun deleteDidiOffline(id: Int, activeStatus: Int, needsToPostDeleteStatus: Boolean) {
        this.didiDao.deleteDidiOffline(id, activeStatus, needsToPostDeleteStatus)
    }

    fun getDidisToBeDeleted(villageId: Int, needsToPostDeleteStatus: Boolean): List<DidiEntity> {
        return this.didiDao.getDidisToBeDeleted(villageId, needsToPostDeleteStatus)
    }

    fun updateDeletedDidiNeedToPostStatus(id: Int, needsToPostDeleteStatus: Boolean) {
        this.didiDao.updateDeletedDidiNeedToPostStatus(id, needsToPostDeleteStatus)
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

    fun getAllQuestionsForLanguage(languageId: Int): List<QuestionEntity> {
        return this.questionListDao.getAllQuestionsForLanguage(languageId)
    }


    fun getAnswerForDidi(actionType: String, didiId: Int): List<SectionAnswerEntity> {
        return this.answerDao.getAnswerForDidi(actionType, didiId)
    }

    fun fetchOptionYesCount(didiId: Int, type: String, actionType: String): Int {
        return this.answerDao.fetchOptionYesCount(didiId, type, actionType)
    }

    suspend fun deleteDidi(didiId: JsonArray): ApiResponseModel<List<DidiEntity>> {
        return this.apiInterface.deleteDidi(didiId)
    }

    suspend fun getPendingStatus(transactionIdRequest: TransactionIdRequest): ApiResponseModel<List<TransactionIdResponse>> {
        return this.apiInterface.getPendingStatus(transactionIdRequest)
    }

    suspend fun updateDidis(didiWealthRankingRequest: List<EditDidiRequest>): ApiResponseModel<List<DidiEntity>> {
        return this.apiInterface.updateDidis(didiWealthRankingRequest)
    }

    suspend fun addDidis(didiList: JsonArray): ApiResponseModel<List<DidiApiResponse>> {
        return this.apiInterface.addDidis(didiList)
    }

    suspend fun editWorkFlow(addWorkFlowRequest: List<EditWorkFlowRequest>): ApiResponseModel<List<WorkFlowResponse>> {
        return this.apiInterface.editWorkFlow(addWorkFlowRequest)
    }


    fun getAllSteps(): List<StepListEntity> {
        return this.stepsListDao.getAllSteps()
    }

    fun insert(step: StepListEntity) {
        this.stepsListDao.insert(step)
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


    fun getAllStepsForVillage(villageId: Int): List<StepListEntity> {
        return this.stepsListDao.getAllStepsForVillage(villageId)
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

    fun getTolaCountForVillage(villageId: Int): Long {
        return this.lastSelectedTolaDao.getTolaCountForVillage(villageId)
    }

    fun getTolaForVillage(villageId: Int): LastTolaSelectedEntity {
        return this.lastSelectedTolaDao.getTolaForVillage(villageId)
    }

    fun updateSelectedTola(tolaId: Int, tolaName: String, villageId: Int) {
        this.lastSelectedTolaDao.updateSelectedTola(tolaId, tolaName, villageId)
    }

    fun insertSelectedTola(caste: LastTolaSelectedEntity) {
        this.lastSelectedTolaDao.insertSelectedTola(caste)
    }

    fun saveStepId(stepId: Int) {
        this.prefRepo.saveStepId(stepId)
    }

    fun getFromPage(): String {
        return this.prefRepo.getFromPage()
    }

    fun saveSummaryScreenOpenFrom(openFrom: Int) {
        this.prefRepo.saveSummaryScreenOpenFrom(openFrom)
    }

    fun saveQuestionScreenOpenFrom(openFrom: Int) {
        this.prefRepo.saveQuestionScreenOpenFrom(openFrom)
    }


}