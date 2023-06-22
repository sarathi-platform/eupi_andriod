package com.patsurvey.nudge.activities.ui.bpc.score_comparision

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.network.model.ErrorModelWithApi
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

    var filterDidiList by mutableStateOf(listOf<DidiEntity>())
        private set

    private val _questionPassingScore = MutableStateFlow(0)
    val questionPassingScore: StateFlow<Int> get() = _questionPassingScore

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

            filterDidiList = didiList.value
        }
    }

    fun calculateMatchPercentage(): Int {
        val matchedCount = filterDidiList.filter {
            (it.score ?: 0.0) >= questionPassingScore.value.toDouble()
                    && (it.crpScore ?: 0.0) >= questionPassingScore.value.toDouble() }.size

        return ((matchedCount/filterDidiList.size) * 100).toInt()

    }

    override fun onServerError(error: ErrorModel?) {
        TODO("Not yet implemented")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }

}
