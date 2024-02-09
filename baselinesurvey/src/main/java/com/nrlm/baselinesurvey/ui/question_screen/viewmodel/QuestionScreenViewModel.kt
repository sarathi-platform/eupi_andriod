package com.nrlm.baselinesurvey.ui.question_screen.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.model.response.ContentList
import com.nrlm.baselinesurvey.ui.common_components.common_events.SearchEvent
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.QuestionScreenUseCase
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionScreenEvents
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.sortedBySectionOrder
import com.nrlm.baselinesurvey.utils.states.LoaderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuestionScreenViewModel @Inject constructor(
    private val questionScreenUseCase: QuestionScreenUseCase
): BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    private val _sectionDetail = mutableStateOf<SectionListItem>(
        SectionListItem(
            contentList = listOf(
                ContentList(
                    BLANK_STRING,
                    BLANK_STRING
                )
            ), languageId = 2
        )
    )
    private val sectionDetail: State<SectionListItem> get() = _sectionDetail

    private val _sectionsList = mutableStateOf<List<SectionEntity>>(emptyList())
    val sectionsList: State<List<SectionEntity>> get() = _sectionsList

    val totalQuestionCount = sectionDetail.value.questionList.size
    val answeredQuestionCount =
        mutableStateOf(sectionDetail.value.questionAnswerMapping.values.size)

    val showExpandedImage = mutableStateOf(false)

    val expandedImagePath = mutableStateOf("")

    private val _filterSectionList = mutableStateOf<SectionListItem>(
        SectionListItem(
            contentList = listOf(
                ContentList(
                    BLANK_STRING,
                    BLANK_STRING
                )
            ), languageId = 2
        )
    )

    val filterSectionList: State<SectionListItem> get() = _filterSectionList

    fun initQuestionScreenHandler(surveyeeId: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _sectionsList.value = questionScreenUseCase.getSectionsListUseCase.invoke(surveyeeId)
                .sortedBySectionOrder()
        }
    }

    suspend fun getFormQuestionResponseEntity(surveyId: Int, sectionId: Int, questionId: Int, didiId: Int): LiveData<List<FormQuestionResponseEntity>> {
        return questionScreenUseCase.getFormQuestionResponseUseCase.getFormResponsesForQuestionLive(surveyId, sectionId, questionId, didiId)
    }

    var formResponsesForQuestionLive: LiveData<List<FormQuestionResponseEntity>> = MutableLiveData(mutableListOf())

    var optionItemEntityList = emptyList<OptionItemEntity>()
    suspend fun getFormQuestionsOptionsItemEntityList(surveyId: Int, sectionId: Int, questionId: Int): List<OptionItemEntity> {
        return questionScreenUseCase.getFormQuestionResponseUseCase.invoke(
            surveyId,
            sectionId,
            questionId
        )
    }

    fun init(sectionId: Int, surveyId: Int, surveyeeId: Int) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val selectedlanguageId = questionScreenUseCase.getSectionUseCase.getSelectedLanguage()
            _sectionDetail.value =
                questionScreenUseCase.getSectionUseCase.invoke(
                    sectionId,
                    surveyId,
                    selectedlanguageId
                )
            val questionAnswerMap = mutableMapOf<Int, List<OptionItemEntity>>()
            val localAnswerList =
                questionScreenUseCase.getSectionAnswersUseCase.getSectionAnswerForDidi(
                    sectionId = sectionId,
                    didiId = surveyeeId
                )
            Log.d("TAG", "init: localAnswerList-> $localAnswerList")
            localAnswerList.forEach {
                if (!questionAnswerMap.containsKey(it.questionId)) {
                    questionAnswerMap.put(it.questionId, it.optionItems)
                }
            }
            Log.d("TAG", "init: questionAnswerMap-> $questionAnswerMap")
            _sectionDetail.value = _sectionDetail.value.copy(
                questionAnswerMapping = questionAnswerMap
            )
            _filterSectionList.value = _sectionDetail.value
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    override fun <T> onEvent(event: T) {
        when (event) {
            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }
            is QuestionScreenEvents.SectionProgressUpdated -> {
                CoroutineScope(Dispatchers.IO).launch {
                    questionScreenUseCase.updateSectionProgressUseCase.invoke(event.surveyId, event.sectionId, event.didiId, event.sectionStatus)
                }
            }
            is QuestionScreenEvents.RatioTypeQuestionAnswered -> {
                saveOrUpdateSectionAnswers(
                    surveyId = event.surveyId,
                    sectionId = event.sectionId,
                    didiId = event.didiId,
                    questionId = event.questionId,
                    optionsItem = listOf(event.optionItemEntity),
                    questionEntity = event.questionEntity
                )
            }
            is QuestionScreenEvents.ListTypeQuestionAnswered -> {
                saveOrUpdateSectionAnswers(
                    surveyId = event.surveyId,
                    sectionId = event.sectionId,
                    didiId = event.didiId,
                    questionId = event.questionId,
                    optionsItem = listOf(event.optionItemEntity),
                    questionEntity = event.questionEntity
                )
            }
            is QuestionScreenEvents.GridTypeQuestionAnswered -> {
                saveOrUpdateSectionAnswers(
                    surveyId = event.surveyId,
                    sectionId = event.sectionId,
                    didiId = event.didiId,
                    questionId = event.questionId,
                    optionsItem = event.optionItemList,
                    questionEntity = event.questionEntity
                )
            }

            is QuestionScreenEvents.SendAnswersToServer -> {
                viewModelScope.launch(Dispatchers.IO) {
                    questionScreenUseCase.saveSectionAnswerUseCase.saveSectionAnswersToServer(
                        event.didiId,
                        event.surveyId
                    )
                }
            }

            is SearchEvent.PerformSearch -> {
                performSearchQuery(event.searchTerm, event.isFilterApplied, event.fromScreen)
            }

            /*is QuestionScreenEvents.FormTypeQuestionAnswered -> {
                viewModelScope.launch(Dispatchers.IO) {
                    questionScreenUseCase.saveSectionAnswerUseCase.updateOptionItemValue(
                        event.surveyId,
                        event.sectionId,
                        event.questionId,
                        event.optionItemId,
                        event.selectedValue
                    )
                }
            }*/
        }
    }

    private fun saveOrUpdateSectionAnswers(
        surveyId: Int,
        sectionId: Int,
        didiId: Int,
        questionId: Int,
        optionsItem: List<OptionItemEntity>,
        questionEntity: QuestionEntity
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val isQuestionAlreadyAnswered =
                    questionScreenUseCase.saveSectionAnswerUseCase.isQuestionAlreadyAnswered(
                        didiId = didiId,
                        sectionId = sectionId,
                        questionId = questionId
                    )
                if (isQuestionAlreadyAnswered > 0) {
                    questionScreenUseCase.saveSectionAnswerUseCase.updateSectionAnswerForDidi(
                        didiId = didiId,
                        questionId = questionId,
                        sectionId = sectionId,
                        optionItems = optionsItem,
                        questionType = questionEntity.type ?: BLANK_STRING,
                        questionSummary = questionEntity.questionSummary ?: BLANK_STRING
                    )
                } else {
                    questionScreenUseCase.saveSectionAnswerUseCase.saveSectionAnswerForDidi(
                        questionId = questionId,
                        didiId = didiId,
                        sectionId = sectionId,
                        surveyId = surveyId,
                        questionType = questionEntity.type ?: BLANK_STRING,
                        questionSummary = questionEntity.questionSummary ?: BLANK_STRING,
                        optionItems = optionsItem

                    )
                }
            } catch (ex: Exception) {
                BaselineLogger.e(
                    "QuestionScreenViewModel",
                    "saveOrUpdateSectionAnswers -> questionId = $questionId,\n" +
                            "                        didiId = $didiId,\n" +
                            "                        sectionId = $sectionId,\n" +
                            "                        surveyId = $surveyId,\n" +
                            "                        questionType = ${questionEntity.type ?: BLANK_STRING},\n" +
                            "                        questionSummary = ${questionEntity.questionSummary ?: BLANK_STRING},\n" +
                            "                        optionItems = $optionsItem ",
                    ex
                )
            }
        }
    }

    override fun performSearchQuery(
        queryTerm: String, isFilterApplied: Boolean, fromScreen: String
    ) {
        val filteredList = ArrayList<QuestionEntity>()
        if (queryTerm.isNotEmpty()) {
            sectionDetail.value.questionList.forEach { question ->
                if (question.questionDisplay?.lowercase()
                        ?.contains(queryTerm.lowercase()) == true
                ) {
                    filteredList.add(question)
                }
            }
        } else {
            filteredList.addAll(sectionDetail.value.questionList)
        }
        _filterSectionList.value = filterSectionList.value.copy(
            questionList = filteredList
        )
    }

}
