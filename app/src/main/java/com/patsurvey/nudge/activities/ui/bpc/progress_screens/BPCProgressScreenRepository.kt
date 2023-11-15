package com.patsurvey.nudge.activities.ui.bpc.progress_screens

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.BpcSummaryEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.AddWorkFlowRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.DidiApiResponse
import com.patsurvey.nudge.model.response.WorkFlowResponse
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.NudgeLogger
import javax.inject.Inject

class BPCProgressScreenRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val apiService: ApiService,
    val villageListDao: VillageListDao,
    val stepsListDao: StepsListDao,
    val bpcSummaryDao: BpcSummaryDao,
    val questionListDao: QuestionListDao,
    val answerDao: AnswerDao
) :BaseRepository() {

    fun getBpcSummaryForVillage(villageId: Int): BpcSummaryEntity?{
        return bpcSummaryDao.getBpcSummaryForVillage(villageId = villageId)
    }

    fun getAllVillages(): List<VillageEntity>{
        return villageListDao.getAllVillages(prefRepo.getAppLanguageId()?:2)
    }
    fun getAllStepsForVillage(villageId: Int):List<StepListEntity>{
        return stepsListDao.getAllStepsForVillage(villageId)
    }

    suspend fun addWorkFlow(addWorkFlowRequest: List<AddWorkFlowRequest>):ApiResponseModel<List<WorkFlowResponse>> {
        NudgeLogger.d("BPCProgressScreenRepository","addWorkFlow Request=>${Gson().toJson(addWorkFlowRequest)}")
        return apiInterface.addWorkFlow(addWorkFlowRequest)
    }

    fun updateWorkflowId(stepId: Int, workflowId: Int,villageId:Int,status:String){
        stepsListDao.updateWorkflowId(stepId, workflowId, villageId, status)
    }

    fun getAllDidisForVillage(): List<DidiEntity>{
       return didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
    }

    fun getAllInclusiveQues(didiId:Int): List<SectionAnswerEntity> {
       return answerDao.getAllInclusiveQues(didiId = didiId)
    }

    fun getTotalWeightWithoutNumQues(didiId :Int) :Double{
       return answerDao.getTotalWeightWithoutNumQues(didiId)
    }
    fun getQuestion(questionId:Int): QuestionEntity {
        return questionListDao.getQuestion(questionId)
    }

    fun updateVOEndorsementDidiStatus(didiId:Int, status: Int){
        didiDao.updateVOEndorsementDidiStatus(
            prefRepo.getSelectedVillage().id,
            didiId,
            status
        )
    }

    fun updateDidiScore(score: Double,comment:String,isDidiAccepted:Boolean,didiId: Int){
        didiDao.updateDidiScore(
            score = score,
            comment = comment,
            didiId = didiId,
            isDidiAccepted = isDidiAccepted
        )
    }

    fun updateModifiedDateServerId(didiId: Int){
        didiDao.updateModifiedDateServerId(System.currentTimeMillis(), didiId)
    }
}