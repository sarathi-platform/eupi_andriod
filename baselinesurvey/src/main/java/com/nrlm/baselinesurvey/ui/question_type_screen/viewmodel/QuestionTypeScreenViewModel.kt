package com.nrlm.baselinesurvey.ui.question_type_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.model.datamodel.ConditionsDto
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.entity.FormTypeOption
import com.nrlm.baselinesurvey.ui.question_type_screen.domain.use_case.FormQuestionScreenUseCase
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.checkCondition
import com.nrlm.baselinesurvey.utils.convertQuestionListToOptionItemEntity
import com.nrlm.baselinesurvey.utils.states.LoaderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class QuestionTypeScreenViewModel @Inject constructor(
    private val formQuestionScreenUseCase: FormQuestionScreenUseCase
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

    private var didiId = -1

    fun init(sectionId: Int, surveyId: Int, questionId: Int, surveyeeId: Int, referenceId: String = BLANK_STRING) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        didiId = surveyeeId
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _optionList.value =
                formQuestionScreenUseCase.getFormQuestionResponseUseCase.invoke(
                    surveyId,
                    sectionId,
                    questionId
                )

            getOptionItemEntityState(surveyId = surveyId, didiId = surveyeeId, sectionId = sectionId, questionId = questionId)
            if (referenceId.isNotBlank()) {
                this@QuestionTypeScreenViewModel.referenceId = referenceId
                _formQuestionResponseEntity.value = getFormResponseForReferenceId(referenceId = referenceId)
            }
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    fun getOptionItemEntityState(surveyId: Int, didiId: Int, sectionId: Int, questionId: Int) {
        formTypeOption = FormTypeOption.getOptionItem(surveyId = surveyId, didiId = didiId, sectionId = sectionId, questionId = questionId, optionItems = optionList.value)
        formTypeOption?.options?.forEach { optionItemEntity ->
            _updatedOptionList.add(
                OptionItemEntityState(
                optionId = optionItemEntity.optionId,
                optionItemEntity = optionItemEntity,
                showQuestion = true)
            )
            optionItemEntity.conditions?.forEach { conditionsDto ->
                conditionsDto?.resultList?.forEach {  questionList ->
                    val mOptionItemEntity = questionList.convertQuestionListToOptionItemEntity(optionItemEntity.sectionId, optionItemEntity.surveyId)
                    _updatedOptionList.add(OptionItemEntityState(mOptionItemEntity.optionId, mOptionItemEntity, false))
                }
            }
        }
        totalOptionSize.intValue = updatedOptionList.size
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
                totalOptionSize.intValue = updatedOptionList.filter { it.showQuestion }.size
                if (answeredOptionCount.intValue > totalOptionSize.intValue) {
                    answeredOptionCount.intValue = totalOptionSize.intValue
                }
            }

            is QuestionTypeEvent.SaveCacheFormQuestionResponseToDbEvent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    formQuestionScreenUseCase.saveFormQuestionResponseUseCase.saveFormsListIntoDB(
                        event.formQuestionResponseList
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
                }
                updateCachedData()
            }
        }
    }

    override fun updateQuestionStateForCondition(conditionResult: Boolean, conditionsDto: ConditionsDto?) {
        conditionsDto?.resultList?.forEach { questionList ->
            val tempList = _updatedOptionList.distinctBy { it.optionId }
            var questionsToShow = tempList.find { it.optionId == questionList.questionId }
            questionsToShow = questionsToShow?.copy(showQuestion = conditionResult)
            val questionsToShowIndex = _updatedOptionList.distinctBy { it.optionId }
                .map { it.optionId }
                .indexOf(questionsToShow?.optionId)
            _updatedOptionList.removeAt(questionsToShowIndex)
            _updatedOptionList.add(questionsToShowIndex, questionsToShow!!)
            onEvent(
                QuestionTypeEvent.DeleteFormQuestionOptionResponseEvent(
                    questionsToShow.optionId,
                    questionsToShow.optionItemEntity?.questionId,
                    questionsToShow.optionItemEntity?.sectionId,
                    questionsToShow.optionItemEntity?.surveyId,
                    didiId
                )
            )
        }
    }

    private fun updateCachedData() {
        _formQuestionResponseEntity.value = storeCacheForResponse
        totalOptionSize.intValue = updatedOptionList.size
        answeredOptionCount.intValue = formQuestionResponseEntity.value.size
    }

}
