package com.patsurvey.nudge.activities.ui.bpc.score_comparision

import android.util.Log
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.PatSurveyStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreComparisonViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val didiDao: DidiDao,
    val questionListDao: QuestionListDao
): BaseViewModel() {


    private val _didiList = MutableStateFlow(listOf<DidiEntity>())
    val didiList: StateFlow<List<DidiEntity>> get() = _didiList

    private var _filterDidiList = MutableStateFlow(listOf<DidiEntity>())
    val filterDidiList: StateFlow<List<DidiEntity>> get() = _filterDidiList

    private val _questionPassingScore = MutableStateFlow(0)
    val questionPassingScore: StateFlow<Int> get() = _questionPassingScore

    val _passPercentage = MutableStateFlow(0)
    val passPercentage: StateFlow<Int> get() = _passPercentage

    init {
        fetchDidiList()
    }

    fun fetchDidiList() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localDidList = didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
            val filterdLocalList = localDidList.filter { it.section1Status == PatSurveyStatus.COMPLETED.ordinal
                    && it.section2Status == PatSurveyStatus.COMPLETED.ordinal
                    && it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal
            }
            _didiList.value = localDidList

            val passingScore = questionListDao.getPassingScore()
            _questionPassingScore.value = passingScore

            _filterDidiList.value = didiList.value
            _passPercentage.value = calculateMatchPercentage(didiList.value)
        }
    }

    fun calculateMatchPercentage(didiList: List<DidiEntity>): Int {
        val matchedCount = didiList.filter {
            (it.score ?: 0.0) >= questionPassingScore.value.toDouble()
                    && (it.crpScore ?: 0.0) >= questionPassingScore.value.toDouble() }.size

        return ((matchedCount.toFloat()/didiList.size.toFloat()) * 100).toInt()

    }

    override fun onServerError(error: ErrorModel?) {
        Log.e("ScoreComparisonViewModel", "onServerError: ${error?.message}")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        Log.e("ScoreComparisonViewModel", "onServerError: ${errorModel?.message}, api: ${errorModel?.apiName} ")
    }

}
