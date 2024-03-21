package com.nrlm.baselinesurvey.ui.question_type_screen.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_LANGUAGE_ID
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.datamodel.ConditionsDto
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.Constants.ResultType
import com.nrlm.baselinesurvey.ui.common_components.common_events.EventWriterEvents
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.entity.FormTypeOption
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case.FormQuestionScreenUseCase
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.calculateResultForFormula
import com.nrlm.baselinesurvey.utils.checkCondition
import com.nrlm.baselinesurvey.utils.convertFormQuestionResponseEntityToSaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.utils.convertFormTypeQuestionListToOptionItemEntity
import com.nrlm.baselinesurvey.utils.convertQuestionListToOptionItemEntity
import com.nrlm.baselinesurvey.utils.convertToOptionItemEntity
import com.nrlm.baselinesurvey.utils.findIndexOfListByOptionId
import com.nrlm.baselinesurvey.utils.findOptionExist
import com.nrlm.baselinesurvey.utils.getResponseForOptionId
import com.nrlm.baselinesurvey.utils.isNumeric
import com.nrlm.baselinesurvey.utils.json
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nudge.core.enums.EventType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class QuestionTypeScreenViewModel @Inject constructor(
    private val formQuestionScreenUseCase: FormQuestionScreenUseCase,
    private val eventWriterHelperImpl: EventWriterHelperImpl
) : BaseViewModel() {

    private val TAG = QuestionTypeScreenViewModel::class.java.simpleName

    private val _loaderState = mutableStateOf(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState
    val optionList: State<List<OptionItemEntity>> get() = _optionList
    private val _optionList = mutableStateOf<List<OptionItemEntity>>(emptyList())

    var referenceId: String = UUID.randomUUID().toString()

    private val _formQuestionResponseEntity =
        mutableStateOf<List<FormQuestionResponseEntity>>(emptyList())

    val formQuestionResponseEntity: State<List<FormQuestionResponseEntity>> get() = _formQuestionResponseEntity

    private val _storeCacheForResponse = mutableListOf<FormQuestionResponseEntity>()
    val storeCacheForResponse: List<FormQuestionResponseEntity> get() = _storeCacheForResponse


    var formTypeOption = FormTypeOption.getEmptyOptionItem()

    private var _updatedOptionList = mutableStateListOf<OptionItemEntityState>()
    val updatedOptionList: SnapshotStateList<OptionItemEntityState> get() = _updatedOptionList

    val totalOptionSize = mutableIntStateOf(0)
    val answeredOptionCount = mutableIntStateOf(0)

    var question: QuestionEntity? = null

    private var didiId = -1

    val calculatedResult = mutableStateOf("")

//    val calculationResult = mutableStateOf()

    fun init(
        sectionId: Int,
        surveyId: Int,
        questionId: Int,
        surveyeeId: Int,
        referenceId: String = BLANK_STRING
    ) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        didiId = surveyeeId
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            question =
                formQuestionScreenUseCase.getFormQuestionResponseUseCase.getFormQuestionForId(
                    surveyId,
                    sectionId,
                    questionId
                )
            _optionList.value =
                formQuestionScreenUseCase.getFormQuestionResponseUseCase.invoke(
                    surveyId,
                    sectionId,
                    questionId
                )

            BaselineLogger.d(TAG, "init: referenceId: ${this@QuestionTypeScreenViewModel.referenceId}")
            if (referenceId.isNotBlank()) {
                this@QuestionTypeScreenViewModel.referenceId = referenceId
                BaselineLogger.d(
                    TAG,
                    "init: referenceId after update: ${this@QuestionTypeScreenViewModel.referenceId}"
                )
                _formQuestionResponseEntity.value =
                    getFormResponseForReferenceId(referenceId = referenceId)
            }

            getOptionItemEntityState(
                surveyId = surveyId,
                didiId = surveyeeId,
                sectionId = sectionId,
                questionId = questionId
            )

            delay(100)

            totalOptionSize.intValue = updatedOptionList.filter { it.showQuestion }.size
            if (referenceId.isNotBlank()) {
                calculatedResult.value =
                    formQuestionResponseEntity.value.find { it.optionId == updatedOptionList.find { it.optionItemEntity?.optionType == QuestionType.Calculation.name }?.optionId }?.selectedValue
                        ?: BLANK_STRING
            }

            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private fun getOptionItemEntityState(surveyId: Int, didiId: Int, sectionId: Int, questionId: Int) {
        formTypeOption = FormTypeOption.getOptionItem(surveyId = surveyId, didiId = didiId, sectionId = sectionId, questionId = questionId, optionItems = optionList.value)
        formTypeOption?.options?.forEach { optionItemEntity ->
            _updatedOptionList.add(
                OptionItemEntityState(
                optionId = optionItemEntity.optionId,
                optionItemEntity = optionItemEntity,
                showQuestion = true)
            )
            optionItemEntity.conditions?.forEach { conditionsDto ->
                when (conditionsDto?.resultType) {
                    ResultType.Questions.name -> {
                        conditionsDto?.resultList?.forEach { questionList ->
                            if (questionList.type?.equals(QuestionType.Form.name, true) == true) {
                                val mOptionItemEntityList = questionList.convertFormTypeQuestionListToOptionItemEntity(optionItemEntity.sectionId, optionItemEntity.surveyId, optionItemEntity.languageId ?: DEFAULT_LANGUAGE_ID)
                                mOptionItemEntityList.forEach { mOptionItemEntity ->
                                    _updatedOptionList.add(
                                        OptionItemEntityState(
                                            mOptionItemEntity.optionId,
                                            mOptionItemEntity,
                                            false
                                        )
                                    )
                                }
                            }
                            val mOptionItemEntity = questionList.convertQuestionListToOptionItemEntity(
                                optionItemEntity.sectionId,
                                optionItemEntity.surveyId
                            )
                            _updatedOptionList.add(
                                OptionItemEntityState(
                                    mOptionItemEntity.optionId,
                                    mOptionItemEntity,
                                    false
                                )
                            )

                            // TODO Handle later correctly
                            mOptionItemEntity.conditions?.forEach { conditionsDto2 ->
                                if (conditionsDto2?.resultType.equals(ResultType.Questions.name, true)) {
                                    conditionsDto2?.resultList?.forEach { subQuestionList ->
                                        val mOptionItemEntity2 = subQuestionList.convertQuestionListToOptionItemEntity(
                                            mOptionItemEntity.sectionId,
                                            mOptionItemEntity.surveyId
                                        )
                                        _updatedOptionList.add(
                                            OptionItemEntityState(
                                                mOptionItemEntity2.optionId,
                                                mOptionItemEntity2,
                                                false
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    ResultType.Options.name -> {
                        conditionsDto?.resultList?.forEach { questionList ->
                            val mOptionItemEntity =
                                questionList.convertToOptionItemEntity(
                                    sectionId,
                                    surveyId,
                                    questionId,
                                    languageId = optionItemEntity.languageId ?: DEFAULT_LANGUAGE_ID
                                )
                            _updatedOptionList.add(
                                OptionItemEntityState(
                                    mOptionItemEntity.optionId,
                                    mOptionItemEntity,
                                    false
                                )
                            )
                        }
                    }
                    ResultType.Formula.name -> {

                    }
                }
            }
        }

        updateAnsweredConditionalQuestion()

    }

    //TODO Handle Update for answered question seperate index search and list update logic.
    private fun updateAnsweredConditionalQuestion() {
        val tempList = updatedOptionList.toList()
        val tempFormQuestionResponseEntityList = formQuestionResponseEntity.value
        try {
            tempFormQuestionResponseEntityList.distinctBy { it.optionId }.forEach { formQuestionResponseEntity ->
                Log.d(TAG, "updateAnsweredConditionalQuestion: formQuestionResponseEntity -> $formQuestionResponseEntity")
                tempList.forEach { optionItemState ->
                    if (optionItemState.optionId == formQuestionResponseEntity.optionId) {
                        var optionToUpdate = tempList.find { it.optionId == optionItemState.optionId }

                        optionToUpdate = optionToUpdate?.copy(showQuestion = true)

                        val optionToUpdateIndex = tempList.findIndexOfListByOptionId(optionToUpdate?.optionId)
                        if (optionToUpdateIndex != -1) {
                            _updatedOptionList.removeAt(optionToUpdateIndex)
                            _updatedOptionList.add(optionToUpdateIndex, optionToUpdate!!)
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "updateAnsweredConditionalQuestion: exception -> ${ex.localizedMessage}", ex)
        }
    }

    private suspend fun getFormResponseForReferenceId(referenceId: String): List<FormQuestionResponseEntity> {
        return formQuestionScreenUseCase.getFormQuestionResponseUseCase.getFormResponseForReferenceId(referenceId)
    }

    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

            is QuestionTypeEvent.SaveFormQuestionResponseEvent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val formQuestionResponseForQuestionOption = formQuestionScreenUseCase.getFormQuestionResponseUseCase.getFormResponsesForQuestionOption(
                        surveyId = event.formQuestionResponseEntity.surveyId,
                        sectionId = event.formQuestionResponseEntity.sectionId,
                        questionId = event.formQuestionResponseEntity.questionId,
                        referenceId = event.formQuestionResponseEntity.referenceId,
                        optionId = event.formQuestionResponseEntity.optionId,
                        didiId = event.formQuestionResponseEntity.didiId
                    )
                    if (formQuestionResponseForQuestionOption.any { it.optionId == event.formQuestionResponseEntity.optionId }) {
                        formQuestionScreenUseCase.updateFormQuestionResponseUseCase.invoke(
                            event.formQuestionResponseEntity.surveyId,
                            event.formQuestionResponseEntity.sectionId,
                            event.formQuestionResponseEntity.questionId,
                            event.formQuestionResponseEntity.optionId,
                            event.formQuestionResponseEntity.selectedValue,
                            event.formQuestionResponseEntity.referenceId,
                            event.formQuestionResponseEntity.didiId
                        )
                    } else {
                        formQuestionScreenUseCase.saveFormQuestionResponseUseCase.invoke(event.formQuestionResponseEntity)
                    }
                }
            }

            is QuestionTypeEvent.DeleteFormQuestionOptionResponseEvent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        if (event.optionId != null && event.questionId != null && event.sectionId != null && event.surveyId != null && event.surveyeeId != null && event.surveyeeId != -1) {
                            formQuestionScreenUseCase.deleteFormQuestionOptionResponseUseCase.invoke(
                                optionId = event.optionId,
                                questionId = event.questionId,
                                sectionId = event.sectionId,
                                surveyId = event.surveyId,
                                surveyeeId = event.surveyeeId
                            )
                        } else {
                            throw NullPointerException((event as QuestionTypeEvent.DeleteFormQuestionOptionResponseEvent).toString())
                        }
                    } catch (ex: Exception) {
                        BaselineLogger.e(TAG, "onEvent -> QuestionTypeEvent.DeleteFormQuestionOptionResponseEvent -> null pointer exception", ex)
                    }
                }
            }

            is QuestionTypeEvent.UpdateConditionalOptionState -> {
                if (event.userInputValue != BLANK_STRING) {
                    event.optionItemEntityState?.optionItemEntity?.conditions?.forEach { conditionsDto ->
                        val conditionCheckResult = conditionsDto?.checkCondition(event.userInputValue)
                        updateQuestionStateForCondition(conditionCheckResult == true, conditionsDto)
                    }
                } else {
                    event.optionItemEntityState?.optionItemEntity?.conditions?.forEach { conditionsDto ->
                        updateQuestionStateForCondition(false, conditionsDto)
                    }
                }
                /*totalOptionSize.intValue = updatedOptionList.filter { it.showQuestion }.size
                if (answeredOptionCount.intValue > totalOptionSize.intValue) {
                    answeredOptionCount.intValue = totalOptionSize.intValue
                }*/
            }

            is QuestionTypeEvent.UpdateCalculationTypeQuestionValue -> {
                val optionList = updatedOptionList.toList()
                if (optionList.any { it.optionItemEntity?.optionType == QuestionType.Calculation.name }) {
                    val calculationOption =
                        optionList.find { it.optionItemEntity?.optionType == QuestionType.Calculation.name }
                    calculationOption?.optionItemEntity?.conditions?.forEach { conditionDto ->
                        val optionIds = mutableListOf<Int>()
                        conditionDto?.value?.split(" ")?.filter { it != "" }?.forEach { va ->
                            if (va.isNotEmpty() && isNumeric(va)) {
                                optionIds.add(va.toInt())
                            }
                        }
                        var areAllValuesPresent = 0
                        if (optionIds.isNotEmpty()) {
                            optionIds.forEach { option ->
                                val findOPtion = storeCacheForResponse.findOptionExist(option)
                                if (findOPtion == true){
                                    areAllValuesPresent ++
                                }
                            }
                            if (areAllValuesPresent == optionIds.size) {
                                val result =
                                    conditionDto?.calculateResultForFormula(storeCacheForResponse)
                                calculatedResult.value = result ?: BLANK_STRING
                            }
                        }
                    }
                }

            }

            is QuestionTypeEvent.SaveCacheFormQuestionResponseToDbEvent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val finalFormQuestionResponseList =
                        event.formQuestionResponseList.toMutableList()
                    val unchangedValues = mutableListOf<FormQuestionResponseEntity>()
                    formQuestionResponseEntity.value.forEach { formQuestionResponseEntity ->
                        finalFormQuestionResponseList.forEach { finalFormQuestionResponseItem ->
                            if (formQuestionResponseEntity.optionId != finalFormQuestionResponseItem.optionId)
                                unchangedValues.add(formQuestionResponseEntity)
                        }
                    }
                    finalFormQuestionResponseList.addAll(unchangedValues)

                    updatedOptionList.forEach {
                        if (it.optionItemEntity?.optionType?.equals(QuestionType.Calculation.name, true) == true) {
                            it.optionItemEntity?.conditions?.forEach { conditionDto ->
                                val resultedValue = conditionDto?.calculateResultForFormula(finalFormQuestionResponseList)
                                if (!resultedValue.isNullOrBlank()) {
                                    finalFormQuestionResponseList.add(
                                        FormQuestionResponseEntity(
                                            id = 0,
                                            didiId = didiId,
                                            questionId = event.questionId,
                                            surveyId = event.surveyId,
                                            sectionId = event.sectionId,
                                            referenceId = referenceId,
                                            optionId = it.optionId ?: -1,
                                            selectedValue = resultedValue
                                        )
                                    )
                                }
                            }
                        }
                    }
                    finalFormQuestionResponseList.forEach {
                        val existingFormQuestionResponseEntity =
                            formQuestionScreenUseCase.saveFormQuestionResponseUseCase.getOptionItem(
                                it
                            )
                        if (existingFormQuestionResponseEntity > 0) {
                            formQuestionScreenUseCase.saveFormQuestionResponseUseCase.updateFromListItemIntoDb(
                                it
                            )
                        } else {
                            formQuestionScreenUseCase.saveFormQuestionResponseUseCase.saveFormsListIntoDB(
                                finalFormQuestionResponseList
                            )
                        }
                    }
                    val completeOptionListForQuestion =
                        formQuestionScreenUseCase.getFormQuestionResponseUseCase
                            .getFormResponsesForQuestion(
                                event.surveyId,
                                event.sectionId,
                                event.questionId,
                                event.subjectId
                            )
                    onEvent(
                        EventWriterEvents.SaveAnswerEvent(
                            surveyId = finalFormQuestionResponseList.first().surveyId,
                            sectionId = finalFormQuestionResponseList.first().sectionId,
                            didiId = didiId,
                            questionId = finalFormQuestionResponseList.first().questionId,
                            questionType = QuestionType.Form.name,
                            questionTag = question?.tag ?: -1,
                            saveAnswerEventOptionItemDtoList = completeOptionListForQuestion
                                .convertFormQuestionResponseEntityToSaveAnswerEventOptionItemDto(
                                    QuestionType.Form
                                )
                        )
                    )
                }
            }

            is QuestionTypeEvent.CacheFormQuestionResponseEvent -> {
                val form = storeCacheForResponse
                    .find { it.optionId == event.formQuestionResponseEntity.optionId }
                if (form == null) {
                    _storeCacheForResponse.add(event.formQuestionResponseEntity)
                } else {
                    form.selectedValue = event.formQuestionResponseEntity.selectedValue
                    val index = storeCacheForResponse.map { it.optionId }.indexOf(form.optionId)
                        .coerceIn(0, storeCacheForResponse.size)

                    _storeCacheForResponse.removeAt(index)
                    _storeCacheForResponse.add(index = index, form)
                }
                updateCachedData()
            }

            is EventWriterEvents.SaveAnswerEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val saveAnswerEvent =
                        eventWriterHelperImpl.createSaveAnswerEventForFormTypeQuestion(
                            surveyId = event.surveyId,
                            sectionId = event.sectionId,
                            didiId = event.didiId,
                            questionId = event.questionId,
                            questionType = event.questionType,
                            questionTag = event.questionTag,
                            saveAnswerEventOptionItemDtoList = event.saveAnswerEventOptionItemDtoList
                        )
                    formQuestionScreenUseCase.eventsWriterUserCase.invoke(
                        events = saveAnswerEvent,
                        eventType = EventType.STATEFUL
                    )
                }
            }
        }
    }

    override fun updateQuestionStateForCondition(conditionResult: Boolean, conditionsDto: ConditionsDto?) {
        when (conditionsDto?.resultType) {
            ResultType.Questions.name -> {
                conditionsDto?.resultList?.forEach { questionList ->
                    if (questionList.type?.equals(QuestionType.Form.name, true) == true) {
                        val tempList = _updatedOptionList.distinctBy { it.optionId }

                        questionList.options?.forEach { optItem ->
                            val questionsToShow = tempList.find { optionItemEntityState ->  optionItemEntityState.optionId == optItem?.optionId }

                            updateQuestionsToShow(questionsToShow, conditionResult)
                        }
                    } else {
                        val tempList = _updatedOptionList.distinctBy { it.optionId }
                        val questionsToShow = tempList.find { it.optionId == questionList.questionId }

                        updateQuestionsToShow(questionsToShow, conditionResult)

                    }
                }
            }
            ResultType.Options.name -> {
                conditionsDto.resultList.forEach { questionList ->
                    val tempList = _updatedOptionList.distinctBy { it.optionId }
                    val optionToShow = tempList.find { it.optionId == questionList.questionId }

                    updateQuestionsToShow(optionToShow, conditionResult)

                }

            }
        }

    }

    private fun updateQuestionsToShow(questionsToShow: OptionItemEntityState?, conditionResult: Boolean) {

        if (questionsToShow?.showQuestion == conditionResult)
            return

        val mQuestionsToShow = questionsToShow?.copy(showQuestion = conditionResult)
        val questionsToShowIndex = _updatedOptionList.distinctBy { it.optionId }
            .map { it.optionId }
            .indexOf(mQuestionsToShow?.optionId)
        if (questionsToShowIndex != -1) {
            _updatedOptionList.removeAt(questionsToShowIndex)
            _updatedOptionList.add(questionsToShowIndex, mQuestionsToShow!!)
            /*if (!conditionResult) {
                onEvent(
                    QuestionTypeEvent.DeleteFormQuestionOptionResponseEvent(
                        mQuestionsToShow.optionId,
                        mQuestionsToShow.optionItemEntity?.questionId,
                        mQuestionsToShow.optionItemEntity?.sectionId,
                        mQuestionsToShow.optionItemEntity?.surveyId,
                        didiId
                    )
                )
            }*/
        }
    }

    fun updateCachedData() {
//        _formQuestionResponseEntity.value = storeCacheForResponse
        val tempList = updatedOptionList.toList()
        totalOptionSize.intValue = tempList.distinctBy { it.optionId }.filter { it
            .optionItemEntity?.optionType != QuestionType.Form.name && it.optionItemEntity?.optionType != QuestionType.Calculation.name }.filter { it.showQuestion }.size
        answeredOptionCount.intValue = (storeCacheForResponse.size).coerceIn(0, totalOptionSize.intValue)
        Log.d(TAG, "updateCachedData: storeCacheForResponse.size: ${storeCacheForResponse.size}, totalOptionSize.intValue: ${totalOptionSize.intValue}, answeredOptionCount.intValue: ${answeredOptionCount.intValue}")
    }

}
