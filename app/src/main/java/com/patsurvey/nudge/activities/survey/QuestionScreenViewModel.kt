package com.patsurvey.nudge.activities.survey

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class QuestionScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val didiDao: DidiDao,
    val villageListDao: VillageListDao,
    val questionListDao: QuestionListDao,
    val apiService: ApiService
) : BaseViewModel() {

    private val _questionList = MutableStateFlow(listOf<QuestionEntity>())
    val questionList: StateFlow<List<QuestionEntity>> get() = _questionList

    val didiName = mutableStateOf("DidiEntity()")

    init {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val questionList = questionListDao.getQuestionForType(TYPE_EXCLUSION)
            withContext(Dispatchers.IO) {
                _questionList.emit(questionList)
            }
        }
    }

    fun setDidiDetails(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didi = didiDao.getDidi(didiId)
            withContext(Dispatchers.Main) {
                didiName.value = didi.name
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }
}