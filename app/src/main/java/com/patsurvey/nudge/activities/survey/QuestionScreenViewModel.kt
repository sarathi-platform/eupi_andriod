package com.patsurvey.nudge.activities.survey

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.dataModel.AnswerOptionModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.PatSurveyStatus
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
    val answerDao: AnswerDao,
    val apiService: ApiService
) : BaseViewModel() {

    private val _questionList = MutableStateFlow(listOf<QuestionEntity>())
    val questionList: StateFlow<List<QuestionEntity>> get() = _questionList
    private val _optionList = MutableStateFlow(listOf<AnswerOptionModel>())
    val optionList: StateFlow<List<AnswerOptionModel>> get() = _optionList

    private val _answerList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val answerList: StateFlow<List<SectionAnswerEntity>> get() = _answerList

    val didiName = mutableStateOf("DidiEntity()")
    val mDidiId = mutableStateOf(0)
    val isYesClick = mutableStateOf(false)
    val isAnswered = mutableStateOf(false)
    val sectionType = mutableStateOf(TYPE_EXCLUSION)


   fun getAllQuestionsAnswers(didiId: Int) {
       job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
           val questionList = questionListDao.getQuestionForType(sectionType.value)
           val localAnswerList = answerDao.getAnswerForDidi(sectionType.value, didiId = didiId)
           withContext(Dispatchers.IO) {
               try {
                   _questionList.emit(questionList)
                   _answerList.emit(localAnswerList)
                   updateAnswerOptions(0,didiId)
               }catch (ex:Exception){
                   ex.printStackTrace()
               }

           }
       }
   }

    fun findQuestionOptionList(questionIndex:Int){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val updatedList = mutableListOf<AnswerOptionModel>()
            withContext(Dispatchers.IO) {
                try {
                    questionList.value[questionIndex].options?.forEach {
                        updatedList.add(AnswerOptionModel(it?.optionId?:0,it?.display?: BLANK_STRING,false))
                    }
                    _optionList.emit(updatedList)
                }catch (ex:Exception){
                    ex.printStackTrace()
                }

            }
        }
    }

    fun setDidiDetails(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didi = didiDao.getDidi(didiId)
            updateDidiQuesSection(didiId,PatSurveyStatus.INPROGRESS.ordinal)
            withContext(Dispatchers.Main) {
                didiName.value = didi.name
                mDidiId.value=didi.id
            }
        }
    }

    fun updateDidiQuesSection(didiId: Int,status:Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
             didiDao.updateQuesSectionStatus(didiId,status)
        }
    }
    fun setAnswerToQuestion(didiId: Int,optionId:Int,questionIndex:Int,answerOption:String,answerValue:String,onAnswerSave: () ->Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localAnswerList = answerDao.getAnswerForDidi(sectionType.value, didiId = didiId)
            _answerList.emit(localAnswerList)
            withContext(Dispatchers.IO) {
                if(questionList.value.size>questionIndex){
                    val alreadyAnsweredModel= answerDao.isAlreadyAnswered(didiId = didiId,
                        questionId =  questionList.value[questionIndex].questionId?:0, actionType = sectionType.value)

                    if(alreadyAnsweredModel!=null){
                        answerDao.updateAnswer(didiId = didiId, questionId = questionList.value[questionIndex].questionId?:0,
                            actionType = sectionType.value,
                        answerValue = answerValue,
                        answerOption = answerOption, optionId = optionId)
                        withContext(Dispatchers.Main){
                            onAnswerSave()
                        }
                    }else{
                        answerDao.insertAnswer(SectionAnswerEntity(id = 0, optionId = optionId,
                            didiId = didiId,
                            answerOption = answerOption,
                            answerValue = answerValue,
                            questionId = questionList.value[questionIndex].questionId?:0,
                            actionType = sectionType.value,
                            type = QuestionType.RadioButton.name))
                        withContext(Dispatchers.Main){
                            onAnswerSave()
                        }
                    }

            }
        }
    }
    }
    fun updateAnswerOptions(questionIndex:Int,didiId: Int){
        job = CoroutineScope(Dispatchers.IO +exceptionHandler).launch{
            val localAnswerList = answerDao.getAnswerForDidi(sectionType.value, didiId = didiId)
            _answerList.emit(localAnswerList)
              withContext(Dispatchers.IO){
                if(questionList.value.size>questionIndex){
                    val alreadyAnsweredModel= questionList.value[questionIndex].questionId?.let {
                        answerDao.isAlreadyAnswered(
                            didiId = didiId,
                            questionId = it,
                            sectionType.value)
                    }

                    withContext(Dispatchers.Main){
                        if(alreadyAnsweredModel!=null){
                            isYesClick.value=alreadyAnsweredModel.answerValue.equals("Yes",true)
                            isAnswered.value=true
                        }else{
                            isYesClick.value=false
                            isAnswered.value=false
                        }
                    }

                }
            }
        }

    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }
}