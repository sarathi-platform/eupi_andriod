package com.patsurvey.nudge.activities.survey

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.dao.*
import com.patsurvey.nudge.model.dataModel.AnswerOptionModel
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QuestionType
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
class QuestionScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val didiDao: DidiDao,
    val villageListDao: VillageListDao,
    val questionListDao: QuestionListDao,
    val answerDao: AnswerDao,
    val apiService: ApiService,
    val numericAnswerDao: NumericAnswerDao
) : BaseViewModel() {
    val totalAmount = mutableStateOf(0)
    private val _questionList = MutableStateFlow(listOf<QuestionEntity>())
    val questionList: StateFlow<List<QuestionEntity>> get() = _questionList
    private val _optionList = MutableStateFlow(listOf<AnswerOptionModel>())

    val optionNumValueList: StateFlow<List<NumericAnswerEntity>> get() = _optionNumValueList
    private val _optionNumValueList = MutableStateFlow(listOf<NumericAnswerEntity>())
    val optionList: StateFlow<List<AnswerOptionModel>> get() = _optionList

    private val _answerList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val answerList: StateFlow<List<SectionAnswerEntity>> get() = _answerList

    val didiName = mutableStateOf("DidiEntity()")
    val mDidiId = mutableStateOf(0)

    val listTypeAnswerIndex = mutableStateOf(-1)
    val sectionType = mutableStateOf(TYPE_EXCLUSION)

    private val _selIndValue = MutableStateFlow<Int>(-1)
    val selIndValue: StateFlow<Int> get() = _selIndValue

    private val _totalAssetAmount = MutableStateFlow<Int>(-1)
    val totalAssetAmount:StateFlow<Int> get() = _totalAssetAmount


    fun getAllQuestionsAnswers(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val questionList = questionListDao.getQuestionForType(
                sectionType.value,
                prefRepo.getAppLanguageId() ?: 2
            )
            val localAnswerList = answerDao.getAnswerForDidi(sectionType.value, didiId = didiId)
            val localNumAnswerList = numericAnswerDao.getAllAnswersForDidi(didiId)
            withContext(Dispatchers.IO) {
                try {
                    if(localNumAnswerList.isNotEmpty()) {
                        questionList.forEach { que->
                            if (que.type == QuestionType.Numeric_Field.name) {
                                que.options.forEach { optionsItem ->
                                    val cIndex=localNumAnswerList.map { it.optionId }.indexOf(optionsItem.optionId)
                                    if(cIndex!=-1){
                                       if(localNumAnswerList[cIndex].optionId == optionsItem.optionId){
                                           optionsItem.count=localNumAnswerList[cIndex].count
                                       }
                                    }

                                }

                                // Calculate Total Asset Amount
                                val aIndex=localAnswerList.map { it.questionId }.indexOf(que.questionId)
                                if(aIndex!=-1) {
                                    _totalAssetAmount.value =
                                        localAnswerList[aIndex].totalAssetAmount ?: 0
                                }
                            }
                        }
                    }
                    _questionList.value = questionList
                    _answerList.value = localAnswerList
                    updateAnswerOptions(0, didiId)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }
        }
    }

    fun calculateTotalAmount(quesIndex:Int){
       job = CoroutineScope(Dispatchers.IO  + exceptionHandler).launch {
           withContext(Dispatchers.IO){
               if(questionList.value[quesIndex].type == QuestionType.Numeric_Field.name) {
                   val aIndex = answerList.value.map { it.questionId }
                       .indexOf(questionList.value[quesIndex].questionId)
                   if (aIndex != -1) {
                       _totalAssetAmount.value =
                           answerList.value[aIndex].totalAssetAmount ?: 0
                   }
               }
           }
       }
    }

    fun setDidiDetails(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didi = didiDao.getDidi(didiId)
            updateDidiQuesSection(didiId, PatSurveyStatus.INPROGRESS.ordinal)
            withContext(Dispatchers.Main) {
                didiName.value = didi.name
                mDidiId.value = didi.id
            }
        }
    }

    fun updateDidiQuesSection(didiId: Int, status: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            didiDao.updateQuesSectionStatus(didiId, status)
            if (sectionType.value.equals(TYPE_EXCLUSION, true)) {
                didiDao.updatePatSection1Status(didiId, status)
            } else didiDao.updatePatSection2Status(didiId, status)

        }
    }

    fun setAnswerToQuestion(
        didiId: Int, questionId: Int, answerOptionModel: OptionsItem,
        assetAmount: Int, quesType: String, summary: String, selIndex: Int,
        onAnswerSave: () -> Unit
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            withContext(Dispatchers.IO) {

                val alreadyAnsweredModel = answerDao.isAlreadyAnswered(
                    didiId = didiId,
                    questionId = questionId,
                    actionType = sectionType.value
                )
                if (alreadyAnsweredModel != null) {
                    answerDao.updateAnswer(
                        didiId = didiId, questionId = questionId,
                        actionType = sectionType.value,
                        answerValue = answerOptionModel.display?: BLANK_STRING,
                        optionValue = answerOptionModel.optionValue ?: 0,
                        optionId = answerOptionModel.optionId?:0,
                        weight = answerOptionModel.weight ?: 0,
                        type = quesType,
                        totalAssetAmount = assetAmount,
                        summary = summary,
                        selectedIndex = selIndex + 1
                    )
                    withContext(Dispatchers.Main) {
                        onAnswerSave()
                    }
                } else {
                    answerDao.insertAnswer(
                        SectionAnswerEntity(
                            id = 0, optionId = answerOptionModel.optionId?:0,
                            didiId = didiId,
                            optionValue = answerOptionModel.optionValue ?: 0,
                            answerValue = answerOptionModel.display?: BLANK_STRING,
                            questionId = questionId,
                            actionType = sectionType.value,
                            totalAssetAmount = assetAmount,
                            type = quesType,
                            summary = summary,
                            selectedIndex = selIndex + 1,
                            villageId = prefRepo.getSelectedVillage().id
                        )
                    )
                    withContext(Dispatchers.Main) {
                        onAnswerSave()
                    }
                }
                val localAnswerList = answerDao.getAnswerForDidi(sectionType.value, didiId = didiId)
                _answerList.emit(localAnswerList)
//            }
            }
        }
    }

    fun updateNumericAnswer(numericAnswer: NumericAnswerEntity) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                val optionDetails = numericAnswerDao.getOptionDetails(
                    numericAnswer.optionId,
                    numericAnswer.questionId, numericAnswer.didiId
                )
                if (optionDetails != null) {
                    numericAnswerDao.updateAnswer(
                        numericAnswer.didiId,
                        numericAnswer.optionId,
                        numericAnswer.questionId,
                        numericAnswer.count
                    )
                } else {
                    numericAnswerDao.insertNumericOption(numericAnswer)
                }

                val amountList = numericAnswerDao.getTotalAssetAmount(numericAnswer.questionId,numericAnswer.didiId)
                if(amountList.isNotEmpty() && amountList.size>0){
                    var amt=0
                    amountList.forEach {
                        amt += it
                    }
                    totalAmount.value = amt
                }

            }
        }
    }

    fun updateAnswerOptions(questionIndex: Int, didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localAnswerList = answerDao.getAnswerForDidi(sectionType.value, didiId = didiId)
            _answerList.emit(localAnswerList)
            withContext(Dispatchers.IO) {
                if (questionList.value.size > questionIndex) {
                    val alreadyAnsweredModel = questionList.value[questionIndex].questionId?.let {
                        answerDao.isAlreadyAnswered(
                            didiId = didiId,
                            questionId = it,
                            sectionType.value
                        )
                    }
                }
            }
        }

    }

    fun findListTypeSelectedAnswer(quesIndex: Int, didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                 val optionId = answerDao.fetchOptionID(
                    didiId,
                    questionList.value[quesIndex].questionId ?: 0,
                    sectionType.value
                )
                if(optionId>0){
                    val index = questionList.value[quesIndex].options.map { it.optionId }.indexOf(optionId)
                    listTypeAnswerIndex.value = index
                    _selIndValue.value = index
                    totalAmount.value =0
                } else if(optionId == 0 && (questionList.value[quesIndex].type == QuestionType.Numeric_Field.name)){

                    val answer=answerDao.getNumTypeAnswer(didiId,questionList.value[quesIndex].questionId?:0,QuestionType.Numeric_Field.name)
                    totalAmount.value =  answer.totalAssetAmount?:0
                    listTypeAnswerIndex.value = -1
                    _selIndValue.value = -1
                } else{
                    listTypeAnswerIndex.value = -1
                    _selIndValue.value = -1
                    totalAmount.value = 0
                }

            }

        }

    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }
}