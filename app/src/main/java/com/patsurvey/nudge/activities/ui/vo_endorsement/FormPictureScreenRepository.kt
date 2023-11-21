package com.patsurvey.nudge.activities.ui.vo_endorsement

import com.google.gson.Gson
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.database.dao.PoorDidiListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.request.AddWorkFlowRequest
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.model.response.WorkFlowResponse
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.sql.RowId
import java.util.Collections
import javax.inject.Inject

class FormPictureScreenRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val villageListDao: VillageListDao,
    val stepsListDao: StepsListDao,
    val apiService: ApiService,
    val poorDidiListDao: PoorDidiListDao
):BaseRepository(){
    fun fetchVillageForLanguage(villageId:Int): VillageEntity {
        return villageListDao.fetchVillageDetailsForLanguage(villageId, prefRepo.getAppLanguageId() ?: 2) ?: villageListDao.getVillage(villageId)
    }

    fun getVillage(villageId: Int):VillageEntity{
       return villageListDao.getVillage(villageId)
    }

    fun markVOEndorsementStatusComplete(
        villageId: Int,
        stepId: Int,
        updatedCompletedStepsList: MutableList<Int>
    ){
        villageListDao.updateLastCompleteStep(villageId, updatedCompletedStepsList)
        stepsListDao.markStepAsCompleteOrInProgress(stepId, StepStatus.COMPLETED.ordinal,villageId)
        stepsListDao.updateNeedToPost(stepId, villageId, true)
        val stepDetails=stepsListDao.getStepForVillage(villageId, stepId)
        if(stepDetails.orderNumber<stepsListDao.getAllSteps().size){
            stepsListDao.markStepAsInProgress((stepDetails.orderNumber+1),
                StepStatus.INPROGRESS.ordinal,villageId)
            stepsListDao.updateNeedToPost(stepDetails.id, villageId, true)
        }
        prefRepo.savePref("$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}", true)
    }

    fun getAllDidisForVillage(): List<DidiEntity>{
       return didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
    }

    fun updateBeneficiaryProcessStatus(didiId: Int, status: List<BeneficiaryProcessStatusModel>){
        didiDao.updateBeneficiaryProcessStatus(didiId, status)
    }

    fun updateNeedToPostVO(needsToPostVo: Boolean,didiId: Int,villageId: Int){
        didiDao.updateNeedToPostVO(needsToPostVo, didiId ,villageId)
    }

    fun getAllNeedToPostVoDidi(needsToPostVo: Boolean): List<DidiEntity>{
       return didiDao.getAllNeedToPostVoDidi(
            needsToPostVo = needsToPostVo,
            villageId = prefRepo.getSelectedVillage().id
        )
    }

    suspend fun updateDidiRanking(didiWealthRankingRequest: List<EditDidiWealthRankingRequest>): ApiResponseModel<List<DidiEntity>> {
        NudgeLogger.d("FormPictureScreenRepository","updateDidiRanking Request=> ${Gson().toJson(didiWealthRankingRequest)}")
        return apiService.updateDidiRanking(
            didiWealthRankingRequest
        )
    }

    fun getStepForVillage(villageId: Int,stepId: Int): StepListEntity {
        return stepsListDao.getStepForVillage(villageId, stepId)
    }

    fun getAllStepsForVillage(villageId:Int):List<StepListEntity>{
       return stepsListDao.getAllStepsForVillage(villageId)
    }

    suspend fun editWorkFlow(addWorkFlowRequest: List<EditWorkFlowRequest>):ApiResponseModel<List<WorkFlowResponse>>{
        NudgeLogger.d("FormPictureScreenRepository","editWorkFlow Request=> ${Gson().toJson(addWorkFlowRequest)}")
       return apiService.editWorkFlow(
            addWorkFlowRequest
        )
    }

    fun updateWorkflowId(stepId: Int, workflowId: Int,villageId: Int,status:String){
        stepsListDao.updateWorkflowId(
            stepId = stepId,
            workflowId = workflowId,
            villageId=villageId,
            status = status
        )
    }

    fun updateNeedToPost(stepId:Int, villageId: Int, needsToPost: Boolean){
        stepsListDao.updateNeedToPost(stepId, villageId, needsToPost)
    }

    suspend fun addWorkFlow(addWorkFlowRequest: List<AddWorkFlowRequest>):ApiResponseModel<List<WorkFlowResponse>> {
        NudgeLogger.d("FormPictureScreenRepository","addWorkFlow Request=> ${
            Gson().toJson(
                Collections.unmodifiableList(addWorkFlowRequest))}")
        return apiService.addWorkFlow(addWorkFlowRequest)
    }

    suspend fun uploadDocument( formList:List<MultipartBody.Part>,villageId: RequestBody,userType:RequestBody): ApiResponseModel<Object>{
        return apiService.uploadDocument(formList, villageId, userType)
    }

    fun updateVoEndorsementEditFlag( voEndorsementEdit: Boolean){
        didiDao.updateVoEndorsementEditFlag(prefRepo.getSelectedVillage().id, voEndorsementEdit)
    }

}