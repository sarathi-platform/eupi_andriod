package com.patsurvey.nudge.activities.ui.bpc.progress_screens

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.patsurvey.nudge.MyApplication.Companion.appScopeLaunch
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.BpcSummaryEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.AddWorkFlowRequest
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ApiResponseFailException
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.FLAG_RATIO
import com.patsurvey.nudge.utils.FLAG_WEIGHT
import com.patsurvey.nudge.utils.LOW_SCORE
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.TYPE_INCLUSION
import com.patsurvey.nudge.utils.calculateScore
import com.patsurvey.nudge.utils.toWeightageRatio
import com.patsurvey.nudge.utils.updateLastSyncTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class BpcProgressScreenViewModel @Inject constructor(
    val repository: BPCProgressScreenRepository
) : BaseViewModel() {

   /* private val _stepsList = MutableStateFlow(listOf<StepListEntity>())
    val stepList: StateFlow<List<StepListEntity>> get() = _stepsList*/
    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList

    private val _summaryData = MutableStateFlow(BpcSummaryEntity.getEmptySummary())
    val summaryData: StateFlow<BpcSummaryEntity> get() = _summaryData

    val showLoader = mutableStateOf(false)

    val villageSelected = mutableStateOf(0)
    val selectedText = mutableStateOf("Select Village")

    val bpcCompletedDidiCount = mutableStateOf(0)

    val isBpcVerificationComplete = mutableStateOf(mutableMapOf<Int, Boolean>())

    var bpcSummaryLiveData: LiveData<BpcSummaryEntity> =
        repository.getBpcSummaryDataForSelectedVillage()

    val stepListLive: LiveData<List<StepListEntity>> = repository.getStepListForVillageLive()

    fun isLoggedIn() = (repository.prefRepo.getAccessToken()?.isNotEmpty() == true)

    fun init(context: Context) {
        showLoader.value = true
        selectedText.value = repository.getSelectedVillage().name
        appScopeLaunch(Dispatchers.IO) {
            getBpcSummaryDataForSelectedVillage()
            fetchBpcDataForVillage(networkCallbackListener = object : NetworkCallbackListener {
                override fun onSuccess() {
                    appScopeLaunch(Dispatchers.IO) {
                        delay(100)
                        setBpcVerificationCompleteForVillages()
                        repository.updateVillageDataLoadStatus(getSelectedVillage().id, true)
                        delay(200)
                        withContext(Dispatchers.Main) {
                            showLoader.value = false
                        }
                    }
                }

                override fun onFailed() {
                    showLoader.value = false

                }
            })
        }
    }

    private suspend fun getBpcSummaryDataForSelectedVillage() {
        val selectedVillage = repository.getSelectedVillage()
        if (repository.isSummaryAlreadyExistsForVillage(selectedVillage.id) == 0)
            repository.fetchBpcSummaryDataForVillageFromNetwork(selectedVillage)
    }

    private suspend fun fetchBpcDataForVillage(networkCallbackListener: NetworkCallbackListener) {
        if (!repository.isDataLoadTried(getSelectedVillage().id))
            repository.fetchBpcDataForVillage(
                repository.getSelectedVillage(),
                networkCallbackListener = networkCallbackListener
            )
        else {
            networkCallbackListener.onSuccess()
        }
    }

    fun fetchBpcSummaryData(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val summary = repository.getBpcSummaryForVillage(villageId) ?: BpcSummaryEntity(
                    0, 0, 0, 0, 0, 0, villageId = villageId
                )
                _summaryData.value = summary
            } catch (ex: Exception) {
                NudgeLogger.e("BpcProgressScreenViewModel", "fetchBpcSummaryData -> catch", ex)
                val summary = BpcSummaryEntity(0, 0, 0, 0, 0, 0, villageId = villageId)
                _summaryData.value = summary
            }

        }
    }

    /*fun fetchVillageList(){
        job=viewModelScope.launch {
            withContext(Dispatchers.IO){
                val villageList=repository.getAllVillages()
//                val stepList = stepsListDao.getAllStepsForVillage(villageId = villageId)
//                val tolaDBList=tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
                _villagList.value = villageList
//                _tolaList.emit(tolaDBList)
//                _didiList.emit(didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id))
                withContext(Dispatchers.Main){
                    villageList.mapIndexed { index, villageEntity ->
                        if(repository.prefRepo.getSelectedVillage().id==villageEntity.id){
                            villageSelected.value=index
                        }
                    }
                    selectedText.value = villageList[villageList.map { it.id }.indexOf(repository.prefRepo.getSelectedVillage().id)].name
                    getStepsList(repository.prefRepo.getSelectedVillage().id)
                }
            }
        }
    }*/

    /*fun getStepsList(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepList = repository.getAllStepsForVillage(villageId)
            withContext(Dispatchers.IO) {
                _stepsList.value = stepList
            }
        }
    }*/

    fun isStepComplete(stepId: Int, villageId: Int): LiveData<Int>? {
        return repository.stepsListDao.isStepCompleteLiveForBpc(stepId, villageId)
    }

    fun updateSelectedVillage(selectedVillageEntity: VillageEntity) {
        repository.prefRepo.saveSelectedVillage(selectedVillageEntity)
    }

    fun callWorkFlowApiToGetWorkFlowId() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val dbResponse =
                    repository.getAllStepsForVillage(repository.prefRepo.getSelectedVillage().id)
                val bpcStep = dbResponse.sortedBy { it.orderNumber }.last()
                if (bpcStep.workFlowId == 0) {
                    val response = repository.addWorkFlow(
                        listOf(
                            AddWorkFlowRequest(
                                StepStatus.INPROGRESS.name,
                                repository.prefRepo.getSelectedVillage().id,
                                bpcStep.programId,
                                bpcStep.id
                            )
                        )
                    )
                    NudgeLogger.d(
                        "BpcProgressScreenViewModel", "addWorkFlow Request=> ${
                            Gson().toJson(
                                listOf(
                                    AddWorkFlowRequest(
                                        StepStatus.INPROGRESS.name,
                                        repository.prefRepo.getSelectedVillage().id,
                                        bpcStep.programId,
                                        bpcStep.id
                                    )
                                )
                            )
                        }"
                    )

                    if (response.status.equals(SUCCESS, true)) {
                        response.data?.let {
                            repository.updateWorkflowId(
                                bpcStep.id,
                                it[0].id,
                                repository.prefRepo.getSelectedVillage().id,
                                it[0].status
                            )
                        }
                    } else {
                        val error = ApiResponseFailException(response.message)
                        onCatchError(error, ApiType.WORK_FLOW_API)
                        onError(tag = "BpcProgressScreenViewModel", "Error : ${response.message}")
                    }
                    if (!response.lastSyncTime.isNullOrEmpty()) {
                        updateLastSyncTime(repository.prefRepo, response.lastSyncTime)
                    }
                }
            } catch (ex: Exception) {
                onCatchError(ex, ApiType.WORK_FLOW_API)
                onError(tag = "BpcProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {

    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {

    }

    fun getBpcCompletedDidiCount() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = repository.getAllDidisForVillage()
//            val passingScore = questionListDao.getPassingScore()
            val verifiedDidiCount =
                didiList.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal }.size/*didiList.filter { (it.score?.toInt() ?: 0) >= passingScore && (it.crpScore?.toInt() ?: 0) >= passingScore }.size*/
            withContext(Dispatchers.Main) {
                bpcCompletedDidiCount.value = verifiedDidiCount
            }
        }
    }

    fun calculateDidiScore(didiId: Int) {
        var passingMark = 0
        var isDidiAccepted = false
        var comment = LOW_SCORE
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                val inclusiveQueList = repository.getAllInclusiveQues(didiId = didiId)
                if (inclusiveQueList.isNotEmpty()) {
                    var totalWightWithoutNumQue = repository.getTotalWeightWithoutNumQues(didiId)
                    val numQueList =
                        inclusiveQueList.filter { it.type == QuestionType.Numeric_Field.name }
                    if (numQueList.isNotEmpty()) {
                        numQueList.forEach { answer ->
                            val numQue = repository.getQuestion(answer.questionId)
                            passingMark = numQue.surveyPassingMark ?: 0
                            if (numQue.questionFlag?.equals(FLAG_WEIGHT, true) == true) {
                                val weightList = toWeightageRatio(numQue.json.toString())
                                if (weightList.isNotEmpty()) {
                                    val newScore = calculateScore(
                                        weightList,
                                        answer.totalAssetAmount?.toDouble() ?: 0.0,
                                        false
                                    )
                                    totalWightWithoutNumQue += newScore
                                }
                            } else if (numQue.questionFlag?.equals(FLAG_RATIO, true) == true) {
                                val ratioList = toWeightageRatio(numQue.json.toString())
                                val newScore = calculateScore(
                                    ratioList,
                                    answer.totalAssetAmount?.toDouble() ?: 0.0,
                                    true
                                )
                                totalWightWithoutNumQue += newScore
                            }
                        }
                    }
                    // TotalScore
                    if (totalWightWithoutNumQue >= passingMark) {
                        isDidiAccepted = true
                        comment = BLANK_STRING
                        repository.updateVOEndorsementDidiStatus(
                            didiId = didiId,
                            status = 1
                        )
                    } else {
                        isDidiAccepted = false
                        repository.updateVOEndorsementDidiStatus(
                            didiId = didiId,
                            status = 0
                        )
                    }
                    repository.updateDidiScore(
                        score = totalWightWithoutNumQue,
                        comment = comment,
                        didiId = didiId,
                        isDidiAccepted = isDidiAccepted
                    )
                } else {
                    repository.updateDidiScore(
                        score = 0.0,
                        comment = TYPE_EXCLUSION,
                        didiId = didiId,
                        isDidiAccepted = false
                    )

                }
                repository.updateModifiedDateServerId(didiId)
            }
        }
    }

    fun setBpcVerificationCompleteForVillages() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            villageList.value.forEach { village ->
                val stepList = repository.getAllStepsForVillage(village.id)
                isBpcVerificationComplete.value[village.id] = (stepList.sortedBy { it.orderNumber }
                    .last().isComplete == StepStatus.COMPLETED.ordinal)
            }
        }
    }

    fun getSelectedVillage(): VillageEntity = repository.getSelectedVillage()
    fun refreshDataForCurrentVillage() {

    }

}
