package com.nrlm.baselinesurvey.ui.question_type_screen.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DELIMITER_MULTISELECT_OPTIONS
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.datamodel.ConditionsDto
import com.nrlm.baselinesurvey.model.response.ContentList
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.Constants.ResultType
import com.nrlm.baselinesurvey.ui.common_components.common_events.DialogEvents
import com.nrlm.baselinesurvey.ui.common_components.common_events.EventWriterEvents
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionScreenEvents
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.entity.FormTypeOption
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case.FormQuestionScreenUseCase
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.calculateResultForFormula
import com.nrlm.baselinesurvey.utils.checkCondition
import com.nrlm.baselinesurvey.utils.checkConditionForMultiSelectDropDown
import com.nrlm.baselinesurvey.utils.convertFormQuestionResponseEntityToSaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.utils.convertFormTypeQuestionListToOptionItemEntity
import com.nrlm.baselinesurvey.utils.convertQuestionListToOptionItemEntity
import com.nrlm.baselinesurvey.utils.convertToOptionItemEntity
import com.nrlm.baselinesurvey.utils.findIndexOfListByOptionId
import com.nrlm.baselinesurvey.utils.findOptionExist
import com.nrlm.baselinesurvey.utils.isNumeric
import com.nrlm.baselinesurvey.utils.showCustomToast
import com.nrlm.baselinesurvey.utils.states.DialogState
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nudge.core.DEFAULT_LANGUAGE_ID
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

    var areResponsesChanged: Boolean = false

    private val TAG = QuestionTypeScreenViewModel::class.java.simpleName

    private val _loaderState = mutableStateOf(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    private val _showUserChangedDialog = mutableStateOf<DialogState>(DialogState())
    val showUserChangedDialog: State<DialogState> get() = _showUserChangedDialog

    val optionList: State<List<OptionItemEntity>> get() = _optionList
    private val _optionList = mutableStateOf<List<OptionItemEntity>>(emptyList())

    var referenceId: String = UUID.randomUUID().toString()

    private val _formQuestionResponseEntity =
        mutableStateOf<List<FormQuestionResponseEntity>>(emptyList())

    val formQuestionResponseEntity: State<List<FormQuestionResponseEntity>> get() = _formQuestionResponseEntity

    private var _storeCacheForResponse = mutableListOf<FormQuestionResponseEntity>()
    val storeCacheForResponse: List<FormQuestionResponseEntity> get() = _storeCacheForResponse


    var formTypeOption = FormTypeOption.getEmptyOptionItem()

    private var _updatedOptionList = mutableStateListOf<OptionItemEntityState>()
    val updatedOptionList: SnapshotStateList<OptionItemEntityState> get() = _updatedOptionList

    private val updatedOptionListInDefaultLanguage = mutableListOf<OptionItemEntityState>()

    val totalOptionSize = mutableIntStateOf(0)
    val answeredOptionCount = mutableIntStateOf(0)

    var question = mutableStateOf<QuestionEntity?>(null)
    var contents = mutableListOf<List<ContentEntity>>()
    private var didiId = -1

    val calculatedResult = mutableStateOf("")

    var tempRefId = mutableStateOf(BLANK_STRING)

    var conditionalQuestionNotMarked = false

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
            question.value =
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

            BaselineLogger.d(
                TAG,
                "init: referenceId: ${this@QuestionTypeScreenViewModel.referenceId}"
            )
            if (referenceId.isNotBlank()) {
                this@QuestionTypeScreenViewModel.referenceId = referenceId
                tempRefId.value = referenceId
                BaselineLogger.d(
                    TAG,
                    "init: referenceId after update: ${this@QuestionTypeScreenViewModel.referenceId}"
                )
                _formQuestionResponseEntity.value =
                    getFormResponseForReferenceId(referenceId = referenceId)
                _storeCacheForResponse.addAll(formQuestionResponseEntity.value.toMutableList())
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
                    formQuestionResponseEntity.value.find { it.optionId == updatedOptionList.find { it.optionItemEntity?.optionType == QuestionType.Calculation.name && it.showQuestion }?.optionId }?.selectedValue
                        ?: BLANK_STRING
            }

            updateCachedData()

            getOptionsInDefaultLanguage(surveyId, sectionId, questionId)

            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private suspend fun getOptionsInDefaultLanguage(
        surveyId: Int,
        sectionId: Int,
        questionId: Int
    ) {
        val optionList = formQuestionScreenUseCase.getFormQuestionResponseUseCase.invoke(
            surveyId,
            sectionId,
            questionId,
            selectDefaultLanguage = true
        )
        optionList.forEach { optionItemEntity ->
            updatedOptionListInDefaultLanguage.add(
                OptionItemEntityState(
                    optionId = optionItemEntity.optionId,
                    optionItemEntity = optionItemEntity,
                    showQuestion = true
                )
            )
            optionItemEntity.conditions?.forEach { conditionsDto ->
                when (conditionsDto?.resultType) {
                    ResultType.Questions.name -> {
                        conditionsDto?.resultList?.forEach { questionList ->
                            if (questionList.type?.equals(QuestionType.Form.name, true) == true) {
                                val mOptionItemEntityList =
                                    questionList.convertFormTypeQuestionListToOptionItemEntity(
                                        optionItemEntity.sectionId,
                                        optionItemEntity.surveyId,
                                        optionItemEntity.languageId ?: DEFAULT_LANGUAGE_ID
                                    )
                                mOptionItemEntityList.forEach { mOptionItemEntity ->
                                    updatedOptionListInDefaultLanguage.add(
                                        OptionItemEntityState(
                                            mOptionItemEntity.optionId,
                                            mOptionItemEntity,
                                            false
                                        )
                                    )
                                }
                            }
                            val mOptionItemEntity =
                                questionList.convertQuestionListToOptionItemEntity(
                                    optionItemEntity.sectionId,
                                    optionItemEntity.surveyId
                                )
                            updatedOptionListInDefaultLanguage.add(
                                OptionItemEntityState(
                                    mOptionItemEntity.optionId,
                                    mOptionItemEntity,
                                    false
                                )
                            )

                            // TODO Handle later correctly
                            mOptionItemEntity.conditions?.forEach { conditionsDto2 ->
                                if (conditionsDto2?.resultType.equals(
                                        ResultType.Questions.name,
                                        true
                                    )
                                ) {
                                    conditionsDto2?.resultList?.forEach { subQuestionList ->
                                        val mOptionItemEntity2 =
                                            subQuestionList.convertQuestionListToOptionItemEntity(
                                                mOptionItemEntity.sectionId,
                                                mOptionItemEntity.surveyId
                                            )
                                        updatedOptionListInDefaultLanguage.add(
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
                            updatedOptionListInDefaultLanguage.add(
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
    }

    private fun getOptionItemEntityState(
        surveyId: Int,
        didiId: Int,
        sectionId: Int,
        questionId: Int
    ) {
        formTypeOption = FormTypeOption.getOptionItem(
            surveyId = surveyId,
            didiId = didiId,
            sectionId = sectionId,
            questionId = questionId,
            optionItems = optionList.value
        )
        formTypeOption?.options?.forEach { optionItemEntity ->
            _updatedOptionList.add(
                OptionItemEntityState(
                    optionId = optionItemEntity.optionId,
                    optionItemEntity = optionItemEntity,
                    showQuestion = true
                )
            )
            optionItemEntity.conditions?.forEach { conditionsDto ->
                when (conditionsDto?.resultType) {
                    ResultType.Questions.name -> {
                        conditionsDto?.resultList?.forEach { questionList ->
                            if (questionList.type?.equals(QuestionType.Form.name, true) == true
                                || questionList.type?.equals(
                                    QuestionType.FormWithNone.name,
                                    true
                                ) == true
                            ) {
                                val mOptionItemEntityList =
                                    questionList.convertFormTypeQuestionListToOptionItemEntity(
                                        optionItemEntity.sectionId,
                                        optionItemEntity.surveyId,
                                        optionItemEntity.languageId ?: DEFAULT_LANGUAGE_ID
                                    )
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
                            val mOptionItemEntity =
                                questionList.convertQuestionListToOptionItemEntity(
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
                                if (conditionsDto2?.resultType.equals(
                                        ResultType.Questions.name,
                                        true
                                    )
                                ) {
                                    conditionsDto2?.resultList?.forEach { subQuestionList ->
                                        val mOptionItemEntity2 =
                                            subQuestionList.convertQuestionListToOptionItemEntity(
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
            tempFormQuestionResponseEntityList.distinctBy { it.optionId }
                .forEach { formQuestionResponseEntity ->
                    Log.d(
                        TAG,
                        "updateAnsweredConditionalQuestion: formQuestionResponseEntity -> $formQuestionResponseEntity"
                    )
                    tempList.forEach { optionItemState ->
                        if (optionItemState.optionId == formQuestionResponseEntity.optionId) {
                            var optionToUpdate =
                                tempList.find { it.optionId == optionItemState.optionId }

                            optionToUpdate = optionToUpdate?.copy(showQuestion = true)

                            val optionToUpdateIndex =
                                tempList.findIndexOfListByOptionId(optionToUpdate?.optionId)
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
        return formQuestionScreenUseCase.getFormQuestionResponseUseCase.getFormResponseForReferenceId(
            referenceId
        )
    }

    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

            is DialogEvents.ShowDialogEvent -> {
                _showUserChangedDialog.value = _showUserChangedDialog.value.copy(
                    isDialogVisible = event.showDialog
                )
            }

            is QuestionTypeEvent.SaveFormQuestionResponseEvent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val formQuestionResponseForQuestionOption =
                        formQuestionScreenUseCase.getFormQuestionResponseUseCase.getFormResponsesForQuestionOption(
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
                            event.formQuestionResponseEntity.didiId,
                            event.formQuestionResponseEntity.selectedValueId
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
                                surveyeeId = event.surveyeeId,
                                referenceId = referenceId
                            )
                        } else {
                            throw NullPointerException((event as QuestionTypeEvent.DeleteFormQuestionOptionResponseEvent).toString())
                        }
                    } catch (ex: Exception) {
                        BaselineLogger.e(
                            TAG,
                            "onEvent -> QuestionTypeEvent.DeleteFormQuestionOptionResponseEvent -> null pointer exception",
                            ex
                        )
                    }
                }
            }

            is QuestionTypeEvent.UpdateConditionalOptionState -> {
                if (event.userInputValue != BLANK_STRING) {
                    event.optionItemEntityState?.optionItemEntity?.conditions?.forEach { conditionsDto ->
                        val conditionCheckResult =
                            if (TextUtils.equals(
                                    event.optionItemEntityState.optionItemEntity.optionType?.toLowerCase(),
                                    QuestionType.MultiSelectDropDown.name.toLowerCase()
                                )
                            ) {
                                val stringValue =
                                    event.optionItemEntityState.optionItemEntity.values?.filter {
                                        event.userInputValue.split(
                                            DELIMITER_MULTISELECT_OPTIONS
                                        ).contains(it.value)
                                    }?.map { it.value } ?: listOf()
                                conditionsDto?.checkConditionForMultiSelectDropDown(stringValue)

                            } else {
                                conditionsDto?.checkCondition(event.userInputValue)
                            }
                        updateQuestionStateForCondition(conditionCheckResult == true, conditionsDto)
                        conditionsDto?.resultList?.forEach { subQList ->
                            subQList.options?.forEach { subOptList ->
                                subOptList?.conditions?.forEach { subCondtionDto ->
                                    val subConditionCheckResult =
                                        if (TextUtils.equals(
                                                event.optionItemEntityState.optionItemEntity.optionType?.toLowerCase(),
                                                QuestionType.MultiSelectDropDown.name.toLowerCase()
                                            )
                                        ) {
                                            val stringValue =
                                                event.optionItemEntityState.optionItemEntity.values?.filter {
                                                    event.userInputValue.split(
                                                        DELIMITER_MULTISELECT_OPTIONS
                                                    ).contains(it.value)
                                                }?.map { it.value } ?: listOf()
                                            subCondtionDto?.checkConditionForMultiSelectDropDown(
                                                stringValue
                                            )

                                        } else {
                                            subCondtionDto?.checkCondition(event.userInputValue)
                                        }
                                    updateQuestionStateForCondition(
                                        subConditionCheckResult == true,
                                        subCondtionDto
                                    )
                                }
                            }
                        }
                    }
                } else {
                    if (!TextUtils.equals(
                            event.optionItemEntityState?.optionItemEntity?.optionType?.toLowerCase(),
                            QuestionType.MultiSelectDropDown.name.toLowerCase()
                        )
                    ) {
                        event.optionItemEntityState?.optionItemEntity?.conditions?.forEach { conditionsDto ->
                            updateQuestionStateForCondition(false, conditionsDto)
                        }
                    }
                }

            }

            is QuestionTypeEvent.UpdateCalculationTypeQuestionValue -> {
                val optionList = updatedOptionList.toList()
                if (optionList.any { it.optionItemEntity?.optionType == QuestionType.Calculation.name && it.showQuestion }) {
                    val calculationOption =
                        optionList.find { it.optionItemEntity?.optionType == QuestionType.Calculation.name && it.showQuestion }
                    calculationOption?.optionItemEntity?.conditions?.forEach { conditionDto ->
                        val optionIds = mutableListOf<Int>()
                        conditionDto?.value?.split(" ")?.filter { it != "" }?.forEach { va ->
                            if (va.isNotEmpty() && isNumeric(va)) {
                                optionIds.add(va.toInt())
                            }
                        }
                        var areAllValuesPresent = 0
                        val listOfValuesToCalculate = mutableListOf<FormQuestionResponseEntity>()
                        if (optionIds.isNotEmpty()) {
                            optionIds.forEach { option ->
                                val findOption = storeCacheForResponse.findOptionExist(option)
                                if (findOption == true) {
                                    areAllValuesPresent++
                                    storeCacheForResponse.find { it.optionId == option }
                                        ?.let { listOfValuesToCalculate.add(it) }
                                } else {
                                    val findOptionInSavedValues = formQuestionResponseEntity.value
                                    val findUnchangedOption =
                                        findOptionInSavedValues.findOptionExist(option)
                                    if (findUnchangedOption == true) {
                                        areAllValuesPresent++
                                        findOptionInSavedValues.find { it.optionId == option }
                                            ?.let { listOfValuesToCalculate.add(it) }
                                    }
                                }
                            }

                            if (areAllValuesPresent == optionIds.size) {
                                val result =
                                    conditionDto?.calculateResultForFormula(listOfValuesToCalculate)
                                calculatedResult.value = result ?: BLANK_STRING
                            }
                        }
                    }
                }

            }

            is QuestionTypeEvent.SaveCacheFormQuestionResponseToDbEvent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val finalFormQuestionResponseList = mutableListOf<FormQuestionResponseEntity>()

                    val unchangedValues = mutableMapOf<Int, FormQuestionResponseEntity>()
                    val formQuestionResponseEntityMap =
                        formQuestionResponseEntity.value.associateBy { it.optionId }
                    unchangedValues.putAll(formQuestionResponseEntityMap)
                    event.formQuestionResponseList.forEach {
                        unchangedValues[it.optionId] = it
                    }

                    finalFormQuestionResponseList.addAll(unchangedValues.values.toList())

                    updatedOptionList.forEach {
                        if (it.optionItemEntity?.optionType?.equals(
                                QuestionType.Calculation.name,
                                true
                            ) == true && it.showQuestion
                        ) {
                            it.optionItemEntity?.conditions?.forEach { conditionDto ->
                                val resultedValue = conditionDto?.calculateResultForFormula(
                                    finalFormQuestionResponseList
                                )
                                if (!resultedValue.isNullOrBlank()) {
                                    val map =
                                        finalFormQuestionResponseList.associateBy { it.optionId }
                                            .toMutableMap()
                                    it.optionId?.let { optionId ->
                                        map.put(
                                            optionId, FormQuestionResponseEntity(
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
                                    finalFormQuestionResponseList.clear()
                                    finalFormQuestionResponseList.addAll(map.values.toList())
                                }
                            }
                        }
                    }
                    finalFormQuestionResponseList.distinctBy { it.optionId }.forEach {
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
                            questionTag = question?.value?.tag ?: -1,
                            questionDesc = question.value?.questionDisplay ?: BLANK_STRING,
                            saveAnswerEventOptionItemDtoList = completeOptionListForQuestion
                                .convertFormQuestionResponseEntityToSaveAnswerEventOptionItemDto(
                                    QuestionType.Form,
                                    updatedOptionList.toList()
                                )
                        )
                    )
                    onEvent(
                        QuestionScreenEvents.SectionProgressUpdated(
                            finalFormQuestionResponseList.first().surveyId,
                            finalFormQuestionResponseList.first().sectionId,
                            didiId,
                            SectionStatus.INPROGRESS
                        )
                    )
                }
            }

            is QuestionTypeEvent.CacheFormQuestionResponseEvent -> {
                val form = storeCacheForResponse
                    .find { it.optionId == event.formQuestionResponseEntity.optionId }
                if (form == null) {
                    if (event.formQuestionResponseEntity.selectedValue != BLANK_STRING)
                        _storeCacheForResponse.add(event.formQuestionResponseEntity)
                    conditionalQuestionNotMarked = false
                } else {
                    val tempList = updatedOptionList.toList()
                    val option =
                        tempList.find { it.optionId == event.formQuestionResponseEntity.optionId }?.optionItemEntity
                    if (event.formQuestionResponseEntity.selectedValue == BLANK_STRING) {
                        if (option?.conditions == null) {
                            val index =
                                storeCacheForResponse.map { it.optionId }.indexOf(form.optionId)
//                                    .coerceIn(0, storeCacheForResponse.size)

                            if (index != -1) {
                                _storeCacheForResponse.removeAt(index)
                                conditionalQuestionNotMarked = false
                            }
                        } else {
                            conditionalQuestionNotMarked = true
                            showCustomToast(
                                BaselineCore.getAppContext(),
                                BaselineCore.getAppContext()
                                    .getString(R.string.madnatory_question_not_marked_error)
                            )
                        }
                    } else {
                        form.selectedValue = event.formQuestionResponseEntity.selectedValue
                        form.selectedValueId = event.formQuestionResponseEntity.selectedValueId
                        val index = storeCacheForResponse.map { it.optionId }.indexOf(form.optionId)
//                            .coerceIn(0, storeCacheForResponse.size)

                        if (index != -1) {
                            _storeCacheForResponse.removeAt(index)
                            _storeCacheForResponse.add(index = index, form)
                            conditionalQuestionNotMarked = false
                        }
                    }
                }
                if (!TextUtils.equals(
                        updatedOptionList.toList()
                            .find { it.optionId == event.formQuestionResponseEntity.optionId }
                            ?.optionItemEntity?.optionType?.toLowerCase(),
                        QuestionType.MultiSelectDropDown.name.toLowerCase()
                    )
                ) {
                    removeAnswersForUnSelectedConditions(event.formQuestionResponseEntity)
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
                            questionDesc = event.questionDesc,
                            referenceOptionList = updatedOptionListInDefaultLanguage.toList(),
                            saveAnswerEventOptionItemDtoList = event.saveAnswerEventOptionItemDtoList
                        )
                    formQuestionScreenUseCase.eventsWriterUserCase.invoke(
                        events = saveAnswerEvent,
                        eventType = EventType.STATEFUL
                    )
                }
            }

            is QuestionScreenEvents.SectionProgressUpdated -> {
                CoroutineScope(Dispatchers.IO).launch {
                    onEvent(
                        EventWriterEvents.UpdateSectionStatusEvent(
                            event.surveyId,
                            event.sectionId,
                            event.didiId,
                            event.sectionStatus
                        )
                    )
                    formQuestionScreenUseCase.updateSectionProgressUseCase.invoke(
                        event.surveyId,
                        event.sectionId,
                        event.didiId,
                        event.sectionStatus
                    )

                    updateMissionActivityTaskStatus(event.didiId, SectionStatus.INPROGRESS)
                }
            }

            is EventWriterEvents.UpdateSectionStatusEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val updateSectionStatusEvent =
                        eventWriterHelperImpl.createUpdateSectionStatusEvent(
                            event.surveyId,
                            event.sectionId,
                            event.didiId,
                            event.sectionStatus
                        )
                    formQuestionScreenUseCase.eventsWriterUserCase.invoke(
                        events = updateSectionStatusEvent,
                        eventType = EventType.STATEFUL
                    )
                }
            }

            is EventWriterEvents.UpdateMissionActivityTaskStatus -> {
                CoroutineScope(Dispatchers.IO).launch {
                    eventWriterHelperImpl.markMissionActivityTaskInProgress(
                        missionId = event.missionId,
                        activityId = event.activityId,
                        taskId = event.taskId,
                        status = event.status
                    )
                }
            }

            is EventWriterEvents.UpdateMissionActivityTaskStatusEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val eventList = eventWriterHelperImpl.getMissionActivityTaskEventList(
                        missionId = event.missionId,
                        activityId = event.activityId,
                        taskId = event.taskId,
                        status = event.status
                    )
                    eventList.forEach { event ->
                        formQuestionScreenUseCase.eventsWriterUserCase.invoke(
                            events = event,
                            eventType = EventType.STATEFUL
                        )
                    }
                }
            }
        }
    }

    private suspend fun updateMissionActivityTaskStatus(didiId: Int, sectionStatus: SectionStatus) {
        val activityForSubjectDto = eventWriterHelperImpl.getActivityFromSubjectId(didiId)
        activityForSubjectDto?.let {
            onEvent(
                EventWriterEvents.UpdateMissionActivityTaskStatus(
                    missionId = activityForSubjectDto.missionId,
                    activityId = activityForSubjectDto.activityId,
                    taskId = activityForSubjectDto.taskId,
                    status = sectionStatus
                )
            )
            onEvent(
                EventWriterEvents.UpdateMissionActivityTaskStatusEvent(
                    missionId = activityForSubjectDto.missionId,
                    activityId = activityForSubjectDto.activityId,
                    taskId = activityForSubjectDto.taskId,
                    status = sectionStatus
                )
            )
        }
    }

    private fun removeAnswersForUnSelectedConditions(response: FormQuestionResponseEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tempList = updatedOptionList.toList()
                val optionMarked = tempList.find {
                    it.optionId == response.optionId
                }
                val unSelectedConditionDtoList =
                    optionMarked?.optionItemEntity?.conditions?.filter { it?.checkCondition(response.selectedValue) != true }


                unSelectedConditionDtoList?.forEach { unSelectedConditionDto ->
                    unSelectedConditionDto?.resultList?.forEach { qList ->
                        if (qList.type == QuestionType.Form.name || qList.type == QuestionType.FormWithNone.name) {
                            qList.options?.forEach { opt ->
                                val i1 =
                                    storeCacheForResponse.map { it.optionId }.indexOf(opt?.optionId)
                                if (i1 != -1) {
                                    _storeCacheForResponse.removeAt(i1)
                                }
                                val i2 = formQuestionResponseEntity.value.map { it.optionId }
                                    .indexOf(opt?.optionId)
                                if (i2 != -1) {
                                    val index2Option = formQuestionResponseEntity.value[i2]
                                    val list = formQuestionResponseEntity.value.toMutableList()
                                    list.removeAt(i2)
                                    _formQuestionResponseEntity.value = list
                                    formQuestionScreenUseCase.deleteFormQuestionOptionResponseUseCase.invoke(
                                        optionId = index2Option.optionId,
                                        questionId = index2Option.questionId,
                                        sectionId = index2Option.sectionId,
                                        surveyId = index2Option.surveyId,
                                        surveyeeId = didiId,
                                        referenceId = response.referenceId
                                    )
                                }
                                opt?.conditions?.forEach { subConditionDto ->
                                    subConditionDto?.resultList?.forEach { subQList ->
                                        val subIndex1 = storeCacheForResponse.map { it.optionId }
                                            .indexOf(subQList.questionId)
                                        if (subIndex1 != -1) {
                                            _storeCacheForResponse.removeAt(subIndex1)
                                        }
                                        val subIndex2 =
                                            formQuestionResponseEntity.value.map { it.optionId }
                                                .indexOf(subQList.questionId)
                                        if (subIndex2 != -1) {
                                            val subIndex2Option =
                                                formQuestionResponseEntity.value[subIndex2]

                                            val list =
                                                formQuestionResponseEntity.value.toMutableList()
                                            list.removeAt(subIndex2)
                                            _formQuestionResponseEntity.value = list

                                            formQuestionScreenUseCase.deleteFormQuestionOptionResponseUseCase.invoke(
                                                optionId = subIndex2Option.optionId,
                                                questionId = subIndex2Option.questionId,
                                                sectionId = subIndex2Option.sectionId,
                                                surveyId = subIndex2Option.surveyId,
                                                surveyeeId = didiId,
                                                referenceId = response.referenceId
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            val i1 =
                                storeCacheForResponse.map { it.optionId }.indexOf(qList.questionId)
                            if (i1 != -1) {
                                _storeCacheForResponse.removeAt(i1)
                            }
                            val i2 = formQuestionResponseEntity.value.map { it.optionId }
                                .indexOf(qList.questionId)
                            if (i2 != -1) {
                                val index2Option = formQuestionResponseEntity.value[i2]
                                val list = _formQuestionResponseEntity.value.toMutableList()
                                list.removeAt(i2)
                                _formQuestionResponseEntity.value = list
                                formQuestionScreenUseCase.deleteFormQuestionOptionResponseUseCase.invoke(
                                    optionId = index2Option.optionId,
                                    questionId = index2Option.questionId,
                                    sectionId = index2Option.sectionId,
                                    surveyId = index2Option.surveyId,
                                    surveyeeId = didiId,
                                    referenceId = response.referenceId
                                )
                            }
                            qList.options?.forEach { optItem ->
                                optItem?.conditions?.forEach { subConditionDto ->
                                    subConditionDto?.resultList?.forEach { subQList ->
                                        val subIndex1 = storeCacheForResponse.map { it.optionId }
                                            .indexOf(subQList.questionId)
                                        if (subIndex1 != -1) {
                                            _storeCacheForResponse.removeAt(subIndex1)
                                        }
                                        val subIndex2 =
                                            formQuestionResponseEntity.value.map { it.optionId }
                                                .indexOf(subQList.questionId)
                                        if (subIndex2 != -1) {
                                            val subIndex2Option =
                                                formQuestionResponseEntity.value[subIndex2]
                                            val list =
                                                _formQuestionResponseEntity.value.toMutableList()
                                            list.removeAt(subIndex2)
                                            _formQuestionResponseEntity.value = list
                                            formQuestionScreenUseCase.deleteFormQuestionOptionResponseUseCase.invoke(
                                                optionId = subIndex2Option.optionId,
                                                questionId = subIndex2Option.questionId,
                                                sectionId = subIndex2Option.sectionId,
                                                surveyId = subIndex2Option.surveyId,
                                                surveyeeId = didiId,
                                                referenceId = response.referenceId
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            } catch (ex: Exception) {
                BaselineLogger.e(
                    "QuestionTypeScreenViewModel",
                    "removeAnswersForUnSelectedConditions: exception -> ${ex.message}",
                    ex
                )
            }
        }

    }

    override fun updateQuestionStateForCondition(
        conditionResult: Boolean,
        conditionsDto: ConditionsDto?
    ) {
        when (conditionsDto?.resultType) {
            ResultType.Questions.name -> {
                conditionsDto?.resultList?.forEach { questionList ->
                    if (questionList.type?.equals(QuestionType.Form.name, true) == true) {
                        val tempList = _updatedOptionList.distinctBy { it.optionId }

                        questionList.options?.forEach { optItem ->
                            val questionsToShow =
                                tempList.find { optionItemEntityState -> optionItemEntityState.optionId == optItem?.optionId }

                            updateQuestionsToShow(questionsToShow, conditionResult)
                        }
                    } else {
                        val tempList = _updatedOptionList.distinctBy { it.optionId }
                        val questionsToShow =
                            tempList.find { it.optionId == questionList.questionId }

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

    private fun updateQuestionsToShow(
        questionsToShow: OptionItemEntityState?,
        conditionResult: Boolean
    ) {

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
        totalOptionSize.intValue = tempList.distinctBy { it.optionId }.filter {
            it.optionItemEntity?.optionType != QuestionType.Form.name && it.optionItemEntity?.optionType != QuestionType.Calculation.name
                    && it.optionItemEntity?.optionType != QuestionType.FormWithNone.name
        }.filter { it.showQuestion }.size
        answeredOptionCount.intValue =
            (storeCacheForResponse.size).coerceIn(0, totalOptionSize.intValue)
        BaselineLogger.d(
            TAG,
            "updateCachedData: storeCacheForResponse.size: ${storeCacheForResponse.size}, totalOptionSize.intValue: ${totalOptionSize.intValue}, answeredOptionCount.intValue: ${answeredOptionCount.intValue}"
        )
    }

    suspend fun getContentData(
        contents: List<ContentList?>?,
        contentType: String
    ): ContentList? {
        contents?.let { contentsData ->
            for (content in contentsData) {
                if (content?.contentType.equals(contentType, true)) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val contentEntity =
                            formQuestionScreenUseCase.getFormQuestionResponseUseCase.getContentData(
                                content?.contentKey ?: BLANK_STRING,
                            )
                        if (contentEntity != null) {
                            content?.contentValue = contentEntity.contentValue
                        }
                    }
                    delay(500)
                    return content!!
                }
            }
        }
        return null
    }

    fun getResponseChangedFlag() = areResponsesChanged

    fun setResponseChangedFlag(responseChanged: Boolean) {
        areResponsesChanged = responseChanged
    }
}
