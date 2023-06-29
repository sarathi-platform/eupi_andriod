package com.patsurvey.nudge.activities.ui.bpc.progress_screens

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.BpcSummaryEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcSelectedDidiDao
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.VillageListDao
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
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.TYPE_INCLUSION
import com.patsurvey.nudge.utils.calculateScore
import com.patsurvey.nudge.utils.toWeightageRatio
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BpcProgressScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiService: ApiService,
    val villageListDao: VillageListDao,
    val stepsListDao: StepsListDao,
    val didiDao: DidiDao,
    val bpcSummaryDao: BpcSummaryDao,
    val bpcSelectedDidiDao: BpcSelectedDidiDao,
    val questionListDao: QuestionListDao,
    val answerDao: AnswerDao
): BaseViewModel() {

    private val _stepsList = MutableStateFlow(listOf<StepListEntity>())
    val stepList: StateFlow<List<StepListEntity>> get() = _stepsList
    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList

    private val _summaryData = MutableStateFlow(BpcSummaryEntity.getEmptySummary())
    val summaryData: StateFlow<BpcSummaryEntity> get() = _summaryData

    val showLoader = mutableStateOf(false)

    val villageSelected = mutableStateOf(0)
    val selectedText = mutableStateOf("Select Village")

    val bpcCompletedDidiCount = mutableStateOf(0)

    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)

    init {
        fetchVillageList()
        fetchBpcSummaryData(prefRepo.getSelectedVillage().id)
    }

    fun fetchBpcSummaryData(villageId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val summary = bpcSummaryDao.getBpcSummaryForVillage(villageId)
            _summaryData.value = summary
        }
    }

    fun fetchVillageList(){
        showLoader.value = true
        val villageId=prefRepo.getSelectedVillage().id
        job=viewModelScope.launch {
            withContext(Dispatchers.IO){
                val villageList=villageListDao.getAllVillages()
                val stepList = stepsListDao.getAllStepsForVillage(villageId = villageId)
//                val tolaDBList=tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
                _villagList.value = villageList
//                _tolaList.emit(tolaDBList)
//                _didiList.emit(didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id))
                withContext(Dispatchers.Main){
                    villageList.mapIndexed { index, villageEntity ->
                        if(prefRepo.getSelectedVillage().id==villageEntity.id){
                            villageSelected.value=index
                        }
                    }
                    _stepsList.value = stepList
                    selectedText.value = prefRepo.getSelectedVillage().name
//                    getStepsList(prefRepo.getSelectedVillage().id)
                    showLoader.value = false
                }
            }
        }
    }

    fun isStepComplete(stepId: Int,villageId: Int): LiveData<Int> {
        return stepsListDao.isStepCompleteLive(stepId,villageId)
    }

    fun updateSelectedVillage(selectedVillageEntity: VillageEntity) {
        prefRepo.saveSelectedVillage(selectedVillageEntity)
    }

    fun callWorkFlowApiToGetWorkFlowId(){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val dbResponse=stepsListDao.getAllStepsForVillage(prefRepo.getSelectedVillage().id)
                val bpcStep = dbResponse.sortedBy { it.orderNumber }.last()
                if(bpcStep.workFlowId==0){
                    val response = apiService.addWorkFlow(
                        listOf(
                            AddWorkFlowRequest(
                                StepStatus.INPROGRESS.name, prefRepo.getSelectedVillage().id,
                                bpcStep.programId, bpcStep.id)
                        ) )
                    withContext(Dispatchers.IO){
                        if (response.status.equals(SUCCESS, true)) {
                            response.data?.let {
                                stepsListDao.updateWorkflowId(bpcStep.id, it[0].id, prefRepo.getSelectedVillage().id, it[0].status)
                            }
                        }else{
                            val error = ApiResponseFailException(response.message)
                            onCatchError(error, ApiType.WORK_FLOW_API)
                            onError(tag = "ProgressScreenViewModel", "Error : ${response.message}")
                        }
                    }
                }

            }catch (ex:Exception){
                onCatchError(ex, ApiType.WORK_FLOW_API)
                onError(tag = "ProgressScreenViewModel", "Error : ${ex.localizedMessage}")
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {

    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {

    }
    fun addDidisToDidiDaoIfNeeded() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiEntityList = didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
            val selectedDidiList = bpcSelectedDidiDao.fetchAllSelectedDidiForVillage(prefRepo.getSelectedVillage().id)
            selectedDidiList.forEach { didiEntity->
                if (!didiEntityList.map { it.id }.contains(didiEntity.id)) {
                    didiDao.insertDidi(
                        DidiEntity.getDidiEntityFromSelectedDidiEntityForBpc(didiEntity)
                    )
                }
            }
        }
    }

    fun getBpcCompletedDidiCount() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didiList = didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
