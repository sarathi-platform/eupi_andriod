package com.patsurvey.nudge.activities.survey

import com.google.gson.Gson
import com.nudge.core.enums.EventName
import com.nudge.core.eventswriter.entities.EventV1
import com.patsurvey.nudge.activities.settings.TransactionIdRequest
import com.patsurvey.nudge.activities.settings.TransactionIdResponseForPatStatus
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.dataModel.PATDidiStatusModel
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.model.request.PATSummarySaveRequest
import com.patsurvey.nudge.model.request.SaveMatchSummaryRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.SaveMatchSummaryResponse
import com.patsurvey.nudge.model.response.TransactionResponseModel
import com.patsurvey.nudge.model.response.WorkFlowResponse
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.FORM_C
import com.patsurvey.nudge.utils.FORM_D
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_FORM_PATH
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_
import com.patsurvey.nudge.utils.getFormSubPath
import com.patsurvey.nudge.utils.json
import javax.inject.Inject

class SurveySummaryRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val answerDao: AnswerDao,
    val numericAnswerDao: NumericAnswerDao,
    val questionDao: QuestionListDao,
    val villageListDao: VillageListDao,
    val apiService: ApiService,
    val questionListDao: QuestionListDao
):BaseRepository() {
    fun getAllDidisForVillage(villageId:Int):List<DidiEntity>{
     return didiDao.getAllDidisForVillage(villageId)
    }
    fun fetchVillageDetailsForLanguage(villageId:Int): VillageEntity {
       return villageListDao.fetchVillageDetailsForLanguage(villageId, prefRepo.getAppLanguageId() ?: 2) ?: villageListDao.getVillage(villageId)
    }

    fun getStepByOrder(orderNumber: Int): StepListEntity {
       return stepsListDao.getStepByOrder(orderNumber,prefRepo.getSelectedVillage().id)
    }

    fun getAllDidisForVillage():List<DidiEntity>{
       return didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
    }
    fun fetchNotAvailableDidis():Int{
       return didiDao.fetchNotAvailableDidis(prefRepo.getSelectedVillage().id)
    }
    fun fetchPATSurveyDidiList():List<PATDidiStatusModel>{
      return answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id)
    }

    fun getDidiScoreFromDb(didiId:Int):Double{
       return didiDao.getDidiScoreFromDb(didiId)
    }
    fun getAllNeedToPostQuesForDidi(didiId: Int):List<SectionAnswerEntity>{
       return answerDao.getAllNeedToPostQuesForDidi(didiId)
    }

    fun getQuestion(questionId:Int): QuestionEntity {
       return questionDao.getQuestion(questionId)
    }

    fun getSingleQueOptions(questionId: Int,didiId: Int):List<NumericAnswerEntity>{
       return numericAnswerDao.getSingleQueOptions(
            questionId,
            didiId
        )
    }

    fun getPassingScore():Int{
       return questionDao.getPassingScore()
    }

    fun updateNeedToPostPAT(needsToPostPAT: Boolean,didiId: Int,villageId: Int){
        didiDao.updateNeedToPostPAT(
            needsToPostPAT,
            didiId,
            villageId
        )
    }

    suspend fun savePATSurveyToServer(patSummarySaveRequest: List<PATSummarySaveRequest>): ApiResponseModel<List<TransactionResponseModel>> {
        NudgeLogger.d("SurveySummaryRepository","savePATSurveyToServer Request=>${patSummarySaveRequest.json()}")
        return apiService.savePATSurveyToServer(patSummarySaveRequest)
    }

    suspend fun updateDidiScore(scoreDidiList: List<EditDidiWealthRankingRequest>): ApiResponseModel<List<DidiEntity>>{
        NudgeLogger.d("SurveySummaryRepository","updateDidiScore Request=>${scoreDidiList.json()}")
       return apiService.updateDidiScore(scoreDidiList)
    }

    fun updateDidiTransactionId(id: Int, transactionId: String) {
        didiDao.updateDidiTransactionId(
            id,
            transactionId
        )
    }

    fun fetchPendingPatStatusDidi(needsToPostPAT: Boolean, transactionId: String?): List<DidiEntity>{
       return didiDao.fetchPendingPatStatusDidi(needsToPostPAT, transactionId)
    }
    suspend fun getPendingStatusForPat(transactionIdRequest: TransactionIdRequest):ApiResponseModel<List<TransactionIdResponseForPatStatus>>{
       return apiService.getPendingStatusForPat(transactionIdRequest)
    }

    fun updateDidiNeedToPostPat(didiId: Int,needsToPostPAT: Boolean){
        didiDao.updateDidiNeedToPostPat(didiId = didiId, needsToPostPAT)
    }

    fun getStepForVillage(villageId: Int,stepId: Int): StepListEntity{
      return stepsListDao.getStepForVillage(villageId, stepId)
    }

    fun getAllStepsForVillage(villageId: Int):List<StepListEntity>{
        return stepsListDao.getAllStepsForVillage(villageId)
    }

   suspend fun editWorkFlow(addWorkFlowRequest: List<EditWorkFlowRequest>):ApiResponseModel<List<WorkFlowResponse>>{
       NudgeLogger.d("SurveySummaryRepository","editWorkFlow Request=> ${Gson().toJson(addWorkFlowRequest)}")
        return apiService.editWorkFlow(
            addWorkFlowRequest
        )
    }

    fun updateWorkflowId(stepId: Int, workflowId: Int,villageId:Int,status:String){
        stepsListDao.updateWorkflowId(
            stepId = stepId,
            workflowId = workflowId,
            villageId = villageId,
            status = status
        )
    }

    fun updateNeedToPost(id:Int, villageId: Int, needsToPost: Boolean){
        stepsListDao.updateNeedToPost(
            id = id,
            villageId = villageId,
            needsToPost = needsToPost
        )
    }

    fun getVillage(villageId: Int):VillageEntity{
        return villageListDao.getVillage(villageId)
    }

    fun markStepComplete(villageId: Int, stepId: Int, updatedCompletedStepsList: MutableList<Int>){
        villageListDao.updateLastCompleteStep(villageId, updatedCompletedStepsList)
        stepsListDao.markStepAsCompleteOrInProgress(
            stepId,
            StepStatus.COMPLETED.ordinal,
            villageId
        )
        stepsListDao.updateNeedToPost(stepId, villageId, true)
        val stepDetails = stepsListDao.getStepForVillage(villageId, stepId)
        if (stepDetails.orderNumber < stepsListDao.getAllSteps().size) {
            stepsListDao.markStepAsInProgress(
                (stepDetails.orderNumber + 1),
                StepStatus.INPROGRESS.ordinal,
                villageId
            )
            stepsListDao.updateNeedToPost(stepDetails.id, villageId, true)
            prefRepo.savePref("$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}", false)
            for (i in 1..5) {
                prefRepo.savePref(getFormPathKey(getFormSubPath(FORM_C, i)), "")
                prefRepo.savePref(getFormPathKey(getFormSubPath(FORM_D, i)), "")
            }
        }
    }

    fun getFormPathKey(subPath: String): String {
        return "${PREF_FORM_PATH}_${prefRepo.getSelectedVillage().id}_${subPath}"
    }

    fun markBPCStepComplete(stepId: Int, isComplete: Int = 0,villageId:Int){
        stepsListDao.markStepAsCompleteOrInProgress(
            stepId = stepId,
            isComplete = isComplete,
            villageId = villageId
        )
        villageListDao.updateStepAndStatusId(villageId,stepId,StepStatus.COMPLETED.ordinal)
    }

    fun updateBeneficiaryProcessStatus(didiId: Int, status: List<BeneficiaryProcessStatusModel>){
        didiDao.updateBeneficiaryProcessStatus(didiId, status)
    }
    fun getAllNeedToPostPATDidi(needsToPostPAT: Boolean): List<DidiEntity>{
       return didiDao.getAllNeedToPostPATDidi(needsToPostPAT, prefRepo.getSelectedVillage().id)
    }

    suspend fun updateDidiRanking(didiWealthRankingRequest: List<EditDidiWealthRankingRequest>): ApiResponseModel<List<DidiEntity>>{
        NudgeLogger.d("SurveySummaryRepository","updateDidiRanking Request=> ${Gson().toJson(didiWealthRankingRequest)}")
       return apiService.updateDidiRanking(didiWealthRankingRequest)
    }

    fun updateNeedsToPostBPCProcessStatus(needsToPostBPCProcessStatus: Boolean,didiId: Int){
        didiDao.updateNeedsToPostBPCProcessStatus(needsToPostBPCProcessStatus,didiId)
    }

    suspend fun saveMatchSummary(saveMatchSummaryRequest: ArrayList<SaveMatchSummaryRequest>): ApiResponseModel<ArrayList<SaveMatchSummaryResponse>> {
        NudgeLogger.d("SurveySummaryRepository","saveMatchSummary Request=> ${Gson().toJson(saveMatchSummaryRequest)}")
       return apiService.saveMatchSummary(saveMatchSummaryRequest)
    }

    fun fetchOptionYesCount(didiId: Int, type: String,actionType:String): Int{
       return answerDao.fetchOptionYesCount(
            didiId = didiId,
            type = type,
            actionType = actionType
        )
    }

    fun getAllInclusiveQues(didiId: Int): List<SectionAnswerEntity>{
       return answerDao.getAllInclusiveQues(didiId = didiId)
    }

    fun updateParticularDidiScore(score: Double,comment:String,isDidiAccepted:Boolean,didiId: Int){
        didiDao.updateDidiScore(
            score = score,
            comment = comment,
            didiId = didiId,
            isDidiAccepted = isDidiAccepted
        )
    }

    fun updateVOEndorsementDidiStatus(villageId: Int,didiId:Int, status: Int){
        didiDao.updateVOEndorsementDidiStatus(
            villageId,
            didiId,
            status
        )
    }

    fun getTotalWeightWithoutNumQues(didiId: Int):Double{
       return answerDao.getTotalWeightWithoutNumQues(didiId)
    }

    fun updatePatEditFlag(villageId: Int, patEdit: Boolean){
        didiDao.updatePatEditFlag(villageId, patEdit)
    }

    suspend fun writeBpcMatchScoreEvent(
        villageId: Int,
        passingScore: Int,
        bpcStep: StepListEntity,
        didiList: List<DidiEntity>
    ) {
        val event = EventV1(eventTopic = EventName.SAVE_BPC_MATCH_SCORE.topicName, payload = SaveMatchSummaryRequest.getSaveMatchSummaryRequestForBpc(
            villageId = villageId,
            stepListEntity = bpcStep,
            didiList = didiList,
            questionPassionScore = passingScore
        ).json(),
            mobileNumber = prefRepo.getMobileNumber() ?: BLANK_STRING
        )

        writeEventIntoLogFile(event)

    }

}