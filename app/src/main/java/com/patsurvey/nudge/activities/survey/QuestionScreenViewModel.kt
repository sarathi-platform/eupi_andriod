package com.patsurvey.nudge.activities.survey

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.QuestionType
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

    private val _answerList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val answerList: StateFlow<List<SectionAnswerEntity>> get() = _answerList

    val didiName = mutableStateOf("DidiEntity()")
    val mDidiId = mutableStateOf(0)
    val isYesClick = mutableStateOf(false)
    val isAnswered = mutableStateOf(false)


   fun getAllQuestionsAnswers(didiId: Int) {
       job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
           val questionList = questionListDao.getQuestionForType(TYPE_EXCLUSION)
           val localAnswerList = answerDao.getAnswerForDidi(TYPE_EXCLUSION, didiId = didiId)
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

    fun setDidiDetails(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didi = didiDao.getDidi(didiId)
            withContext(Dispatchers.Main) {
                didiName.value = didi.name
                mDidiId.value=didi.id
            }
        }
    }
    fun setAnswerToQuestion(didiId: Int,questionIndex:Int,answerOption:String,answerValue:String,onAnswerSave: () ->Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localAnswerList = answerDao.getAnswerForDidi(TYPE_EXCLUSION, didiId = didiId)
            _answerList.emit(localAnswerList)
            withContext(Dispatchers.IO) {
                if(questionList.value.size>questionIndex){
                    val alreadyAnsweredModel= answerDao.isAlreadyAnswered(
                        questionList.value[questionIndex].questionId?:0,
                            TYPE_EXCLUSION)

                    if(alreadyAnsweredModel!=null){
                        answerDao.updateAnswer(questionId = questionList.value[questionIndex].questionId?:0,
                            actionType = TYPE_EXCLUSION,
                        answerValue = answerValue,
                        answerOption = answerOption)
                        withContext(Dispatchers.Main){
                            onAnswerSave()
                        }
                    }else{
                        answerDao.insertAnswer(SectionAnswerEntity((localAnswerList.size+1),
                         didiId = mDidiId.value,
                        id = (localAnswerList.size+1),
                        answerOption = answerOption,
                        answerValue = answerValue,
                        questionId = questionList.value[questionIndex].questionId?:0,
                        actionType = TYPE_EXCLUSION,
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
            val localAnswerList = answerDao.getAnswerForDidi(TYPE_EXCLUSION, didiId = didiId)
            _answerList.emit(localAnswerList)
              withContext(Dispatchers.IO){
                if(questionList.value.size>questionIndex){
                    val alreadyAnsweredModel= questionList.value[questionIndex].questionId?.let {
                        answerDao.isAlreadyAnswered(
                            it,
                            TYPE_EXCLUSION)
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


}