//            val passingScore = questionListDao.getPassingScore()
            val verifiedDidiCount = didiList.size/*didiList.filter { (it.score?.toInt() ?: 0) >= passingScore && (it.crpScore?.toInt() ?: 0) >= passingScore }.size*/
            withContext(Dispatchers.Main) {
                bpcCompletedDidiCount.value = verifiedDidiCount
            }
        }
    }

    fun updateSelectedDidiPatStatus() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val selectedDidiList = bpcSelectedDidiDao.fetchAllSelectedDidiForVillage(prefRepo.getSelectedVillage().id)
            val questionList = questionListDao.getAllQuestions()
            selectedDidiList.forEach { didi ->
                val didiAnswers = answerDao.getAllAnswerForDidi(didi.id)
                if (didiAnswers.filter { it.actionType == TYPE_INCLUSION }.size == questionList.filter { it.actionType == TYPE_INCLUSION }.size) {
                    bpcSelectedDidiDao.updateSelDidiPatSection1Status(didiId = didi.id, PatSurveyStatus.COMPLETED.ordinal)
                    bpcSelectedDidiDao.updateSelDidiPatSection2Status(didiId = didi.id, PatSurveyStatus.COMPLETED.ordinal)
                    bpcSelectedDidiDao.updateSelDidiPatSurveyStatus(didiId = didi.id, PatSurveyStatus.COMPLETED.ordinal)
                    calculateDidiScore(didi.id)
                } else if (didiAnswers.filter { it.actionType == TYPE_INCLUSION }.size < questionList.filter { it.actionType == TYPE_INCLUSION }.size) {
                    bpcSelectedDidiDao.updateSelDidiPatSection1Status(didiId = didi.id, PatSurveyStatus.COMPLETED.ordinal)
                    bpcSelectedDidiDao.updateSelDidiPatSection2Status(didiId = didi.id, PatSurveyStatus.INPROGRESS.ordinal)
                    bpcSelectedDidiDao.updateSelDidiPatSurveyStatus(didiId = didi.id, PatSurveyStatus.INPROGRESS.ordinal)
                } else
                    if (didiAnswers.filter { it.actionType == TYPE_EXCLUSION }.size < questionList.filter { it.actionType == TYPE_EXCLUSION }.size) {
                    bpcSelectedDidiDao.updateSelDidiPatSection1Status(didiId = didi.id, PatSurveyStatus.INPROGRESS.ordinal)
                    bpcSelectedDidiDao.updateSelDidiPatSection2Status(didiId = didi.id, PatSurveyStatus.NOT_STARTED.ordinal)
                    bpcSelectedDidiDao.updateSelDidiPatSurveyStatus(didiId = didi.id, PatSurveyStatus.INPROGRESS.ordinal)
                } else {
                    if (didiAnswers.filter { it.actionType == TYPE_EXCLUSION }.size == questionList.filter { it.actionType == TYPE_EXCLUSION }.size) {
                        val yesAnswerCount = answerDao.fetchOptionYesCount(didiId = didi.id, QuestionType.RadioButton.name,TYPE_EXCLUSION)
                        if (yesAnswerCount > 0) {
                            bpcSelectedDidiDao.updateSelDidiPatSection1Status(didiId = didi.id, PatSurveyStatus.COMPLETED.ordinal)
                            bpcSelectedDidiDao.updateSelDidiPatSection2Status(didiId = didi.id, PatSurveyStatus.NOT_STARTED.ordinal)
                            bpcSelectedDidiDao.updateSelDidiPatSurveyStatus(didiId = didi.id, PatSurveyStatus.COMPLETED.ordinal)
                        } else {
                            bpcSelectedDidiDao.updateSelDidiPatSection1Status(didiId = didi.id, PatSurveyStatus.COMPLETED.ordinal)
                            bpcSelectedDidiDao.updateSelDidiPatSection2Status(didiId = didi.id, PatSurveyStatus.INPROGRESS.ordinal)
                            bpcSelectedDidiDao.updateSelDidiPatSurveyStatus(didiId = didi.id, PatSurveyStatus.INPROGRESS.ordinal)
                        }
                    }
                }
            }
        }
    }

    fun calculateDidiScore(didiId: Int) {
        var passingMark = 0
        var isDidiAccepted = false
        var comment = LOW_SCORE
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                val inclusiveQueList = answerDao.getAllInclusiveQues(didiId = didiId)
                if (inclusiveQueList.isNotEmpty()) {
                    var totalWightWithoutNumQue = answerDao.getTotalWeightWithoutNumQues(didiId)
                    val numQueList =
                        inclusiveQueList.filter { it.type == QuestionType.Numeric_Field.name }
                    if (numQueList.isNotEmpty()) {
                        numQueList.forEach { answer ->
                            val numQue = questionListDao.getQuestion(answer.questionId)
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
                        didiDao.updateVOEndorsementDidiStatus(
                            prefRepo.getSelectedVillage().id,
                            didiId
                        )
                    }
                    didiDao.updateDidiScore(
                        score = totalWightWithoutNumQue,
                        comment = comment,
                        didiId = didiId,
                        isDidiAccepted = isDidiAccepted
                    )
                    if (prefRepo.isUserBPC()) {
                        bpcSelectedDidiDao.updateSelDidiScore(
                            score = totalWightWithoutNumQue,
                            comment = comment,
                            didiId = didiId,
                        )
                    }
                }
                else {
                    didiDao.updateDidiScore(
                        score = 0.0,
                        comment = TYPE_EXCLUSION,
                        didiId = didiId,
                        isDidiAccepted = false
                    )
                    if (prefRepo.isUserBPC()) {
                        bpcSelectedDidiDao.updateSelDidiScore(
                            score = 0.0,
                            comment = TYPE_EXCLUSION,
                            didiId = didiId,
                        )
                    }
                }
                didiDao.updateModifiedDateServerId(System.currentTimeMillis(), didiId)
            }
        }
    }

}
