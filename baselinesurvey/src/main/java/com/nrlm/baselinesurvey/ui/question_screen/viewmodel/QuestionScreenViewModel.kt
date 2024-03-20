package com.nrlm.baselinesurvey.ui.question_screen.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.database.entity.DidiIntoEntity
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.InputTypeQuestionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.datamodel.ConditionsDto
import com.nrlm.baselinesurvey.model.datamodel.QuestionList
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.Constants.ResultType
import com.nrlm.baselinesurvey.ui.common_components.common_events.EventWriterEvents
import com.nrlm.baselinesurvey.ui.common_components.common_events.SearchEvent
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.QuestionScreenUseCase
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionEntityState
import com.nrlm.baselinesurvey.ui.question_screen.presentation.QuestionScreenEvents
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.QuestionTypeEvent
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.checkCondition
import com.nrlm.baselinesurvey.utils.convertToOptionItemEntity
import com.nrlm.baselinesurvey.utils.findIndexOfListById
import com.nrlm.baselinesurvey.utils.findQuestionEntityStateById
import com.nrlm.baselinesurvey.utils.findQuestionForQuestionId
import com.nrlm.baselinesurvey.utils.getOptionItemEntityFromInputTypeQuestionAnswer
import com.nrlm.baselinesurvey.utils.sortedBySectionOrder
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.updateOptionItemEntityListStateForQuestionByCondition
import com.nudge.core.enums.EventType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuestionScreenViewModel @Inject constructor(
    private val questionScreenUseCase: QuestionScreenUseCase,
    private val eventsWriterHelperImpl: EventWriterHelperImpl
): BaseViewModel() {

    private val _loaderState = mutableStateOf<LoaderState>(LoaderState())
    val loaderState: State<LoaderState> get() = _loaderState

    private val _sectionDetail = mutableStateOf<SectionListItem>(
        SectionListItem(
            languageId = 2
        )
    )
    private val sectionDetail: State<SectionListItem> get() = _sectionDetail

    private val _sectionsList = mutableStateOf<List<SectionEntity>>(emptyList())
    val sectionsList: State<List<SectionEntity>> get() = _sectionsList

    /*val totalQuestionCount = sectionDetail.value.questionList.size
    val answeredQuestionCount =
        mutableStateOf(sectionDetail.value.questionAnswerMapping.values.size)*/

    val showExpandedImage = mutableStateOf(false)

    val expandedImagePath = mutableStateOf("")

    private val _inputTypeQuestionAnswerEntityList = mutableStateOf<List<InputTypeQuestionAnswerEntity>>(emptyList())
    val inputTypeQuestionAnswerEntityList: State<List<InputTypeQuestionAnswerEntity>> get() = _inputTypeQuestionAnswerEntityList

    private val _questionEntityStateList = mutableStateListOf<QuestionEntityState>()
    val questionEntityStateList: SnapshotStateList<QuestionEntityState> get() = _questionEntityStateList

    private val _filterSectionList = mutableStateOf<SectionListItem>(
        SectionListItem(
            languageId = 2
        )
    )

    val filterSectionList: State<SectionListItem> get() = _filterSectionList

    val answeredQuestionCount = mutableSetOf<Int>()
    val totalQuestionCount = mutableIntStateOf(0)

    val isSectionCompleted = mutableStateOf(false)
    val contentMapping = mutableStateOf<Map<String, ContentEntity>>(mutableMapOf())

    private val _formResponseEntityToQuestionMap =
        mutableStateOf(mutableMapOf<Int, List<FormQuestionResponseEntity>>())
    val formResponseEntityToQuestionMap: State<Map<Int, List<FormQuestionResponseEntity>>> get() = _formResponseEntityToQuestionMap

    var didiDetails: SurveyeeEntity? = null

    fun initQuestionScreenHandler(surveyeeId: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _sectionsList.value = questionScreenUseCase.getSectionsListUseCase.invoke(surveyeeId)
                .sortedBySectionOrder()
        }
    }

    suspend fun getFormQuestionResponseEntityLive(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        didiId: Int
    ): LiveData<List<FormQuestionResponseEntity>> {
        return questionScreenUseCase.getFormQuestionResponseUseCase.getFormResponsesForQuestionLive(
            surveyId,
            sectionId,
            questionId,
            didiId
        )
    }

    suspend fun getDidiInfoObjectLive(didiId: Int): LiveData<List<DidiIntoEntity>> {
        return questionScreenUseCase.getSurveyeeDetailsUserCase.getDidiInfoObjectLive(didiId)
    }

    var formResponsesForQuestionLive: LiveData<List<FormQuestionResponseEntity>> = MutableLiveData(mutableListOf())

    var didiInfoObjectLive: LiveData<List<DidiIntoEntity>> = MutableLiveData()

    var optionItemEntityList = emptyList<OptionItemEntity>()
    suspend fun getFormQuestionsOptionsItemEntityList(
        surveyId: Int,
        sectionId: Int,
        questionId: Int
    ): List<OptionItemEntity> {
        return questionScreenUseCase.getFormQuestionResponseUseCase.invoke(
            surveyId,
            sectionId,
            questionId
        )
    }

    fun init(surveyId: Int, sectionId: Int, surveyeeId: Int) {
        onEvent(LoaderEvent.UpdateLoaderState(true))
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            didiDetails = questionScreenUseCase.getSurveyeeDetailsUserCase.invoke(surveyeeId)

            val selectedlanguageId = questionScreenUseCase.getSectionUseCase.getSelectedLanguage()
            _sectionDetail.value =
                questionScreenUseCase.getSectionUseCase.invoke(
                    sectionId,
                    surveyId,
                    selectedlanguageId
                )
            val questionAnswerMap = mutableMapOf<Int, List<OptionItemEntity>>()
            _inputTypeQuestionAnswerEntityList.value =
                questionScreenUseCase.getSectionUseCase.getInputTypeQuestionAnswers(
                    surveyId = surveyId,
                    sectionId = sectionId,
                    didiId = surveyeeId
                )

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

            updateQuestionAnswerMapForNumericInputQuestions()

            _filterSectionList.value = _sectionDetail.value
            contentMapping.value = getContentData()
            initQuestionEntityStateList()

            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
            getFormResponseCountsForSection(surveyId, sectionId, surveyeeId)
        }

    }

    private suspend fun getFormResponseCountsForSection(
        surveyId: Int,
        sectionId: Int,
        surveyeeId: Int
    ) {
        val formQuestionResponseEntityList =
            questionScreenUseCase.getFormQuestionResponseUseCase.getFormQuestionCountForSection(
                surveyId = surveyId,
                sectionId = sectionId,
                didiId = surveyeeId
            )
        val map = formQuestionResponseEntityList.groupBy { it.questionId }
        _formResponseEntityToQuestionMap.value.putAll(map)
        val tempList = questionEntityStateList.toList()
            .filter { it.questionEntity?.type == QuestionType.Form.name }
        map.keys.forEach { questionId ->
            onEvent(
                QuestionScreenEvents.UpdateAnsweredQuestionCount(
                    tempList.find { it.questionId == questionId }!!,
                    false
                )
            )
        }
    }

    private fun updateQuestionAnswerMapForNumericInputQuestions() {

        if (inputTypeQuestionAnswerEntityList.value.isEmpty())
            return

        val questionAnswerMap = sectionDetail.value.questionAnswerMapping.toMutableMap()
        _inputTypeQuestionAnswerEntityList.value.forEach {
            val optionItemEntity = it.getOptionItemEntityFromInputTypeQuestionAnswer(sectionDetail.value)
            if (!questionAnswerMap.containsKey(it.questionId)) {
                questionAnswerMap.put(it.questionId, listOf(optionItemEntity!!))
            } else {
                val mList = mutableListOf<OptionItemEntity>()
                mList.addAll(questionAnswerMap[it.questionId]!!)
                mList.add(optionItemEntity!!)
                questionAnswerMap.put(it.questionId, mList)
            }
        }
        _sectionDetail.value = _sectionDetail.value.copy(
            questionAnswerMapping = questionAnswerMap
        )
    }

    private fun initQuestionEntityStateList() {
        sectionDetail.value.questionList.forEach { questionEntity ->
            var questionEntityState = QuestionEntityState(questionId = questionEntity.questionId, questionEntity = questionEntity,
                optionItemEntityState = emptyList(), answerdOptionList = emptyList(), showQuestion = !questionEntity.isConditional)
            val optionItemEntityStateList: List<OptionItemEntityState> = getOptionItemEntityStateListFromMap(
                sectionDetail.value.optionsItemMap[questionEntity.questionId]
            )
            questionEntityState = questionEntityState.copy(
                optionItemEntityState = optionItemEntityStateList.distinctBy { it.optionId}
            )
            sectionDetail.value.optionsItemMap.forEach { optionItemMap ->

            }
            filterSectionList.value?.questionAnswerMapping?.forEach { optionItemMap ->
                val mOptionItemEntity =
                    getAnswerOptionItemEntityListFromMap(optionItemMap, questionEntity.questionId)
                questionEntityState = questionEntityState.copy(
                    answerdOptionList = mOptionItemEntity
                )
            }
            _questionEntityStateList.add(questionEntityState)
        }
        _questionEntityStateList.sortedBy { it.questionId }

        updateQuestionEntityStateForAnsweredQuestions(questionEntityStateList.toList())

        updateQuestionEntityStateForConditionalQuestions(questionEntityStateList.toList())

    }

    private fun updateQuestionEntityStateForConditionalQuestions(questionEntityStateList: List<QuestionEntityState>) {
        questionEntityStateList.forEach { questionEntityState ->
            sectionDetail.value.questionAnswerMapping[questionEntityState.questionId]?.forEach { optionItemEntity ->
                onEvent(
                    QuestionTypeEvent.UpdateConditionQuestionStateForSingleOption(
                        questionEntityState,
                        optionItemEntity
                    )
                )
                onEvent(
                    QuestionTypeEvent.UpdateConditionQuestionStateForMultipleOption(
                        questionEntityState,
                        listOf(optionItemEntity)
                    )
                )
            }
        }
    }

    private fun updateQuestionEntityStateForAnsweredQuestions(questionEntityStateList: List<QuestionEntityState>) {
        questionEntityStateList.filter { !it.showQuestion }.forEach { questionEntityState ->
            when (questionEntityState.questionEntity?.type) {
                QuestionType.InputNumber.name -> {
                    if (inputTypeQuestionAnswerEntityList.value.map { it.questionId }
                            .contains(questionEntityState.questionId) && !questionEntityState.showQuestion) {
                        updateQuestionStateVisibilityForAnsweredQuestions(
                            questionEntityState.questionId,
                            true
                        )
                    }
                }

                else -> {
                    if (sectionDetail.value.questionAnswerMapping.containsKey(questionEntityState.questionId) && !questionEntityState.showQuestion) {
                        updateQuestionStateVisibilityForAnsweredQuestions(
                            questionEntityState.questionId,
                            true
                        )
                    }
                }
            }
        }

    }

    private fun updateQuestionStateVisibilityForAnsweredQuestions(questionId: Int?, showQuestion: Boolean) {
        var questionToShow = questionEntityStateList.findQuestionEntityStateById(questionId)
        questionToShow = questionToShow?.copy(
                showQuestion = showQuestion
            )
        updateQuestionEntityStateList(questionToShow)
    }

    private fun getOptionItemEntityStateListFromMap(optionItemList: List<OptionItemEntity>?): List<OptionItemEntityState> {
        val optionItemEntityStateList = mutableListOf<OptionItemEntityState>()
        optionItemList?.let { optionList ->
            optionList.forEach {
                val mOptionItemEntityState = OptionItemEntityState(
                    it.optionId,
                    optionItemEntity = it,
                    showQuestion = !it.conditional
                )
                optionItemEntityStateList.add(mOptionItemEntityState)
            }
        }
        return optionItemEntityStateList
    }

    private fun getAnswerOptionItemEntityListFromMap(optionItemMap: Map.Entry<Int, List<OptionItemEntity>>, questionId: Int?): List<OptionItemEntity> {
        val mOptionItemEntity = emptyList<OptionItemEntity>()
        if (optionItemMap.key == questionId) {
            val mOptionItemEntity = mutableListOf<OptionItemEntity>()
            optionItemMap.value.forEach {
                mOptionItemEntity.add(it)
            }
        }
        return mOptionItemEntity
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
                    onEvent(
                        EventWriterEvents.UpdateSectionStatusEvent(
                            event.surveyId,
                            event.sectionId,
                            event.didiId,
                            event.sectionStatus
                        )
                    )
                    questionScreenUseCase.updateSectionProgressUseCase.invoke(
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
                        eventsWriterHelperImpl.createUpdateSectionStatusEvent(
                            event.surveyId,
                            event.sectionId,
                            event.didiId,
                            event.sectionStatus
                        )
                    questionScreenUseCase.eventsWriterUseCase.invoke(
                        events = updateSectionStatusEvent,
                        eventType = EventType.STATEFUL
                    )
                }
            }

            is EventWriterEvents.SaveAnswerEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    if (event.questionType == QuestionType.Form.name) {
                        val saveAnswerEvent =
                            eventsWriterHelperImpl.createSaveAnswerEventForFormTypeQuestion(
                                surveyId = event.surveyId,
                                sectionId = event.sectionId,
                                didiId = event.didiId,
                                questionId = event.questionId,
                                questionType = event.questionType,
                                questionTag = event.questionTag,
                                saveAnswerEventOptionItemDtoList = event.saveAnswerEventOptionItemDtoList
                            )
                        questionScreenUseCase.eventsWriterUseCase.invoke(
                            events = saveAnswerEvent,
                            eventType = EventType.STATEFUL
                        )
                    } else {
                        val saveAnswerEvent = eventsWriterHelperImpl.createSaveAnswerEvent(
                            surveyId = event.surveyId,
                            sectionId = event.sectionId,
                            didiId = event.didiId,
                            questionId = event.questionId,
                            questionType = event.questionType,
                            questionTag = event.questionTag,
                            saveAnswerEventOptionItemDtoList = event.saveAnswerEventOptionItemDtoList
                        )
                        questionScreenUseCase.eventsWriterUseCase.invoke(
                            events = saveAnswerEvent,
                            eventType = EventType.STATEFUL
                        )
                    }

                    if (!event.showConditionalQuestion) {
                        onEvent(
                            EventWriterEvents.UpdateConditionalAnswerEvent(
                                event.surveyId,
                                event.sectionId,
                                event.didiId,
                                event.questionId,
                                event.saveAnswerEventOptionItemDtoList
                            )
                        )
                    }
                }
            }

            is EventWriterEvents.UpdateConditionalAnswerEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val undoConditionalOptionEventsList = mutableListOf<OptionItemEntityState>()
                    val tempQuestionEntityStateList = questionEntityStateList.toList()
                    val question =
                        tempQuestionEntityStateList.find { it.questionId == event.questionId }
                    event.saveAnswerEventOptionItemDtoList.forEach { saveAnswerEventOptionItemDto ->
                        question?.optionItemEntityState?.forEach { optionItemEntityState ->
                            if (optionItemEntityState.optionId != saveAnswerEventOptionItemDto.optionId) {
                                undoConditionalOptionEventsList.add(optionItemEntityState)
                            }
                        }
                    }
                    val conditionalQuestions = mutableListOf<QuestionList>()
                    undoConditionalOptionEventsList.forEach { optionItemEntityState ->
                        optionItemEntityState.optionItemEntity?.conditions?.forEach { conditionsDto ->
                            conditionalQuestions.addAll(conditionsDto?.resultList ?: emptyList())
                        }
                    }
                    conditionalQuestions.forEach { questionList ->
                        questionScreenUseCase.eventsWriterUseCase.invoke(
                            events = eventsWriterHelperImpl.createSaveAnswerEvent(
                                surveyId = event.surveyId,
                                sectionId = event.sectionId,
                                didiId = event.didiId,
                                questionId = questionList.questionId ?: 0,
                                questionType = questionList.type ?: BLANK_STRING,
                                questionTag = questionList.attributeTag ?: -1,
                                showQuestion = false,
                                saveAnswerEventOptionItemDtoList = emptyList()
                            ),
                            eventType = EventType.STATEFUL
                        )
                    }
                }
            }

            is QuestionScreenEvents.UpdateQuestionAnswerMappingForUi -> {
                val questionAnswerMapping =
                    _sectionDetail.value.questionAnswerMapping.toMutableMap()
                questionAnswerMapping[event.question.questionId ?: -1] = event.mOptionItem
                _sectionDetail.value = _sectionDetail.value.copy(
                    questionAnswerMapping = questionAnswerMapping
                )

                _filterSectionList.value = sectionDetail.value
            }

            is QuestionScreenEvents.UpdateInputTypeQuestionAnswerEntityForUi -> {
                val mInputTypeQuestionAnswerEntityList = _inputTypeQuestionAnswerEntityList.value.toMutableList()

                val isAnswerAlreadyMarked = mInputTypeQuestionAnswerEntityList.find { it.optionId == event.inputTypeQuestionAnswerEntity.optionId }
                if (isAnswerAlreadyMarked == null)
                    mInputTypeQuestionAnswerEntityList.add(event.inputTypeQuestionAnswerEntity)
                else {
                    val index = mInputTypeQuestionAnswerEntityList.map { it.optionId }.indexOf(isAnswerAlreadyMarked.optionId)
                    mInputTypeQuestionAnswerEntityList.removeAt(index = index)
                    mInputTypeQuestionAnswerEntityList.add(index, event.inputTypeQuestionAnswerEntity)
                }

                _inputTypeQuestionAnswerEntityList.value = mInputTypeQuestionAnswerEntityList
            }

            is QuestionScreenEvents.RatioTypeQuestionAnswered -> {
                updateQuestionAnswerState(event.questionId, listOf(event.optionItemEntity))

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

            is QuestionScreenEvents.InputTypeQuestionAnswered -> {
                saveInputTypeQuestionAnswer(
                    surveyId = event.surveyId,
                    sectionId = event.sectionId,
                    questionId = event.questionId,
                    optionId = event.optionItemId,
                    didiId = event.didiId,
                    inputValue = event.inputValue
                )
                answeredQuestionCount.add(event.questionId)
            }

            is QuestionScreenEvents.SaveMiscTypeQuestionAnswers -> {
                saveOrUpdateMiscTypeQuestionAnswers(
                    didiId = event.surveyeeId,
                    questionEntityState = event.questionEntityState,
                    optionItemEntity = event.optionItemEntity,
                    selectedValue = event.selectedValue
                )
            }

            is QuestionTypeEvent.UpdateConditionQuestionStateForSingleOption ->  {
                if (event.optionItemEntity.conditions == null) {
                    if (event.questionEntityState?.questionEntity?.type?.equals(QuestionType.RadioButton.name ,true) == true
                        || event.questionEntityState?.questionEntity?.type?.equals(QuestionType.List.name ,true) == true) {
                        val questionToUpdate = questionEntityStateList.find { it.questionId == event.questionEntityState?.questionId }
                        questionToUpdate?.optionItemEntityState?.forEach { optionItemEntity ->
                            optionItemEntity.optionItemEntity?.conditions?.forEach { conditionDto ->
                                updateQuestionStateForCondition(
                                    conditionResult = false,
                                    conditionDto
                                )
                            }
                        }
                    }
                }

                event.optionItemEntity.conditions?.forEach { conditionsDto ->
                    when (event.questionEntityState?.questionEntity?.type) {
                        QuestionType.RadioButton.name,
                        QuestionType.List.name -> {

                            //Hide conditional questions for the unselected values.
                            val questionToUpdate = questionEntityStateList.find { it.questionId == event.questionEntityState?.questionId && it.showQuestion }
                            val unselectedOption = questionToUpdate?.optionItemEntityState?.filter { it.optionId != event.optionItemEntity.optionId }
                            unselectedOption?.forEach { optionItemEntityState ->
                                optionItemEntityState.optionItemEntity?.conditions?.forEach { conditionsDto ->
                                    val mConditionCheckResult = conditionsDto?.checkCondition(event.optionItemEntity.display ?: BLANK_STRING)
                                    updateQuestionStateForCondition(conditionResult = mConditionCheckResult == true, conditionsDto)
                                    conditionsDto?.resultList?.forEach { subQuestion ->
                                        subQuestion.options?.forEach { subQuestionOption ->
                                            subQuestionOption?.conditions?.forEach { subConditionDto ->
                                                val mSubConditionCheckResult = subConditionDto?.checkCondition(event.optionItemEntity.display ?: BLANK_STRING)
                                                updateQuestionStateForCondition(conditionResult = mSubConditionCheckResult == true, subConditionDto)
                                            }
                                        }
                                    }
                                }
                            }

                            val conditionCheckResult = conditionsDto?.checkCondition(event.optionItemEntity.display ?: BLANK_STRING)
                            updateQuestionStateForCondition(conditionResult = conditionCheckResult == true, conditionsDto)
                        }
                        QuestionType.SingleSelectDropdown.name,
                        QuestionType.SingleSelectDropDown.name -> {
                            val conditionCheckResult = conditionsDto?.checkCondition(event.optionItemEntity.selectedValue ?: BLANK_STRING)
                            updateQuestionStateForCondition(conditionResult = conditionCheckResult == true, conditionsDto)
                        }
                    }
                }
            }

            is QuestionTypeEvent.UpdateConditionQuestionStateForInputNumberOptions -> {

                if (event.optionItemEntity.conditions == null) {
                    val questionToUpdate = questionEntityStateList.find { it.questionId == event.questionEntityState?.questionId }
                    questionToUpdate?.optionItemEntityState?.forEach { optionItemEntity ->
                        optionItemEntity.optionItemEntity?.conditions?.forEach { conditionDto ->
                            updateQuestionStateForCondition(
                                conditionResult = false,
                                conditionDto
                            )
                        }
                    }
                }


                event.optionItemEntity.conditions?.forEach { conditionsDto ->

                    //Hide conditional questions for the unselected values.
                    val questionToUpdate = questionEntityStateList.find { it.questionId == event.questionEntityState?.questionId && it.showQuestion }
                    val unselectedOption = questionToUpdate?.optionItemEntityState?.filter { it.optionId != event.optionItemEntity.optionId }
                    unselectedOption?.forEach { optionItemEntityState ->
                        optionItemEntityState.optionItemEntity?.conditions?.forEach { conditionsDto ->
                            val mConditionCheckResult = conditionsDto?.checkCondition(event.optionItemEntity.display ?: BLANK_STRING)
                            updateQuestionStateForCondition(conditionResult = mConditionCheckResult == true, conditionsDto)
                            conditionsDto?.resultList?.forEach { subQuestion ->
                                subQuestion.options?.forEach { subQuestionOption ->
                                    subQuestionOption?.conditions?.forEach { subConditionDto ->
                                        val mSubConditionCheckResult = subConditionDto?.checkCondition(event.optionItemEntity.display ?: BLANK_STRING)
                                        updateQuestionStateForCondition(conditionResult = mSubConditionCheckResult == true, subConditionDto)
                                    }
                                }
                            }
                        }
                    }

                    if (event.optionItemEntity.selectedValue != "0") {
                        val conditionCheckResult = conditionsDto?.checkCondition(
                            event.optionItemEntity.display ?: BLANK_STRING
                        )
                        updateQuestionStateForCondition(
                            conditionResult = conditionCheckResult == true,
                            conditionsDto
                        )
                    } else {
                        updateQuestionStateForCondition(
                            conditionResult = false,
                            conditionsDto
                        )
                    }

                }
            }

            is QuestionTypeEvent.UpdateConditionQuestionStateForMultipleOption -> {
                val mOptionItemList = event.questionEntityState?.optionItemEntityState?.toList()
                val unselectedOptions = mutableListOf<OptionItemEntityState>()
                // When All unselected
                if (event.optionItemEntityList.isEmpty()) {
                    unselectedOptions.addAll(mOptionItemList ?: emptyList())
                }
                mOptionItemList?.forEach {
                    event.optionItemEntityList?.map { it.optionId }?.forEach { selectOptionId ->
                        if (it.optionId != selectOptionId) {
                            unselectedOptions.add(it)
                        }
                    }
                }

                unselectedOptions.distinctBy { it.optionId }.forEach { unselectedOptionItemEntityState ->
                    unselectedOptionItemEntityState.optionItemEntity?.conditions?.let {
                        it.forEach { conditionsDto ->
                            // When All unselected
                            if (event.optionItemEntityList.isEmpty()) {
                                val conditionCheckResult = conditionsDto?.checkCondition(BLANK_STRING)
                                if (conditionsDto?.resultType?.equals(ResultType.Questions.name, true) == true)
                                    updateQuestionStateForCondition(conditionResult = conditionCheckResult == true, conditionsDto)
                            }
                            event.optionItemEntityList.forEach { optionItemEntity ->
                                val conditionCheckResult = conditionsDto?.checkCondition(optionItemEntity.display  ?: BLANK_STRING)
                                if (conditionsDto?.resultType?.equals(ResultType.Questions.name, true) == true)
                                    updateQuestionStateForCondition(conditionResult = conditionCheckResult == true, conditionsDto)
                                /*if (conditionsDto?.resultType?.equals(ResultType.Options.name, true) == true)
                                    updateOptionStateForCondition(conditionResult = conditionCheckResult == true, conditionsDto, unselectedOptionItemEntityState.optionItemEntity)*/
                            }
                        }
                    }
                }

                event.optionItemEntityList.forEach { optionItemEntity ->
                    optionItemEntity.conditions?.let {
                        it.forEach { conditionsDto ->
                            val conditionCheckResult = conditionsDto?.checkCondition(optionItemEntity.display ?: BLANK_STRING)
                            if (conditionsDto?.resultType?.equals(ResultType.Questions.name, true) == true)
                                updateQuestionStateForCondition(conditionResult = conditionCheckResult == true, conditionsDto)
                            if (conditionsDto?.resultType?.equals(ResultType.Options.name, true) == true)
                                updateOptionStateForCondition(conditionResult = conditionCheckResult == true, conditionsDto, optionItemEntity)
                        }
                    }
                }
            }

            is QuestionScreenEvents.UpdateAnsweredQuestionCount -> {
                try {
                    val tempList = questionEntityStateList.toList()
                    viewModelScope.launch(Dispatchers.IO) {
                        if (event.isAllMultipleTypeQuestionUnanswered) {
                            event.question.questionId?.let {
                                if (answeredQuestionCount.contains(it))
                                    answeredQuestionCount.remove(it)
                            }
                        } else {
                            event.question.questionId?.let { answeredQuestionCount.add(it) }
                        }
                        totalQuestionCount.intValue = tempList.filter { it.showQuestion }.distinctBy { it.questionId }.size
                        delay(100)
                        withContext(Dispatchers.Main) {
                            isSectionCompleted.value =
                                answeredQuestionCount.size == totalQuestionCount.intValue || answeredQuestionCount.size > totalQuestionCount.intValue
                        }
                    }
                } catch (ex: Exception) {
                    Log.e("TAG", "onEvent: exception; ${ex.message}", ex)
                }
            }

            is EventWriterEvents.UpdateMissionActivityTaskStatus -> {
                CoroutineScope(Dispatchers.IO).launch {
                    eventsWriterHelperImpl.markMissionActivityTaskInProgress(
                        missionId = event.missionId,
                        activityId = event.activityId,
                        taskId = event.taskId,
                        status = event.status
                    )
                }
            }

            is EventWriterEvents.UpdateMissionActivityTaskStatusEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val eventList = eventsWriterHelperImpl.getMissionActivityTaskEventList(
                        missionId = event.missionId,
                        activityId = event.activityId,
                        taskId = event.taskId,
                        status = event.status
                    )
                    eventList.forEach { event ->
                        questionScreenUseCase.eventsWriterUseCase.invoke(
                            events = event,
                            eventType = EventType.STATEFUL
                        )
                    }
                }
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

    private suspend fun updateMissionActivityTaskStatus(didiId: Int, sectionStatus: SectionStatus) {
        val activityForSubjectDto = eventsWriterHelperImpl.getActivityFromSubjectId(didiId)
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

    private fun saveOrUpdateMiscTypeQuestionAnswers(
        didiId: Int,
        questionEntityState: QuestionEntityState,
        optionItemEntity: OptionItemEntity,
        selectedValue: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val isQuestionAlreadyAnswer =
                questionScreenUseCase.saveSectionAnswerUseCase.isQuestionAlreadyAnswered(
                    surveyId = questionEntityState.questionEntity?.surveyId ?: -1,
                    sectionId = questionEntityState.questionEntity?.sectionId ?: -1,
                    questionId = questionEntityState.questionId ?: -1,
                    didiId = didiId
                )
            val optionToSave = optionItemEntity.copy(selectedValue = selectedValue)
            if (isQuestionAlreadyAnswer > 0) {
                questionScreenUseCase.saveSectionAnswerUseCase.updateSectionAnswerForDidi(
                    didiId = didiId,
                    surveyId = questionEntityState.questionEntity?.surveyId ?: -1,
                    sectionId = questionEntityState.questionEntity?.sectionId ?: -1,
                    questionId = questionEntityState.questionId ?: -1,
                    optionItems = listOf(optionToSave),
                    questionSummary = questionEntityState.questionEntity?.questionSummary ?: BLANK_STRING,
                    questionType = questionEntityState.questionEntity?.type ?: BLANK_STRING
                )
            } else {
                questionScreenUseCase.saveSectionAnswerUseCase.saveSectionAnswerForDidi(
                    didiId = didiId,
                    surveyId = questionEntityState.questionEntity?.surveyId ?: -1,
                    sectionId = questionEntityState.questionEntity?.sectionId ?: -1,
                    questionId = questionEntityState.questionId ?: -1,
                    optionItems = listOf(optionToSave),
                    questionSummary = questionEntityState.questionEntity?.questionSummary ?: BLANK_STRING,
                    questionType = questionEntityState.questionEntity?.type ?: BLANK_STRING
                )
            }

        }

    }

    private fun updateQuestionAnswerState(questionId: Int, answeredOptionItemEntityList: List<OptionItemEntity>) {
        var questionToShow = questionEntityStateList.findQuestionEntityStateById(questionId)
        questionToShow = questionToShow?.copy(
            answerdOptionList = answeredOptionItemEntityList
        )
        val questionToShowIndex = questionEntityStateList.findIndexOfListById(questionId)
        if (questionToShowIndex != -1) {
            _questionEntityStateList.removeAt(questionToShowIndex)
            _questionEntityStateList.add(questionToShowIndex, questionToShow!!)
        }
    }

    override fun updateQuestionStateForCondition(conditionResult: Boolean, conditionsDto: ConditionsDto?) {
        conditionsDto?.let { conditions ->
            conditions.resultList.forEach { questionList ->
                var questionToShow = questionEntityStateList.findQuestionEntityStateById(questionList.questionId)
                val updatedOptionItemEntityStateList = questionToShow?.optionItemEntityState
                    ?.updateOptionItemEntityListStateForQuestionByCondition(conditionResult)
                questionToShow = questionToShow?.copy(
                    optionItemEntityState = updatedOptionItemEntityStateList!!,
                    showQuestion = conditionResult
                )
                updateQuestionEntityStateList(questionToShow)
            }
        }
    }

    fun updateOptionStateForCondition(conditionResult: Boolean, conditionsDto: ConditionsDto?, optionItemEntity: OptionItemEntity) {
        conditionsDto?.let { conditions ->
            conditions.resultList.forEach { questionList ->
                var questionToShow = questionEntityStateList.findQuestionEntityStateById(optionItemEntity.questionId)
                val optionToUpdate = mutableListOf<OptionItemEntityState>()
                questionList.options?.forEach { opt ->
                    if (questionToShow?.optionItemEntityState?.map { it.optionId }?.contains(opt?.optionId) == true) {
                        optionToUpdate.add(questionToShow?.optionItemEntityState?.find { it.optionId == opt?.optionId }!!)
                    }
                }

                val updatedOptionItemEntityStateList = mutableListOf<OptionItemEntityState>()

                if (optionToUpdate.isEmpty())
                    updatedOptionItemEntityStateList.addAll(questionToShow?.optionItemEntityState!!)

                questionToShow?.optionItemEntityState?.forEach { optState ->
                    if (optionToUpdate.map { it.optionId }.contains(optState.optionId)) {
                        updatedOptionItemEntityStateList.add(optState.copy(showQuestion = conditionResult))
                    } else {
                        updatedOptionItemEntityStateList.add(optState)
                    }
                }

                questionToShow = questionToShow?.copy(
                    optionItemEntityState = updatedOptionItemEntityStateList.distinctBy { it.optionId },
                    showQuestion = conditionResult
                )

                updateQuestionEntityStateList(questionToShow)
            }
        }
    }

    private fun updateQuestionEntityStateList(questionToShow: QuestionEntityState?) {
        val questionToShowIndex = questionEntityStateList.findIndexOfListById(questionToShow?.questionId)
        if (questionToShowIndex != -1) {
            _questionEntityStateList.removeAt(questionToShowIndex)
            var index = if (((questionToShow?.questionEntity?.order ?: 0) - 1) > questionToShowIndex) questionToShowIndex else (questionToShow?.questionEntity?.order ?: 0) - 1
            if (index > _questionEntityStateList.size)
                index = _questionEntityStateList.size
            _questionEntityStateList.add(if (index != -1) index else questionToShowIndex, questionToShow!!)
            _questionEntityStateList.distinctBy { it.questionId }
        }
    }

    private fun saveInputTypeQuestionAnswer(surveyId: Int,
                                            sectionId: Int,
                                            didiId: Int,
                                            questionId: Int,
                                            optionId: Int,
                                            inputValue: String)
    {
     CoroutineScope(Dispatchers.IO).launch {
         try {
             val isQuestionAlreadyAnswer =
                 questionScreenUseCase.saveSectionAnswerUseCase.isInputTypeQuestionAlreadyAnswered(
                     surveyId = surveyId,
                     sectionId = sectionId,
                     questionId = questionId,
                     didiId = didiId,
                     optionId = optionId
                 )

             if (isQuestionAlreadyAnswer > 0) {
                 questionScreenUseCase.saveSectionAnswerUseCase.updateInputTypeQuestionAnswer(
                     surveyId = surveyId,
                     sectionId = sectionId,
                     questionId = questionId,
                     didiId = didiId,
                     optionId = optionId,
                     inputValue = inputValue
                 )
             } else {
                 questionScreenUseCase.saveSectionAnswerUseCase.saveInputTypeQuestionAnswer(
                     surveyId = surveyId,
                     sectionId = sectionId,
                     questionId = questionId,
                     didiId = didiId,
                     optionId = optionId,
                     inputValue = inputValue
                 )
             }

         } catch (ex: Exception) {
             BaselineLogger.e(
                 "QuestionScreenViewModel",
                 "saveInputTypeQuestionAnswer -> questionId = $questionId,\n" +
                         "                        didiId = $didiId,\n" +
                         "                        sectionId = $sectionId,\n" +
                         "                        surveyId = $surveyId,\n" +
                         "                        optionId = $optionId"+
                         "                        inputValue = $inputValue"
                 , ex
             )
         }
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
                        questionId = questionId,
                        surveyId = surveyId
                    )

                val existingGridTypeAnswers = questionScreenUseCase.getSectionAnswersUseCase.getSectionAnswerForDidi(sectionId = sectionId, didiId = didiId).findQuestionForQuestionId(questionId)
                if (isQuestionAlreadyAnswered > 0) {
                    val finalAnswerList: MutableList<OptionItemEntity> = mutableListOf()
                    if (questionEntity.type?.equals(QuestionType.Grid.name) == true) {
//                        finalAnswerList.addAll(existingGridTypeAnswers?.optionItems ?: emptyList())
                        finalAnswerList.addAll(optionsItem)
                    } else {
                        finalAnswerList.addAll(optionsItem.distinctBy { it.optionId})
                    }
                    questionScreenUseCase.saveSectionAnswerUseCase.updateSectionAnswerForDidi(
                        didiId = didiId,
                        questionId = questionId,
                        surveyId = surveyId,
                        sectionId = sectionId,
                        optionItems = finalAnswerList,
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

    fun updateSaveUpdateState() {
        filterSectionList.value.questionAnswerMapping.forEach {
            answeredQuestionCount.add(it.key)
        }

        inputTypeQuestionAnswerEntityList.value.groupBy { it.optionId }.forEach {
            answeredQuestionCount.add(it.key)
        }
        val qesList = questionEntityStateList.toList()
        totalQuestionCount.intValue =
            qesList.filter { it.showQuestion }.distinctBy { it.questionId }.size
        // Log.d("TAG", "updateSaveUpdateState: questionEntityStateList.filter { it.showQuestion }.size: ${questionEntityStateList.filter { it.showQuestion }.size} answeredQuestionCount: $answeredQuestionCount ::: totalQuestionCount: ${totalQuestionCount.intValue}")
        isSectionCompleted.value =
            answeredQuestionCount.size == totalQuestionCount.intValue || answeredQuestionCount.size > totalQuestionCount.intValue
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

    private fun getContentData(
    ): Map<String, ContentEntity> {
        val map = mutableMapOf<String, ContentEntity>()
        _filterSectionList.value.contentData?.forEach { contentEntity ->
            contentEntity?.let { map.put(it.contentType, contentEntity) }
        }
        return map
    }

    fun getFormResponseItemCountForQuestion(questionId: Int?): Int {
        val formResponseItemListForQuestion = formResponseEntityToQuestionMap.value[questionId]
        return formResponseItemListForQuestion?.groupBy { it.referenceId }?.keys?.size ?: 0
    }
}
