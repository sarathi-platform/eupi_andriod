package com.patsurvey.nudge.activities.survey

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QUESTION_FLAG_RATIO
import com.patsurvey.nudge.utils.QUESTION_FLAG_WEIGHT
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.TOTAL_FAMILY_MEMBERS_OPTION_VALUE
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.roundOffDecimal
import com.patsurvey.nudge.utils.updateStepStatus
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
    val repository: QuestionScreenRepository
) : BaseViewModel() {
    val totalAmount = mutableStateOf(0.0)
    val enteredAmount = mutableStateOf("")
    val isAnswerSelected = mutableStateOf(false)
    val nextCTAVisibility = mutableStateOf(true)
    private val _questionList = MutableStateFlow(listOf<QuestionEntity>())
    val questionList: StateFlow<List<QuestionEntity>> get() = _questionList

    private val _answerList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val answerList: StateFlow<List<SectionAnswerEntity>> get() = _answerList

    val didiName = mutableStateOf("DidiEntity()")
    val mDidiId = mutableStateOf(0)
    val nextButtonVisible= mutableStateOf(false)
    val prevButtonVisible= mutableStateOf(false)
    val isQuestionChange= mutableStateOf(false)
    val isClickEnable= mutableStateOf(false)
    val listTypeAnswerIndex = mutableStateOf(-1)
    val maxQuesCount = mutableStateOf(0)
    val isNextQuestionAnswered= mutableStateOf(false)
    val sectionType = mutableStateOf(TYPE_EXCLUSION)

    private val _selIndValue = MutableStateFlow<Int>(-1)
    val selIndValue: StateFlow<Int> get() = _selIndValue

    private val _totalAssetAmount = MutableStateFlow<Double>(0.0)
    val totalAssetAmount:StateFlow<Double> get() = _totalAssetAmount


    fun getAllQuestionsAnswers(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            NudgeLogger.d("QuestionScreenViewModel", "getAllQuestionsAnswers called")
            try {
                val questionList = repository.getQuestionForSection(sectionType = sectionType.value)
                val localAnswerList = repository.getSectionAnswersForDidi(
                    actionType = sectionType.value,
                    didiId = didiId
                )
                val localNumAnswerList = repository.getAllAnswerForDidi(didiId = didiId)

                try {
                    if (localNumAnswerList?.isNotEmpty() == true) {
                        questionList?.forEach { que ->
                            if (que.type == QuestionType.Numeric_Field.name) {
                                que.options.forEach { optionsItem ->
                                    val cIndex = localNumAnswerList.map { it.optionId }
                                        .indexOf(optionsItem.optionId)
                                    if (cIndex != -1) {
                                        if (localNumAnswerList[cIndex].optionId == optionsItem.optionId) {
                                            optionsItem.count = localNumAnswerList[cIndex].count
                                        }
                                    }

                                }

                                // Calculate Total Asset Amount
                                val aIndex = que.questionId?.let {
                                    localAnswerList?.map { it.questionId }
                                        ?.indexOf(it) ?: -1
                                }
                                if (aIndex != -1) {
                                    _totalAssetAmount.value =
                                        aIndex?.let { localAnswerList?.get(it)?.totalAssetAmount }
                                            ?: 0.0
                                }
                            }
                        }
                    }
                    questionList?.let {
                        _questionList.value = it
                    }
                    localAnswerList?.let {
                        _answerList.value = it
                    }

                    maxQuesCount.value = questionList?.size ?: 0
                    updateAnswerOptions(0, didiId)
                } catch (ex: Exception) {
                    NudgeLogger.e("QuestionScreenViewModel", "inner catch getAllQuestionsAnswers ->", ex)
                }

            } catch (ex: Exception) {
                NudgeLogger.e("QuestionScreenViewModel", "outer catch getAllQuestionsAnswers ->", ex)
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
                           answerList.value[aIndex].totalAssetAmount ?: 0.0
                   }
               }
           }
       }
    }

    fun setDidiDetails(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val didi = repository.getDidiFromDB(didiId = didiId)
            if(repository.prefRepo.questionScreenOpenFrom() == PageFrom.DIDI_LIST_PAGE.ordinal)
                updateDidiQuesSection(didiId, PatSurveyStatus.INPROGRESS.ordinal)
            withContext(Dispatchers.Main) {
                didiName.value = didi.name
                mDidiId.value = didi.id
            }
        }
    }

    fun updateDidiQuesSection(didiId: Int, status: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            repository.updateDidiQuestionSection(
                didiId = didiId,
                status = status,
                sectionType = sectionType.value
            )
        }
    }

    fun setAnswerToQuestion(
        didiId: Int,
        questionId: Int,
        answerOptionModel: OptionsItem,
        assetAmount: Double,
        enteredAssetAmount: String,
        quesType: String,
        summary: String,
        selIndex: Int,
        questionFlag:String,
        onAnswerSave: () -> Unit
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                if(repository.prefRepo.questionScreenOpenFrom() == PageFrom.NOT_AVAILABLE_STEP_COMPLETE_PAGE.ordinal) {
                    updateStepStatus(stepsListDao = repository.stepsListDao,
                        prefRepo = repository.prefRepo,
                        printTag = "QuestionScreenViewModel",
                        didiDao = repository.didiDao,
                        didiId = didiId)
                }
                repository.updateNeedToPostPAT(didiId=didiId)
                val alreadyAnsweredModel = repository.isAlreadyAnswered(
                    didiId = didiId,
                    questionId = questionId,
                    sectionType = sectionType.value
                )
                try {
                    if (alreadyAnsweredModel > 0) {
                        repository.updateDidiAnswer(
                            didiId = didiId,
                            questionId = questionId,
                            actionType = sectionType.value,
                            answerValue = answerOptionModel.display ?: BLANK_STRING,
                            optionValue = answerOptionModel.optionValue ?: 0,
                            optionId = answerOptionModel.optionId ?: 0,
                            weight = answerOptionModel.weight ?: 0,
                            type = quesType,
                            totalAssetAmount = assetAmount,
                            summary = summary,
                            assetAmount = enteredAssetAmount,
                            questionFlag = questionFlag
                        )
                        repository.updateAnswerNeedToPost(
                            didiId = didiId,
                            questionId = questionId,
                            needsToPost = true
                        )
                        repository.updateAllAnswerNeedToPost(didiId = didiId, needsToPost = true)
                        withContext(Dispatchers.Main) {
                            onAnswerSave()
                        }
                    } else {
                        repository.insertAnswer(
                            SectionAnswerEntity(
                                id = 0,
                                optionId = answerOptionModel.optionId ?: 0,
                                didiId = didiId,
                                optionValue = answerOptionModel.optionValue ?: 0,
                                answerValue = answerOptionModel.display ?: BLANK_STRING,
                                questionId = questionId,
                                actionType = sectionType.value,
                                totalAssetAmount = assetAmount,
                                type = quesType,
                                summary = summary,
                                villageId = repository.prefRepo.getSelectedVillage().id,
                                weight = answerOptionModel.weight ?: 0,
                                assetAmount = enteredAssetAmount,
                                questionFlag = questionFlag
                            )
                        )
                        repository.updateAllAnswerNeedToPost(didiId = didiId, needsToPost = true)
                        withContext(Dispatchers.Main) {
                            onAnswerSave()
                        }
                    }
                }catch (ex:Exception){
                    ex.printStackTrace()
                }

                val localAnswerList = repository.getSectionAnswersForDidi(
                    didiId = didiId,
                    actionType = sectionType.value
                )
                localAnswerList?.let {
                    _answerList.emit(it)
                }

//            }
            }
        }
    }

    fun updateNumericAnswer(
        numericAnswer: NumericAnswerEntity,
        index: Int,
        optionList: List<OptionsItem>,
        onUpdateTotalAmount:()->Unit
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                val optionDetails = repository.getAnswerOptionDetails(
                    optionId = numericAnswer.optionId,
                    questionId = numericAnswer.questionId,
                    didiId = numericAnswer.didiId
                )

                if (optionDetails != null) {
                    repository.updateNumericAnswer( numericAnswer.didiId,
                        numericAnswer.optionId,
                        numericAnswer.questionId,
                        numericAnswer.count,
                        numericAnswer.optionValue)
                } else {
                    repository.insertNumericAnswer(numericAnswer = numericAnswer)
                }
           val formattedOptionList= optionList.sortedBy { it.optionValue }.filter { it.optionType == BLANK_STRING }
                 if(numericAnswer.questionFlag.equals(QUESTION_FLAG_RATIO,true)){
                    val earningMemberCount=calculateCountWeight(formattedOptionList[1])
                    val totalMemberCount=calculateCountWeight(formattedOptionList[0])
                    if(earningMemberCount>0 && totalMemberCount>0){
                        totalAmount.value = roundOffDecimal(earningMemberCount/totalMemberCount)?:0.00
                        onUpdateTotalAmount()
                    }else {
                        totalAmount.value=0.00
                        onUpdateTotalAmount()
                    }
                }else{
                     val amountList = repository.getTotalAssetAmountFromDB(
                         questionId = numericAnswer.questionId,
                         didiId = numericAnswer.didiId
                     )
                    if(amountList.isNotEmpty() && amountList.size>0){
                        var amt=0
                        amountList.forEach {
                            amt += it
                        }
                        totalAmount.value = amt.toDouble()
                        onUpdateTotalAmount()
                    }
                }


            }
        }
    }
    fun calculateCountWeight(optionsItem: OptionsItem): Double {
        var countWeight = 0.0
        countWeight =
            if (optionsItem.optionValue == TOTAL_FAMILY_MEMBERS_OPTION_VALUE && optionsItem.count == 0
            ) {
                1.0
            } else {
                (optionsItem.count ?: 0).toDouble()
            }
        return countWeight
    }

    fun updateAnswerOptions(questionIndex: Int, didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val localAnswerList =
                repository.getSectionAnswersForDidi(actionType = sectionType.value, didiId = didiId)
            _answerList.emit(localAnswerList?: emptyList())
        }

    }
    fun findListTypeSelectedAnswer(quesIndex: Int, didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                try {
                 if(questionList.value.isNotEmpty()) {
                     Log.e(
                         "TAG",
                         "QuestionScreenIssue findListTypeSelectedAnswer: ${questionList.value}",
                     )
                     val answerCount = repository.isQuestionAnswered(
                         didiId = didiId,
                         questionId = questionList.value[quesIndex].questionId ?: 0
                     )
                     isClickEnable.value = answerCount > 0

                     val optionId = repository.fetchOptionIdFromDB(
                         didiId = didiId,
                         questionId = questionList.value[quesIndex].questionId ?: 0,
                         actionType = sectionType.value
                     )

                     if (optionId > 0) {
                         val index =
                             questionList.value[quesIndex].options.sortedBy { it.optionValue }
                                 .map { it.optionId }.indexOf(optionId)
                         listTypeAnswerIndex.value = index
                         _selIndValue.value = index
                         totalAmount.value = 0.0
                         enteredAmount.value = BLANK_STRING
                     } else if (optionId == 0 && (questionList.value[quesIndex].type == QuestionType.Numeric_Field.name)) {
                         nextCTAVisibility.value =
                             (quesIndex < questionList.value.size - 1 && quesIndex < answerList.value.size)
                         val totalDBAmount = repository.fetchTotalAmount(
                             questionList.value[quesIndex].questionId ?: 0, didiId
                         )
                         if (questionList.value[quesIndex].questionFlag.equals(
                                 QUESTION_FLAG_WEIGHT,
                                 true
                             )
                         ) {
                             totalAmount.value = totalDBAmount.toDouble()
                         } else {
                             val optionList =
                                 questionList.value[quesIndex].options.sortedBy { it.optionValue }
                                     .filter { it.optionType == BLANK_STRING }
                             optionList?.let { option ->
                                 val option1Count =
                                     option.filter { it.optionValue == 1 }[0].count?.toDouble()
                                         ?: 0.0
                                 val option2Count =
                                     option.filter { it.optionValue == 2 }[0].count?.toDouble()
                                         ?: 0.0
                                 if (option1Count > 0 && option2Count > 0) {
                                     totalAmount.value =
                                         roundOffDecimal(option2Count / option1Count) ?: 0.00
                                 } else totalAmount.value = 0.00
                             }
                         }
                         listTypeAnswerIndex.value = -1
                         _selIndValue.value = -1
                         enteredAmount.value =
                             "0" /*if(totalEnteredAmount.isNullOrEmpty()) BLANK_STRING else totalEnteredAmount.toString()*/
                     } else {
                         listTypeAnswerIndex.value = -1
                         _selIndValue.value = -1
                         totalAmount.value = 0.0
                         enteredAmount.value = BLANK_STRING
                     }

                 }
                } catch (ex: Exception) {

                    Log.e("TAG", "QuestionScreenIssue findListTypeSelectedAnswer: ${ex.message}")
                    ex.printStackTrace()
                }

            }
        }

    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }
}