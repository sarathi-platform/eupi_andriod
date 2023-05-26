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
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.dataModel.AnswerOptionModel
import com.patsurvey.nudge.model.response.OptionsItem
import com.patsurvey.nudge.network.interfaces.ApiService
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
    val apiService: ApiService,
    val numericAnswerDao: NumericAnswerDao
) : BaseViewModel() {

    private val _questionList = MutableStateFlow(listOf<QuestionEntity>())
    val questionList: StateFlow<List<QuestionEntity>> get() = _questionList
    private val _optionList = MutableStateFlow(listOf<AnswerOptionModel>())
    val optionList: StateFlow<List<AnswerOptionModel>> get() = _optionList

    private val _answerList = MutableStateFlow(listOf<SectionAnswerEntity>())
    val answerList: StateFlow<List<SectionAnswerEntity>> get() = _answerList

    val didiName = mutableStateOf("DidiEntity()")
    val mDidiId = mutableStateOf(0)
    val totalAssetAmount = mutableStateOf(0)
    val listTypeAnswerIndex = mutableStateOf(-1)
    val sectionType = mutableStateOf(TYPE_EXCLUSION)

    private val _selIndValue = MutableStateFlow<Int>(-1)
    val selIndValue: StateFlow<Int> get() = _selIndValue


    fun getAllQuestionsAnswers(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val questionList = questionListDao.getQuestionForType(
                sectionType.value,
                prefRepo.getAppLanguageId() ?: 2
            )
            val localAnswerList = answerDao.getAnswerForDidi(sectionType.value, didiId = didiId)
            withContext(Dispatchers.IO) {
                try {
                    _questionList.emit(questionList)
                    _answerList.emit(localAnswerList)
                    updateAnswerOptions(0, didiId)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }
        }
    }

    fun findQuestionOptionList(questionIndex: Int, didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val updatedList = mutableListOf<AnswerOptionModel>()
            withContext(Dispatchers.IO) {
                try {
                    val quesId = questionList.value[questionIndex].questionId
                    questionList.value[questionIndex].options?.forEach {
                        val count = answerDao.countOfOptionId(
                            didiId,
                            quesId ?: 0,
                            sectionType.value,
                            it?.optionId ?: 0
                        )
                        var isSelected = false
                        if (count > 0) {
                            isSelected = true
                        }
                        updatedList.add(
                            AnswerOptionModel(
                                it?.optionId ?: 0,
                                it?.display ?: BLANK_STRING,
                                isSelected,
                                it?.weight,
                                it?.summary,
                                it?.optionValue
                            )
                        )
                    }
                    Log.d(
                        "TAG",
                        "findQuestionOptionList ${questionIndex} Option: ${Gson().toJson(updatedList)}"
                    )
                    _optionList.emit(updatedList)
                } catch (ex: Exception) {
                    ex.printStackTrace()
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
                didiDao.updatePatSection1Status(didiId, 1)
            } else didiDao.updatePatSection2Status(didiId, 1)

        }
    }

    fun setAnswerToQuestion(
        didiId: Int, questionId: Int, answerOptionModel: OptionsItem,
        assetAmount: Int, quesType: String, summary: String, selIndex: Int, onAnswerSave: () -> Unit
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            withContext(Dispatchers.IO) {

                val alreadyAnsweredModel = answerDao.isAlreadyAnswered(
                    didiId = didiId,
                    questionId = questionId, actionType = sectionType.value
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
                            selectedIndex = selIndex + 1
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

                val listOfAmount = numericAnswerDao.getTotalAssetAmount()
                totalAssetAmount.value = 0
                listOfAmount.forEach {
                    totalAssetAmount.value = totalAssetAmount.value + it
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
                }else{
                    listTypeAnswerIndex.value = -1
                    _selIndValue.value = -1
                }

            }

        }

    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }
}