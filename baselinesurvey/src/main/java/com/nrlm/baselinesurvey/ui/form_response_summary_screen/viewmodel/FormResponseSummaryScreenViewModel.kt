package com.nrlm.baselinesurvey.ui.form_response_summary_screen.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.FormResponseObjectDto
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.Constants.ResultType
import com.nrlm.baselinesurvey.ui.common_components.common_events.EventWriterEvents
import com.nrlm.baselinesurvey.ui.form_response_summary_screen.domain.use_case.FormResponseSummaryScreenUseCase
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.convertFormResponseObjectToSaveAnswerEventOptionDto
import com.nrlm.baselinesurvey.utils.convertToOptionItemEntity
import com.nrlm.baselinesurvey.utils.findIdFromTag
import com.nrlm.baselinesurvey.utils.getIndexForReferenceId
import com.nrlm.baselinesurvey.utils.mapFormQuestionResponseToFromResponseObjectDto
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nrlm.baselinesurvey.utils.tagList
import com.nrlm.baselinesurvey.utils.toOptionItemStateList
import com.nudge.core.enums.EventType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormResponseSummaryScreenViewModel @Inject constructor(
    private val formResponseSummaryScreenUseCase: FormResponseSummaryScreenUseCase,
    private val eventWriterHelperImpl: EventWriterHelperImpl
) : BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    val optionItemEntityList = mutableListOf<OptionItemEntity>()

    private val optionItemEntityListInDefaultLanguage = mutableListOf<OptionItemEntity>()

    private var _formResponseObjectDtoList = mutableStateOf(mutableListOf<FormResponseObjectDto>())
    val formResponseObjectDtoList: State<List<FormResponseObjectDto>> get() = _formResponseObjectDtoList

    var questionEntity: QuestionEntity? = null

    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }

            is QuestionTypeEvent.DeleteFormQuestionResponseEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    formResponseSummaryScreenUseCase.deleteFormQuestionResponseUseCase.invoke(
                        referenceId = event.referenceId
                    )
                    val item =
                        formResponseObjectDtoList.value.find { it.referenceId == event.referenceId }
                    onEvent(
                        EventWriterEvents.SaveAnswerEvent(
                            surveyId = event.surveyId,
                            sectionId = event.sectionId,
                            didiId = event.surveyeeId,
                            questionId = event.questionId,
                            questionType = QuestionType.Form.name,
                            questionTag = tagList.findIdFromTag(item?.questionTag ?: BLANK_STRING),
                            questionDesc = event.questionDesc,
                            saveAnswerEventOptionItemDtoList = formResponseObjectDtoList.value.filter { it.referenceId != event.referenceId }
                                .convertFormResponseObjectToSaveAnswerEventOptionDto(
                                    getOptionItemListWithConditionals()
                                )
                        )
                    )
                }
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
                            referenceOptionList = optionItemEntityListInDefaultLanguage.toOptionItemStateList(),
                            saveAnswerEventOptionItemDtoList = event.saveAnswerEventOptionItemDtoList
                        )
                    formResponseSummaryScreenUseCase.eventsWriterUseCase.invoke(
                        events = saveAnswerEvent,
                        eventType = EventType.STATEFUL
                    )
                }
            }
        }
    }

    fun init(surveyId: Int, sectionId: Int, questionId: Int, surveyeeId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val formResponseEntityList =
                formResponseSummaryScreenUseCase.getFormQuestionResponseUseCase.getFormResponsesForQuestion(
                    surveyId = surveyId,
                    sectionId = sectionId,
                    questionId = questionId,
                    didiId = surveyeeId
                )
            val mOptionItemEntityList =
                formResponseSummaryScreenUseCase.getFormQuestionResponseUseCase.invoke(
                    surveyId,
                    sectionId,
                    questionId
                )

            val mOptionItemEntityListInDefaultLanguage =
                formResponseSummaryScreenUseCase.getFormQuestionResponseUseCase.invoke(
                    surveyId,
                    sectionId,
                    questionId,
                    true
                )

            optionItemEntityListInDefaultLanguage.clear()
            optionItemEntityListInDefaultLanguage.addAll(mOptionItemEntityListInDefaultLanguage)

            optionItemEntityList.clear()
            optionItemEntityList.addAll(mOptionItemEntityList)

            questionEntity =
                formResponseSummaryScreenUseCase.getFormQuestionResponseUseCase.getFormQuestionForId(
                    surveyId = surveyId,
                    sectionId = sectionId,
                    questionId = questionId
                )

            val questionTag =
                formResponseSummaryScreenUseCase.getFormQuestionResponseUseCase.getQuestionTag(
                    surveyId = surveyId,
                    sectionId = sectionId,
                    questionId = questionId
                )
            _formResponseObjectDtoList.value.clear()
            _formResponseObjectDtoList.value.addAll(formResponseEntityList.mapFormQuestionResponseToFromResponseObjectDto(
                mOptionItemEntityList,
                questionTag
            ).distinctBy { it.referenceId })
        }
    }

    fun updateFormResponseObjectDtoList(referenceId: String) {
        val tempList = formResponseObjectDtoList.value.toMutableList()

        val index = tempList.getIndexForReferenceId(referenceId)
        tempList.removeAt(index)

        _formResponseObjectDtoList.value = tempList
    }

    fun getOptionItemListWithConditionals(): List<OptionItemEntity> {
        val optionItemList = mutableListOf<OptionItemEntity>()
        optionItemEntityList.forEach { option ->
            optionItemList.add(option)
        }

        optionItemEntityList.forEach { optionItemEntity ->
            optionItemEntity.conditions?.forEach { conditionsDto ->
                if (conditionsDto?.resultType?.equals(ResultType.Questions.name, true) == true) {
                    conditionsDto?.resultList?.forEach { questionItem ->
                        questionItem.options?.forEach { subOption ->
                            val option = subOption?.convertToOptionItemEntity(
                                optionItemEntity.questionId!!,
                                optionItemEntity.sectionId,
                                optionItemEntity.surveyId,
                                optionItemEntity.languageId!!
                            )
                            if (option != null)
                                optionItemList.add(option)
                        }
                    }
                }
            }
        }
        return optionItemList
    }

}