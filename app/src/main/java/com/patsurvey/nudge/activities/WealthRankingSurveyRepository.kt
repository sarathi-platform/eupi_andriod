package com.patsurvey.nudge.activities

import com.google.gson.Gson
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.activities.settings.TransactionIdResponse
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.WorkFlowResponse
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.StepStatus
import javax.inject.Inject

class WealthRankingSurveyRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val villageListDao: VillageListDao,
    val answerDao: AnswerDao,
    val numericAnswerDao: NumericAnswerDao,
    val questionDao: QuestionListDao,
    val apiService: ApiService
):BaseRepository() {

    fun getAllDidisForVillage(villageId:Int): List<DidiEntity>{
       return didiDao.getAllDidisForVillage(villageId)
    }

    fun getStepForVillage(villageId: Int,stepId:Int): StepListEntity {
       return stepsListDao.getStepForVillage(villageId, stepId)
    }

    fun getAllStepsForVillage(villageId:Int) : List<StepListEntity>{
      return stepsListDao.getAllStepsForVillage(villageId)
    }
    suspend fun editWorkFlow(addWorkFlowRequest: List<EditWorkFlowRequest>): ApiResponseModel<List<WorkFlowResponse>> {
        NudgeLogger.d("WealthRankingSurveyRepository","editWorkFlow Request=> ${Gson().toJson(addWorkFlowRequest)}")
        return apiService.editWorkFlow(addWorkFlowRequest)
    }
    fun updateWorkflowId(stepId: Int, workflowId: Int,villageId:Int,status:String){
        stepsListDao.updateWorkflowId(
            stepId,
            workflowId,
            villageId,
            status
        )
    }

    fun updateNeedToPost(stepId:Int, villageId: Int, needsToPost: Boolean){
        stepsListDao.updateNeedToPost(stepId,villageId, needsToPost)
    }

    fun getVillage(villageId: Int): VillageEntity {
       return villageListDao.getVillage(villageId)
    }
    fun updateLastCompleteStep(villageId: Int, stepId: List<Int>){
        villageListDao.updateLastCompleteStep(villageId, stepId)
    }

    fun markStepAsCompleteOrInProgress(stepId: Int, isComplete: Int = 0,villageId:Int){
        stepsListDao.markStepAsCompleteOrInProgress(
            stepId,
            isComplete,
            villageId
        )
    }

    fun getAllSteps(): List<StepListEntity>{
        return stepsListDao.getAllSteps()
    }

    fun markStepAsInProgress(orderNumber: Int, inProgress: Int = 1,villageId:Int){
        stepsListDao.markStepAsInProgress(
            orderNumber,
            inProgress,
            villageId
        )
    }
    fun isStepComplete(stepId: Int): Int{
        return stepsListDao.isStepComplete(stepId, prefRepo.getSelectedVillage().id)
    }
    fun getAllNeedToPostDidiRanking(needsToPostRanking: Boolean): List<DidiEntity>{
        return didiDao.getAllNeedToPostDidiRanking(needsToPostRanking, 0)
    }
    suspend fun updateDidiRanking(didiWealthRankingRequest: List<EditDidiWealthRankingRequest>): ApiResponseModel<List<DidiEntity>>{
        NudgeLogger.d("WealthRankingSurveyRepository","updateDidiRanking Request=> ${Gson().toJson(didiWealthRankingRequest)}")
        return apiService.updateDidiRanking(didiWealthRankingRequest)
    }

    fun setNeedToPostRanking(didiId:Int, needsToPostRanking: Boolean){
        didiDao.setNeedToPostRanking(didiId, needsToPostRanking)
    }

    fun updateDidiTransactionId(didiId: Int, transactionId: String){
        didiDao.updateDidiTransactionId(didiId,
            transactionId = transactionId
        )
    }

    fun fetchPendingWealthStatusDidi(needsToPostRanking: Boolean,transactionId : String?) : List<DidiEntity>{
       return didiDao.fetchPendingWealthStatusDidi(needsToPostRanking, transactionId)
    }

    suspend fun getPendingStatus(transactionIdRequest: TransactionIdRequest): ApiResponseModel<List<TransactionIdResponse>>{
        NudgeLogger.d("WealthRankingSurveyRepository","getPendingStatus Request=> ${Gson().toJson(transactionIdRequest)}")
        return apiService.getPendingStatus(transactionIdRequest)
    }

    fun updateRankEditFlag(villageId: Int, rankingEdit: Boolean){
        didiDao.updateRankEditFlag(villageId, rankingEdit = rankingEdit)
    }

    fun updateDidiNeedToPostWealthRank(didiId: Int, needsToPostRanking: Boolean){
        didiDao.updateDidiNeedToPostWealthRank(didiId, needsToPostRanking)
    }
}