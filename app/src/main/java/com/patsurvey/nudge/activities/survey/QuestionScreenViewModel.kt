package com.patsurvey.nudge.activities.survey

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcNonSelectedDidiDao
import com.patsurvey.nudge.database.dao.BpcSelectedDidiDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.QUESTION_FLAG_RATIO
import com.patsurvey.nudge.utils.QUESTION_FLAG_WEIGHT
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.roundOffDecimal
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
    val numericAnswerDao: NumericAnswerDao,
    val bpcSelectedDidiDao: BpcSelectedDidiDao,
    val bpcNonSelectedDidiDao: BpcNonSelectedDidiDao
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
                val questionList = questionListDao.getQuestionForType(
                    sectionType.value,
                    prefRepo.getAppLanguageId() ?: 2
                )
                val localAnswerList = answerDao.getAnswerForDidi(sectionType.value, didiId = didiId)
                val localNumAnswerList = numericAnswerDao.getAllAnswersForDidi(didiId)

                try {
                    if (localNumAnswerList.isNotEmpty()) {
                        questionList.forEach { que ->
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
                                val aIndex = localAnswerList.map { it.questionId }
                                    .indexOf(que.questionId)
                                if (aIndex != -1) {
                                    _totalAssetAmount.value =
                                        localAnswerList[aIndex].totalAssetAmount ?: 0.0
                                }
                            }
                        }
                    }
                    _questionList.value = questionList
                    _answerList.value = localAnswerList
                    maxQuesCount.value = questionList.size
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
            val didi = didiDao.getDidi(didiId)
            if(prefRepo.questionScreenOpenFrom() == PageFrom.DIDI_LIST_PAGE.ordinal)
                updateDidiQuesSection(didiId, PatSurveyStatus.INPROGRESS.ordinal)
            withContext(Dispatchers.Main) {
                didiName.value = didi.name
                mDidiId.value = didi.id
            }
        }
    }

    fun updateDidiQuesSection(didiId: Int, status: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            if(prefRepo.isUserBPC()){
                didiDao.updateQuesSectionStatus(didiId, status)
                if (sectionType.value.equals(TYPE_EXCLUSION, true)) {
                    didiDao.updatePatSection1Status(didiId, status)
                } else didiDao.updatePatSection2Status(didiId, status)

                val selectedDidi = bpcSelectedDidiDao.fetchSelectedDidi(didiId)
                selectedDidi?.let {
                    bpcSelectedDidiDao.updateQuesSectionStatus(didiId, status)
                    if (sectionType.value.equals(TYPE_EXCLUSION, true)) {
                        bpcSelectedDidiDao.updateSelDidiPatSection1Status(didiId,status)
                    } else {
                        bpcSelectedDidiDao.updateSelDidiPatSection2Status(didiId,status)
                    }
                }
                val nonSelectedDidi = bpcNonSelectedDidiDao.getNonSelectedDidi(didiId)
                nonSelectedDidi?.let {
                    bpcNonSelectedDidiDao.updateQuesSectionStatus(didiId, status)
                    if (sectionType.value.equals(TYPE_EXCLUSION, true)) {
                        bpcNonSelectedDidiDao.updateNonSelDidiPatSection1Status(didiId,status)
                    } else{
                        bpcNonSelectedDidiDao.updateNonSelDidiPatSection2Status(didiId, status)
                    }
                }
            } else {
                didiDao.updateQuesSectionStatus(didiId, status)
                if (sectionType.value.equals(TYPE_EXCLUSION, true)) {
                    didiDao.updatePatSection1Status(didiId, status)
                } else didiDao.updatePatSection2Status(didiId, status)
            }
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
                didiDao.updateNeedToPostPAT(true,didiId, villageId = prefRepo.getSelectedVillage().id)
                val alreadyAnsweredModel = answerDao.isAlreadyAnswered(
                    didiId = didiId,
                    questionId = questionId,
                    actionType = sectionType.value
                )
                try {
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
                            assetAmount = enteredAssetAmount,
                            questionFlag = questionFlag
                        )
                        answerDao.updateNeedToPost(didiId, questionId, true)
                        answerDao.updateAllAnswersNeedToPost(didiId, true)
                        withContext(Dispatchers.Main) {
                            onAnswerSave()
                        }
                    } else {
                        answerDao.insertAnswer(
                            SectionAnswerEntity(
                                id = 0,
                                optionId = answerOptionModel.optionId?:0,
                                didiId = didiId,
                                optionValue = answerOptionModel.optionValue ?: 0,
                                answerValue = answerOptionModel.display?: BLANK_STRING,
                                questionId = questionId,
                                actionType = sectionType.value,
                                totalAssetAmount = assetAmount,
                                type = quesType,
                                summary = summary,
                                villageId = prefRepo.getSelectedVillage().id,
                                weight=answerOptionModel.weight ?: 0,
                                assetAmount = enteredAssetAmount,
                                questionFlag = questionFlag
                            )
                        )
                        answerDao.updateAllAnswersNeedToPost(didiId, true)
                        withContext(Dispatchers.Main) {
                            onAnswerSave()
                        }
                    }
                }catch (ex:Exception){
                    ex.printStackTrace()
                }

                val localAnswerList = answerDao.getAnswerForDidi(sectionType.value, didiId = didiId)
                _answerList.emit(localAnswerList)
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
                val optionDetails = numericAnswerDao.getOptionDetails(
                    numericAnswer.optionId,
                    numericAnswer.questionId, numericAnswer.didiId
                )
                if (optionDetails != null) {
                    numericAnswerDao.updateAnswer(
                        numericAnswer.didiId,
                        numericAnswer.optionId,
                        numericAnswer.questionId,
                        numericAnswer.count,
                        numericAnswer.optionValue
                    )
                } else {
                    numericAnswerDao.insertNumericOption(numericAnswer)
                }
                 if(numericAnswer.questionFlag.equals(QUESTION_FLAG_RATIO,true)){
                    val earningMemberCount=calculateCountWeight(optionList[1])
                    val totalMemberCount=calculateCountWeight(optionList[0])
                    if(earningMemberCount>0 && totalMemberCount>0){
                        totalAmount.value = roundOffDecimal(earningMemberCount/totalMemberCount)?:0.00
                        onUpdateTotalAmount()
                    }else {
                        totalAmount.value=0.00
                        onUpdateTotalAmount()
                    }
                }else{
                    val amountList = numericAnswerDao.getTotalAssetAmount(numericAnswer.questionId,numericAnswer.didiId)
                    if(amountList.isNotEmpty() && amountList.size>0){
                        var amt=0
                        amountList.forEach {
                            amt += it
                        }
                        totalAmount.value = amt.toDouble()
                        onUpdateTotalAmount()
                    }
                     Log.d("TAG", "updateNumericAnswer totalAmount: ${totalAmount.value}")
                }


            }
        }
    }
    fun calculateCountWeight(optionsItem: OptionsItem): Double {
        return (optionsItem.count?:0)/*.times(optionsItem.weight?:0)*/.toDouble()
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

                val answerCount= answerDao.isQuestionAnswered(didiId,questionList.value[quesIndex].questionId?:0)
                isClickEnable.value = answerCount>0
                val optionId = answerDao.fetchOptionID(
                    didiId,
                    questionList.value[quesIndex].questionId ?: 0,
                    sectionType.value
                )
                if(optionId>0){
                    val index = questionList.value[quesIndex].options.map { it.optionId }.indexOf(optionId)
                    listTypeAnswerIndex.value = index
                    _selIndValue.value = index
                    totalAmount.value =0.0
                    enteredAmount.value= BLANK_STRING
                } else if(optionId == 0 && (questionList.value[quesIndex].type == QuestionType.Numeric_Field.name)){
                    nextCTAVisibility.value=(quesIndex < questionList.value.size - 1 && quesIndex< answerList.value.size)
                    val totalDBAmount= numericAnswerDao.fetchTotalAmount(questionList.value[quesIndex].questionId?:0,didiId)
                   if( questionList.value[quesIndex].questionFlag.equals(QUESTION_FLAG_WEIGHT,true)){
                       totalAmount.value =  totalDBAmount.toDouble()
                   }else{
                       val optionList = questionList.value[quesIndex].options
                       optionList?.let {option->
                           val option1Count = option.filter { it.optionValue==1 }[0].count?.toDouble() ?: 0.0
                           val option2Count = option.filter { it.optionValue==2 }[0].count?.toDouble() ?: 0.0
                           if(option1Count > 0 && option2Count > 0){
                               totalAmount.value = roundOffDecimal(option2Count/option1Count)?:0.00
                           }else  totalAmount.value=0.00
                       }
                   }
                    listTypeAnswerIndex.value = -1
                    _selIndValue.value = -1
                    enteredAmount.value="0" /*if(totalEnteredAmount.isNullOrEmpty()) BLANK_STRING else totalEnteredAmount.toString()*/
                } else{
                    listTypeAnswerIndex.value = -1
                    _selIndValue.value = -1
                    totalAmount.value = 0.0
                    enteredAmount.value= BLANK_STRING
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