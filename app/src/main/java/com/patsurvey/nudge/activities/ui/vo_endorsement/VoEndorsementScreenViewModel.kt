package com.patsurvey.nudge.activities.ui.vo_endorsement

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VoEndorsementScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val didiDao: DidiDao,
    val tolaDao: TolaDao,
    val questionListDao: QuestionListDao,
    val answerDao: AnswerDao,
    val numericAnswerDao: NumericAnswerDao,
    val stepsListDao: StepsListDao
): BaseViewModel() {

    val pendingDidiCount = mutableStateOf(0)
    private val _didiList = MutableStateFlow(listOf<DidiEntity>())
    val didiList: StateFlow<List<DidiEntity>> get() = _didiList

    private var _filterDidiList = MutableStateFlow(listOf<DidiEntity>())
    val filterDidiList: StateFlow<List<DidiEntity>> get() = _filterDidiList

    private var _inclusiveQueList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val  inclusiveQueList: StateFlow<List<SectionAnswerEntity>> get() = _inclusiveQueList

    var tolaMapList by mutableStateOf(mapOf<String, List<DidiEntity>>())
        private set

    var filterTolaMapList by mutableStateOf(mapOf<String, List<DidiEntity>>())
        private set

    var villageId: Int = -1
    var stepId: Int = -1

    val showLoader = mutableStateOf(false)

    init {
        villageId = prefRepo.getSelectedVillage().id
//        fetchDidisFromDB()
    }

    @SuppressLint("SuspiciousIndentation")
    fun fetchDidisFromDB() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
               val  localDidiList = didiDao.patCompletedDidis(villageId)
                var passingMark=0
                val scoredDidiList:ArrayList<DidiEntity> = arrayListOf()
                if(localDidiList.isNotEmpty()){
                    localDidiList.forEach {didi->
                            _inclusiveQueList.value = answerDao.getAllInclusiveQues(didiId = didi.id)
                            if(_inclusiveQueList.value.isNotEmpty()){
                                var totalWightWithoutNumQue = answerDao.getTotalWeightWithoutNumQues(didi.id)
                                val numQueList= _inclusiveQueList.value.filter { it.type == QuestionType.Numeric_Field.name }
                                if(numQueList.isNotEmpty()){
                                    numQueList.forEach { answer->
                                        val numQue=questionListDao.getQuestion(answer.questionId)
                                        passingMark =numQue.surveyPassingMark?:0
                                        if(numQue.questionFlag?.equals(FLAG_WEIGHT,true) == true){
                                            val weightList= toWeightageRatio(numQue.json.toString())
                                              if(weightList.isNotEmpty()){
                                                    val newScore= calculateScore(weightList,
                                                        answer.totalAssetAmount?.toDouble()?:0.0,
                                                        false)
                                                  totalWightWithoutNumQue += newScore
                                              }
                                        }else if(numQue.questionFlag?.equals(FLAG_RATIO,true) == true){
                                            val ratioList= toWeightageRatio(numQue.json.toString())
                                            val newScore= calculateScore(ratioList,
                                                answer.totalAssetAmount?.toDouble()?:0.0,
                                                true)
                                            totalWightWithoutNumQue += newScore
                                        }
                                    }
                                }
                                if(totalWightWithoutNumQue>=passingMark){
                                    didiDao.updateVOEndorsementDidiStatus(prefRepo.getSelectedVillage().id,didi.id)
                                    scoredDidiList.add(didi)

                                }
                            }


                    }
                    _didiList.value = scoredDidiList
                    pendingDidiCount.value = _didiList.value.filter { it.voEndorsementStatus == DidiEndorsementStatus.NOT_STARTED.ordinal }.size
                    _filterDidiList.value = _didiList.value

                }
            }
        }
    }

    fun filterList() {
        val map = mutableMapOf<String, MutableList<DidiEntity>>()
        didiList.value.forEachIndexed { _, didiDetailsModel ->
            if (map.containsKey(didiDetailsModel.cohortName)) {
                map[didiDetailsModel.cohortName]?.add(didiDetailsModel)
            } else {
                map[didiDetailsModel.cohortName] = mutableListOf(didiDetailsModel)
            }
        }
        tolaMapList = map
        filterTolaMapList = map

    }

    fun performQuery(query: String, isTolaFilterSelected: Boolean) {
        if (!isTolaFilterSelected) {
            _filterDidiList.value = if (query.isNotEmpty()) {
                val filteredList = ArrayList<DidiEntity>()
                didiList.value.forEach { didi ->
                    if (didi.name.lowercase().contains(query.lowercase())) {
                        filteredList.add(didi)
                    }
                }
                filteredList
            } else {
                didiList.value
            }
        } else {
            if (query.isNotEmpty()) {
                val fList = mutableMapOf<String, MutableList<DidiEntity>>()
                tolaMapList.keys.forEach { key ->
                    val newDidiList = ArrayList<DidiEntity>()
                    tolaMapList[key]?.forEach { didi ->
                        if (didi.name.lowercase().contains(query.lowercase())) {
                            newDidiList.add(didi)
                        }
                    }
                    if (newDidiList.isNotEmpty())
                        fList[key] = newDidiList
                }
                filterTolaMapList = fList
            } else {
                filterTolaMapList = tolaMapList
            }
        }
    }


    fun getVoEndorsementStepStatus(stepId: Int, callBack: (isComplete: Boolean) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val stepStatus = stepsListDao.isStepComplete(stepId, villageId)
            withContext(Dispatchers.Main) {
                if (stepStatus == StepStatus.COMPLETED.ordinal) {
                    callBack(true)
                } else {
                    callBack(false)
                }
            }
        }
    }
    override fun onServerError(error: ErrorModel?) {
        TODO("Not yet implemented")
    }
}