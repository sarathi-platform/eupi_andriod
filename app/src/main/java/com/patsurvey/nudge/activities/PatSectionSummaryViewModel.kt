package com.patsurvey.nudge.activities

import android.util.Log
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PatSectionSummaryViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val didiDao: DidiDao,
    val questionListDao: QuestionListDao,
    val answerDao: AnswerDao
) : BaseViewModel() {

    private val _didiEntity = MutableStateFlow(DidiEntity(
        id = 0,
        name = "",
        address = "",
        guardianName = "",
        relationship = "",
        castId = 0,
        castName = "",
        cohortId = 0,
        cohortName = "",
        villageId = 0,)
    )
    val didiEntity: StateFlow<DidiEntity> get() = _didiEntity

    private val _questionList = MutableStateFlow(listOf<QuestionEntity>())
    val questionList : StateFlow<List<QuestionEntity>> get() = _questionList

    private val _answerList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val answerList : StateFlow<List<SectionAnswerEntity>> get() = _answerList

    fun setDidiDetailsFromDb(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _didiEntity.emit(didiDao.getDidi(didiId))
        }
    }

    fun getQuestionAnswerListForSectionOne(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val questionList = questionListDao.getQuestionForType(TYPE_EXCLUSION)
            val localAnswerList = answerDao.getAnswerForDidi(TYPE_EXCLUSION, didiId = didiId)
            withContext(Dispatchers.Main) {
                try {
                    _questionList.emit(questionList)
                    _answerList.emit(localAnswerList)
                }catch (ex:Exception){
                    ex.printStackTrace()
                }

            }
        }
    }
}
