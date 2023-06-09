package com.patsurvey.nudge.activities

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.TYPE_INCLUSION
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

    private val _didiEntity = MutableStateFlow(
        DidiEntity(
            id = 0,
            name = "",
            address = "",
            guardianName = "",
            relationship = "",
            castId = 0,
            castName = "",
            cohortId = 0,
            cohortName = "",
            villageId = 0,
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis(),
            shgFlag = SHGFlag.NOT_MARKED.value
        )
    )
    val didiEntity: StateFlow<DidiEntity> get() = _didiEntity

    private val _questionList = MutableStateFlow(listOf<QuestionEntity>())
    val questionList: StateFlow<List<QuestionEntity>> get() = _questionList

    private val _answerList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val answerList: StateFlow<List<SectionAnswerEntity>> get() = _answerList

    private val _answerSummeryList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val answerSummeryList: StateFlow<List<SectionAnswerEntity>> get() = _answerSummeryList
    val isYesSelected = mutableStateOf(false)

    fun setDidiDetailsFromDb(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
           val localDidiDetails=didiDao.getDidi(didiId)
            val questionList = questionListDao.getQuestionForType(TYPE_EXCLUSION,prefRepo.getAppLanguageId()?:2)
            val localAnswerList = answerDao.getAnswerForDidi(TYPE_EXCLUSION, didiId = didiId)
            val localSummeryList = answerDao.getAnswerForDidi(TYPE_INCLUSION, didiId = didiId)
            withContext(Dispatchers.IO){
                _didiEntity.emit(localDidiDetails)
                _questionList.emit(questionList)
                _answerList.emit(localAnswerList)
                _answerSummeryList.emit(localSummeryList)
            }
        }
    }

    fun setPATSurveyComplete(didiId: Int,status:Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                didiDao.updateQuesSectionStatus(didiId,status)
                didiDao.updateDidiNeedToPostPat(didiId, true)
            }
        }
    }
    fun setPATSection1Complete(didiId: Int,status:Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                didiDao.updatePatSection1Status(didiId,status)
            }
        }
    }
    fun setPATSection2Complete(didiId: Int,status:Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                didiDao.updatePatSection2Status(didiId,status)
            }
        }
    }

    fun getQuestionAnswerListForSectionOne(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val questionList = questionListDao.getQuestionForType(TYPE_EXCLUSION,prefRepo.getAppLanguageId()?:2)
            val localAnswerList = answerDao.getAnswerForDidi(TYPE_EXCLUSION, didiId = didiId)
            val yesQuesCount = answerDao.fetchOptionYesCount(didiId = didiId,QuestionType.RadioButton.name,TYPE_EXCLUSION)
            withContext(Dispatchers.IO) {
                try {
                    _questionList.emit(questionList)
                    _answerList.emit(localAnswerList)
                    if(yesQuesCount>0){
                        isYesSelected.value=true
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }
        }
    }

    override fun onServerError(error: ErrorModel?) {

    }
}
