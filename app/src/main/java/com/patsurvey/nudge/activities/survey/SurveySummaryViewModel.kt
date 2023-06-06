package com.patsurvey.nudge.activities.survey

import android.annotation.SuppressLint
import android.util.Log
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.converters.BeneficiaryProcessStatusModel
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.request.AnswerDetailDTOListItem
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.EditWorkFlowRequest
import com.patsurvey.nudge.model.request.PATSummarySaveRequest
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SurveySummaryViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val didiDao: DidiDao,
    val stepsListDao: StepsListDao,
    val answerDao: AnswerDao,
    val numericAnswerDao: NumericAnswerDao,
    val questionDao: QuestionListDao,
    val villageListDao: VillageListDao,
    val apiService: ApiService
): BaseViewModel() {

    private val _didiList = MutableStateFlow(listOf<DidiEntity>())
    val didiList: StateFlow<List<DidiEntity>> get() = _didiList

    init {
        fetchDidisFromDB()
    }

    fun fetchDidisFromDB(){
        job= CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                _didiList.emit(didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id))
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    @SuppressLint("SuspiciousIndentation")
    fun savePATSummeryToServer(networkCallbackListener: NetworkCallbackListener){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO){
                    var optionList= emptyList<OptionsItem>()
                    var answeredDidiList:ArrayList<PATSummarySaveRequest> = arrayListOf()
                    var surveyId =0

                    val didiIDList= answerDao.fetchPATSurveyDidiList(prefRepo.getSelectedVillage().id)
                    if(didiIDList.isNotEmpty()){
                        didiIDList.forEach { didi->
                            Log.d(TAG, "savePATSummeryToServer Save: ${didi.id} :: ${didi.patSurveyStatus}")
                            var qList:ArrayList<AnswerDetailDTOListItem> = arrayListOf()
                            val needToPostQuestionsList=answerDao.getAllNeedToPostQuesForDidi(didi.id)
                            if(needToPostQuestionsList.isNotEmpty()){
                                needToPostQuestionsList.forEach {
                                    surveyId= questionDao.getQuestion(it.questionId).surveyId?:0
                                    if(!it.type.equals(QuestionType.Numeric_Field.name,true)){
                                        optionList= listOf(
                                            OptionsItem(optionId = it.optionId,
                                                optionValue = it.optionValue,
                                                count = 0,
                                                summary = it.summary,
                                                display = it.answerValue,
                                                weight = 0,
                                                isSelected = false)
                                        )
                                    }else{
                                        val numOptionList=numericAnswerDao.getSingleQueOptions(it.questionId,it.didiId)
                                        val tList:ArrayList<OptionsItem> = arrayListOf()
                                        if(numOptionList.isNotEmpty()){
                                            numOptionList.forEach { numOption->
                                                tList.add(
                                                    OptionsItem(optionId = numOption.optionId,
                                                        optionValue = 0,
                                                        count = numOption.count,
                                                        summary = it.summary,
                                                        display = it.answerValue,
                                                        weight = numOption.weight,
                                                        isSelected = false)
                                                )
                                            }
                                            optionList=tList
                                        }

                                    }
                                    try {
                                        qList.add(
                                            AnswerDetailDTOListItem(
                                            questionId =it.questionId,
                                            section = it.actionType,
                                            options = optionList)
                                        )
                                    }catch (e:Exception){
                                        e.printStackTrace()
                                    }

                                }

                            }
                            answeredDidiList.add(
                                PATSummarySaveRequest(
                                    villageId= prefRepo.getSelectedVillage().id,
                                    surveyId=surveyId,
                                    beneficiaryId = didi.id,
                                    languageId = prefRepo.getAppLanguageId()?:0,
                                    stateId = prefRepo.getSelectedVillage().stateId,
                                    totalScore = 0,
                                    userType = USER_CRP,
                                    beneficiaryName= didi.name,
                                    answerDetailDTOList= qList,
                                    patSurveyStatus = didi.patSurveyStatus,
                                    section2Status = didi.section2Status,
                                    section1Status = didi.section1Status
                                )
                            )
                        }
                        if(answeredDidiList.isNotEmpty()){
                            withContext(Dispatchers.IO){
                                val saveAPIResponse= apiService.savePATSurveyToServer(answeredDidiList)
                                if(saveAPIResponse.status.equals(SUCCESS,true)){
                                    didiIDList.forEach { didiItem->
                                        didiDao.updateNeedToPostPAT(false,didiItem.id,prefRepo.getSelectedVillage().id)
                                    }
                                    networkCallbackListener.onSuccess()

                                } else {
                                    networkCallbackListener.onFailed()
                                }
                            }

                        }

                    }
                }

            }  catch (ex:Exception){
                networkCallbackListener.onFailed()
                ex.printStackTrace()
                onCatchError(ex)
            }
        }
    }

    fun callWorkFlowAPI(villageId: Int,stepId: Int, networkCallbackListener: NetworkCallbackListener){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val dbResponse=stepsListDao.getStepForVillage(villageId, stepId)
                if(dbResponse.workFlowId>0){
                    val response = apiService.editWorkFlow(
                        listOf(
                            EditWorkFlowRequest(dbResponse.workFlowId, StepStatus.COMPLETED.name)
                        ) )
                    withContext(Dispatchers.IO){
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                stepsListDao.updateWorkflowId(stepId,dbResponse.workFlowId,villageId,it[0].status)
                            }
                        }else{
                            networkCallbackListener.onFailed()
                            onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                        }
                    }
                }

            }catch (ex:Exception){
                networkCallbackListener.onFailed()
                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }

    fun markPatComplete(villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val existingList = villageListDao.getVillage(villageId).steps_completed
            val updatedCompletedStepsList = mutableListOf<Int>()
            if (!existingList.isNullOrEmpty()) {
                existingList.forEach {
                    updatedCompletedStepsList.add(it)
                }
            }
            updatedCompletedStepsList.add(stepId)
            villageListDao.updateLastCompleteStep(villageId, updatedCompletedStepsList)
            stepsListDao.markStepAsCompleteOrInProgress(stepId, StepStatus.COMPLETED.ordinal,villageId)
            val stepDetails=stepsListDao.getStepForVillage(villageId, stepId)
            if(stepDetails.orderNumber<stepsListDao.getAllSteps().size){
                stepsListDao.markStepAsInProgress((stepDetails.orderNumber+1),StepStatus.INPROGRESS.ordinal,villageId)
            }
        }
    }

    fun savePatCompletionDate() {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val date = dateFormat.format(currentTime)
        prefRepo.savePref(PREF_PAT_COMPLETION_DATE, date)
    }

    fun markVoEndorsementComplete(villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val existingList = villageListDao.getVillage(villageId).steps_completed
            val updatedCompletedStepsList = mutableListOf<Int>()
            if (!existingList.isNullOrEmpty()) {
                existingList.forEach {
                    updatedCompletedStepsList.add(it)
                }
            }
            updatedCompletedStepsList.add(stepId)
            villageListDao.updateLastCompleteStep(villageId, updatedCompletedStepsList)
            stepsListDao.markStepAsCompleteOrInProgress(stepId, StepStatus.COMPLETED.ordinal,villageId)
            val stepDetails=stepsListDao.getStepForVillage(villageId, stepId)
            if(stepDetails.orderNumber<stepsListDao.getAllSteps().size){
                stepsListDao.markStepAsInProgress((stepDetails.orderNumber+1),StepStatus.INPROGRESS.ordinal,villageId)
            }
            prefRepo.savePref("$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}", true)
        }
    }

    fun saveVoEndorsementDate() {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val date = dateFormat.format(currentTime)
        prefRepo.savePref(PREF_VO_ENDORSEMENT_COMPLETION_DATE, date)
    }

    fun updateDidiPatStatus() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _didiList.value.forEach {didi ->
                if (didi.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal) {
                    val existingProcessStatus = didi.beneficiaryProcessStatus
                    var updatedStatus = mutableListOf<BeneficiaryProcessStatusModel>()
                    existingProcessStatus?.forEach {
                        updatedStatus.add(it)
                    }
                    updatedStatus.add(BeneficiaryProcessStatusModel("PAT_SURVEY", "COMPLETED"))
                    didiDao.updateBeneficiaryProcessStatus(didi.id, updatedStatus)
                } else if (didi.patSurveyStatus == PatSurveyStatus.NOT_AVAILABLE.ordinal) {
                    val existingProcessStatus = didi.beneficiaryProcessStatus
                    var updatedStatus = mutableListOf<BeneficiaryProcessStatusModel>()
                    existingProcessStatus?.forEach {
                        updatedStatus.add(it)
                    }
                    updatedStatus.add(BeneficiaryProcessStatusModel("PAT_SURVEY", "NOT_AVAILABLE"))
                    didiDao.updateBeneficiaryProcessStatus(didi.id, updatedStatus)
                } else {
                    didiDao.updateNeedToPostPAT(false, didi.id, didi.villageId)
                }
            }
        }
    }

    fun updateDidiVoEndorsementStatus() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _didiList.value.forEach {didi ->
                if (didi.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal) {
                    val existingProcessStatus = didi.beneficiaryProcessStatus
                    var updatedStatus = mutableListOf<BeneficiaryProcessStatusModel>()
                    existingProcessStatus?.forEach {
                        updatedStatus.add(it)
                    }
                    updatedStatus.add(BeneficiaryProcessStatusModel("VO_ENDORSEMENT", "ACCEPTED"))
                    didiDao.updateBeneficiaryProcessStatus(didi.id, updatedStatus)
                } else if (didi.voEndorsementStatus == DidiEndorsementStatus.REJECTED.ordinal) {
                    val existingProcessStatus = didi.beneficiaryProcessStatus
                    var updatedStatus = mutableListOf<BeneficiaryProcessStatusModel>()
                    existingProcessStatus?.forEach {
                        updatedStatus.add(it)
                    }
                    updatedStatus.add(BeneficiaryProcessStatusModel("VO_ENDORSEMENT", "REJECTED"))
                    didiDao.updateBeneficiaryProcessStatus(didi.id, updatedStatus)
                } else {
                    didiDao.updateNeedToPostVO(false, didiId = didi.id, didi.villageId)
                }
            }
        }
    }

    fun updatePatStatusToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO){
                    val needToPostDidiList=didiDao.getAllNeedToPostPATDidi(needsToPostPAT = true, villageId = prefRepo.getSelectedVillage().id)
                    if(needToPostDidiList.isNotEmpty()){
                        needToPostDidiList.forEach { didi->
                            launch {
                                didi.patSurveyStatus.let {
                                    if (it == PatSurveyStatus.COMPLETED.ordinal) {
                                        val updateWealthRankResponse=apiService.updateDidiRanking(
                                            listOf(
                                                EditDidiWealthRankingRequest(didi.id,StepType.PAT_SURVEY.name, PatSurveyStatus.COMPLETED.name),
                                            )
                                        )
                                        if(updateWealthRankResponse.status.equals(SUCCESS,true)){
                                            didiDao.updateNeedToPostPAT(false, didi.id, didi.villageId)
                                        } else {
                                            networkCallbackListener.onFailed()
                                        }
                                    } else if (it == PatSurveyStatus.NOT_AVAILABLE.ordinal) {
                                        val updateWealthRankResponse=apiService.updateDidiRanking(
                                            listOf(
                                                EditDidiWealthRankingRequest(didi.id,StepType.PAT_SURVEY.name, PatSurveyStatus.NOT_AVAILABLE.name),
                                            )
                                        )
                                        if(updateWealthRankResponse.status.equals(SUCCESS,true)){
                                            didiDao.updateNeedToPostPAT(false, didi.id, didi.villageId)
                                        } else {
                                            networkCallbackListener.onFailed()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                onCatchError(ex)
                networkCallbackListener.onFailed()
                onError("SurveySummaryViewModel", "updatePatStatusToNetwork-> onError: ${ex.message}, \n${ex.stackTrace}")
            }
        }
    }
    fun updateVoStatusToNetwork(networkCallbackListener: NetworkCallbackListener) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                withContext(Dispatchers.IO){
                    val needToPostDidiList=didiDao.getAllNeedToPostPATDidi(needsToPostPAT = true, villageId = prefRepo.getSelectedVillage().id)
                    if(needToPostDidiList.isNotEmpty()){
                        needToPostDidiList.forEach { didi->
                            launch {
                                didi.voEndorsementStatus.let {
                                    if (it == DidiEndorsementStatus.ENDORSED.ordinal) {
                                        val updateWealthRankResponse=apiService.updateDidiRanking(
                                            listOf(
                                                EditDidiWealthRankingRequest(didi.id,StepType.VO_ENDORSEMENT.name, ACCEPTED),
                                            )
                                        )
                                        if(updateWealthRankResponse.status.equals(SUCCESS,true)){
                                            didiDao.updateNeedToPostVO(false, didi.id, didi.villageId)
                                        } else {
                                            networkCallbackListener.onFailed()
                                        }
                                    } else if (it == DidiEndorsementStatus.REJECTED.ordinal) {
                                        val updateWealthRankResponse=apiService.updateDidiRanking(
                                            listOf(
                                                EditDidiWealthRankingRequest(didi.id,StepType.VO_ENDORSEMENT.name, DidiEndorsementStatus.REJECTED.name),
                                            )
                                        )
                                        if(updateWealthRankResponse.status.equals(SUCCESS,true)){
                                            didiDao.updateNeedToPostVO(false, didi.id, didi.villageId)
                                        } else {
                                            networkCallbackListener.onFailed()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                onCatchError(ex)
                networkCallbackListener.onFailed()
                onError("SurveySummaryViewModel", "updateVoStatusToNetwork-> onError: ${ex.message}, \n${ex.stackTrace}")
            }
        }
    }
}
