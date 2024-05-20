package com.patsurvey.nudge.activities.ui.bpc.score_comparision

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcScorePercentageDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.calculateMatchPercentage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScoreComparisonViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val didiDao: DidiDao,
    val questionListDao: QuestionListDao,
    val answerDao: AnswerDao,
    val bpcScorePercentageDao: BpcScorePercentageDao
): BaseViewModel() {


    private val _didiList = MutableStateFlow(listOf<DidiEntity>())
    val didiList: StateFlow<List<DidiEntity>> get() = _didiList

    private var _filterDidiList = MutableStateFlow(listOf<DidiEntity>())
    val filterDidiList: StateFlow<List<DidiEntity>> get() = _filterDidiList

    private val _questionPassingScore = MutableStateFlow(0)
    val questionPassingScore: StateFlow<Int> get() = _questionPassingScore

    val _passPercentage = MutableStateFlow(0)
    val passPercentage: StateFlow<Int> get() = _passPercentage

    val exclusionListResponse = mutableStateMapOf<Int, String>()

    var minMatchPercentage: Int = 0

    val showLoader = mutableStateOf(false)

    fun init() {
        getBpcScorePercentage()
        fetchDidiList()
    }

    private fun getBpcScorePercentage() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val bpcScorePercentageForVillage = bpcScorePercentageDao.getBpcScorePercentageForState(prefRepo.getSelectedVillage().stateId)
            minMatchPercentage = bpcScorePercentageForVillage.percentage
        }
    }

    fun fetchDidiList() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localDidList = didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
            val filterdLocalList = localDidList.filter {it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal }
            _didiList.value = filterdLocalList
            val passingScore = questionListDao.getPassingScore()
            _questionPassingScore.value = passingScore

            _filterDidiList.value = didiList.value
            _passPercentage.value =
                calculateMatchPercentage(didiList.value, questionPassingScore.value)
            val exclusionList = localDidList.filter { it.section1Status == PatSurveyStatus.COMPLETED.ordinal && it.section2Status == PatSurveyStatus.NOT_STARTED.ordinal }
            if (exclusionList.isNotEmpty()) {
                val questionList = questionListDao.getQuestionForType(TYPE_EXCLUSION, prefRepo.getAppLanguageId() ?: 2)
                exclusionList.forEach { didi ->
                    var exclusionResponse = ""
                    val exclusionItems = answerDao.getAnswerForDidi(didiId = didi.id, actionType = TYPE_EXCLUSION).filter { it.optionValue == 1 }
                    exclusionItems.forEach {
                        exclusionResponse = "${exclusionResponse}${questionList[questionList.map { it.questionId }.indexOf(it.questionId)].questionSummary}, "
                    }
                    exclusionListResponse[didi.id] = exclusionResponse
                }
            }
            withContext(Dispatchers.Main) {
                showLoader.value = false
            }
        }
    }


    override fun onServerError(error: ErrorModel?) {
        Log.e("ScoreComparisonViewModel", "onServerError: ${error?.message}")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        Log.e("ScoreComparisonViewModel", "onServerError: ${errorModel?.message}, api: ${errorModel?.apiName} ")
    }

}
