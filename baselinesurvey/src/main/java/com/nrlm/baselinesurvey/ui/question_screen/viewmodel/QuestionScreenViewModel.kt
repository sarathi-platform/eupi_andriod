package com.nrlm.baselinesurvey.ui.question_screen.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.database.entity.DidiInfoEntity
import com.nrlm.baselinesurvey.database.entity.FormQuestionResponseEntity
import com.nrlm.baselinesurvey.database.entity.InputTypeQuestionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.FormResponseObjectDto
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
import com.nrlm.baselinesurvey.utils.CalculatorUtils
import com.nrlm.baselinesurvey.utils.DEFAULT_CALCULATION_RESULT
import com.nrlm.baselinesurvey.utils.checkCondition
import com.nrlm.baselinesurvey.utils.convertFormQuestionResponseEntityToSaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.utils.convertToOptionItemEntity
import com.nrlm.baselinesurvey.utils.findIndexOfListById
import com.nrlm.baselinesurvey.utils.findQuestionEntityStateById
import com.nrlm.baselinesurvey.utils.findQuestionForQuestionId
import com.nrlm.baselinesurvey.utils.getAutoCalculationConditionConditions
import com.nrlm.baselinesurvey.utils.getOptionItemEntityFromInputTypeQuestionAnswer
import com.nrlm.baselinesurvey.utils.sortedBySectionOrder
import com.nrlm.baselinesurvey.utils.states.LoaderState
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.toOptionItemStateList
import com.nrlm.baselinesurvey.utils.updateOptionItemEntityListStateForQuestionByCondition
import com.nrlm.baselinesurvey.utils.updateOptionsForNoneCondition
import com.nudge.auditTrail.domain.usecase.AuditTrailUseCase
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
class QuestionScreenViewModel @Inject constructor(
    private val questionScreenUseCase: QuestionScreenUseCase,
    private val eventsWriterHelperImpl: EventWriterHelperImpl,
    val  auditTrailUseCase: AuditTrailUseCase
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

    val didiDetails: MutableState<SurveyeeEntity?> = mutableStateOf(null)
    val didiInfoState: MutableState<DidiInfoEntity?> = mutableStateOf(null)

    var isEditAllowed: Boolean = true

    var isNoneMarkedForFormQuestion: MutableState<MutableMap<Int, Boolean>> =
        mutableStateOf(mutableMapOf())

    private var sectionDetailInDefaultLanguage = SectionListItem(
        languageId = 2
    )

    private var _formResponseObjectDtoList = mutableStateOf(mutableListOf<FormResponseObjectDto>())
    val formResponseObjectDtoList: State<List<FormResponseObjectDto>> get() = _formResponseObjectDtoList

    var referenceIdForFormWithNone: String = BLANK_STRING

    val formWithNoneOptionMarkedSet = mutableSetOf<Int>()

    val inputNumberQuestionMap = mutableMapOf<Int, List<InputTypeQuestionAnswerEntity>>()

    val calculatedResult: MutableState<Map<Int, String>> = mutableStateOf(mutableMapOf())

    fun initQuestionScreenHandler(surveyeeId: Int, subjectId: Int) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _sectionsList.value = questionScreenUseCase.getSectionsListUseCase.invoke(surveyeeId)
                .sortedBySectionOrder()
            val task =
                questionScreenUseCase.getPendingTaskCountLiveUseCase.getActivityFromSubjectId(
                    subjectId
                )
            if (task != null) {
                isEditAllowed = questionScreenUseCase.getPendingTaskCountLiveUseCase.isActivityComplete(
                    task.missionId,
                    task.activityId
                )
            }
        }
    }

    suspend fun getFormQuestionResponseEntity(
        surveyId: Int,
        sectionId: Int,
        didiId: Int
    ): List<FormQuestionResponseEntity> {
        return questionScreenUseCase.getFormQuestionResponseUseCase.getFormResponsesForSection(
            surveyId = surveyId,
            sectionId = sectionId,
            didiId = didiId
        )
    }

    suspend fun getDidiInfoObject(didiId: Int) {
        didiInfoState.value =
            questionScreenUseCase.getSurveyeeDetailsUserCase.getDidiIndoDetail(didiId)
    }

    suspend fun getDidiInfoObjectLive(didiId: Int): LiveData<List<DidiInfoEntity>> {
        return questionScreenUseCase.getSurveyeeDetailsUserCase.getDidiInfoObjectLive(didiId)
    }

    var formResponsesForQuestionLive: LiveData<List<FormQuestionResponseEntity>> = MutableLiveData(mutableListOf())

    var didiInfoObjectLive: LiveData<List<DidiInfoEntity>> = MutableLiveData()

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

            didiDetails.value = questionScreenUseCase.getSurveyeeDetailsUserCase.invoke(surveyeeId)

            val selectedlanguageId = questionScreenUseCase.getSectionUseCase.getSelectedLanguage()
            _sectionDetail.value =
                questionScreenUseCase.getSectionUseCase.invoke(
                    sectionId,
                    surveyId,
                    selectedlanguageId
                )
            sectionDetailInDefaultLanguage = questionScreenUseCase.getSectionUseCase.invoke(
                sectionId,
                surveyId,
                DEFAULT_LANGUAGE_ID
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
            BaselineLogger.d("TAG", "init: localAnswerList-> $localAnswerList")

            localAnswerList.forEach {
                if (!questionAnswerMap.containsKey(it.questionId)) {
                    questionAnswerMap.put(it.questionId, it.optionItems)
                }
            }

            BaselineLogger.d("TAG", "init: questionAnswerMap-> $questionAnswerMap")
            _sectionDetail.value = _sectionDetail.value.copy(
                questionAnswerMapping = questionAnswerMap
            )

            updateQuestionAnswerMapForNumericInputQuestions()

            inputNumberQuestionMap.clear()
            inputNumberQuestionMap.putAll(inputTypeQuestionAnswerEntityList.value.groupBy { it.questionId })

            _filterSectionList.value = _sectionDetail.value
            contentMapping.value = getContentData()
            initQuestionEntityStateList()


            getFormResponseCountsForSection(surveyId, sectionId, surveyeeId)
            updateSaveUpdateState()
            delay(300)
            onEvent(QuestionTypeEvent.UpdateAutoCalculateTypeQuestionValue)
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
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
        if (map.isEmpty())
            _formResponseEntityToQuestionMap.value.clear()
        else {
            _formResponseEntityToQuestionMap.value.clear()
            _formResponseEntityToQuestionMap.value.putAll(map)
        }
        val tempList = questionEntityStateList.toList()
            .filter {
                it.questionEntity?.type == QuestionType.Form.name || it
                    .questionEntity?.type == QuestionType.FormWithNone.name
            }

        answeredQuestionCount.clear()

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
            if (questionEntityState.questionEntity?.type == QuestionType.MultiSelect.name) {
                onEvent(
                    QuestionTypeEvent.UpdateConditionQuestionStateForMultipleOption(
                        questionEntityState,
                        sectionDetail.value.questionAnswerMapping[questionEntityState.questionId]
                            ?: emptyList()
                    )
                )
            } else if (questionEntityState.questionEntity?.type?.equals(
                    QuestionType.InputNumber.name,
                    true
                ) == true
            ) {

                inputTypeQuestionAnswerEntityList?.value?.filter { it.questionId == questionEntityState.questionId }
                    ?.let { inputTypeQuestionAnswerEntitiesForQuestion ->
                        val mOptionList = ArrayList<OptionItemEntity>()
                        inputTypeQuestionAnswerEntitiesForQuestion.forEach { inputTypeQuestionAnswerEntity ->

                            questionEntityState.optionItemEntityState.find { it.optionId == inputTypeQuestionAnswerEntity.optionId }?.optionItemEntity
                                ?.copy(selectedValue = inputTypeQuestionAnswerEntity.inputValue)
                                ?.let {
                                    mOptionList.add(it)
                                }
                        }

                        onEvent(
                            QuestionTypeEvent.UpdateConditionQuestionStateForInputNumberOptions(
                                questionEntityState = questionEntityState,
                                optionItemEntityList = mOptionList,
                                inputTypeQuestionEntity = inputTypeQuestionAnswerEntitiesForQuestion
                            )
                        )

                    }
            } else {
                sectionDetail.value.questionAnswerMapping[questionEntityState.questionId]?.forEach { optionItemEntity ->
                    onEvent(
                        QuestionTypeEvent.UpdateConditionQuestionStateForSingleOption(
                            questionEntityState,
                            optionItemEntity
                        )
                    )
                }
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
                    val mDidiDetails =
                        didiDetails.value?.copy(surveyStatus = event.sectionStatus.ordinal)
                    didiDetails.value = mDidiDetails
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
                    if (event.questionType == QuestionType.Form.name || event.questionType == QuestionType.FormWithNone.name) {
                        val saveAnswerEvent =
                            eventsWriterHelperImpl.createSaveAnswerEventForFormTypeQuestion(
                                surveyId = event.surveyId,
                                sectionId = event.sectionId,
                                didiId = event.didiId,
                                questionId = event.questionId,
                                questionType = event.questionType,
                                questionTag = event.questionTag,
                                questionDesc = event.questionDesc,
                                showQuestion = event.showConditionalQuestion,
                                referenceOptionList = sectionDetailInDefaultLanguage.optionsItemMap[event.questionId]?.toOptionItemStateList()
                                    ?: emptyList(),
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
                            questionDesc = event.questionDesc,
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
                                surveyId = event.surveyId,
                                sectionId = event.sectionId,
                                didiId = event.didiId,
                                questionId = event.questionId,
                                questionDesc = event.questionDesc,
                                saveAnswerEventOptionItemDtoList = event.saveAnswerEventOptionItemDtoList
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
                                questionDesc = event.questionDesc,
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
                //answeredQuestionCount.add(event.questionId)
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
                    if (event.questionEntityState?.questionEntity?.type?.equals(
                            QuestionType.RadioButton.name,
                            true
                        ) == true
                        || event.questionEntityState?.questionEntity?.type?.equals(
                            QuestionType.List.name,
                            true
                        ) == true
                    ) {
                        val questionToUpdate =
                            questionEntityStateList.find { it.questionId == event.questionEntityState?.questionId }
                        questionToUpdate?.optionItemEntityState?.forEach { optionItemEntity ->
                            optionItemEntity.optionItemEntity?.conditions?.forEach { conditionDto ->
                                updateQuestionStateForCondition(
                                    conditionResult = false,
                                    conditionDto
                                )
                            }
                        }

                        val unselectedOption =
                            questionToUpdate?.optionItemEntityState?.filter { it.optionId != event.optionItemEntity.optionId }
                        unselectedOption?.forEach { optionItemEntityState ->
                            optionItemEntityState.optionItemEntity?.conditions?.forEach { conditionsDto ->
                                val mConditionCheckResult = conditionsDto?.checkCondition(
                                    event.optionItemEntity.display ?: BLANK_STRING
                                )
                                updateQuestionStateForCondition(
                                    conditionResult = mConditionCheckResult == true,
                                    conditionsDto
                                )
                                if (mConditionCheckResult == false) {
                                    onEvent(
                                        QuestionTypeEvent.RemoveConditionalQuestionValuesForUnselectedOption(
                                            conditionsDto
                                        )
                                    )
                                }
                                conditionsDto?.resultList?.forEach { subQuestion ->
                                    subQuestion.options?.forEach { subQuestionOption ->
                                        subQuestionOption?.conditions?.forEach { subConditionDto ->
                                            val mSubConditionCheckResult =
                                                subConditionDto?.checkCondition(
                                                    event.optionItemEntity.display
                                                        ?: BLANK_STRING
                                                )
                                            updateQuestionStateForCondition(
                                                conditionResult = mSubConditionCheckResult == true,
                                                subConditionDto
                                            )
                                            if (mConditionCheckResult == false) {
                                                onEvent(
                                                    QuestionTypeEvent.RemoveConditionalQuestionValuesForUnselectedOption(
                                                        subConditionDto!!
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Hide 2nd level child question on first time load if Root question condition is not met.
                if (event.optionItemEntity.conditions.isNullOrEmpty()) {
                    if (event.questionEntityState?.questionEntity?.parentQuestionId != 0) {
                        val parentQuestion =
                            questionEntityStateList.toList()
                                .find { it.questionId == event.questionEntityState?.questionEntity?.parentQuestionId }
                        if (parentQuestion?.showQuestion == false) {
                            val questionToHide = event.questionEntityState?.copy(
                                showQuestion = false
                            )
                            updateQuestionEntityStateList(questionToHide)
                        }
                    }
                }

                // Handle actual condition based on response.
                event.optionItemEntity.conditions?.forEach { conditionsDto ->
                    when (event.questionEntityState?.questionEntity?.type) {
                        QuestionType.RadioButton.name,
                        QuestionType.List.name -> {

                            //Hide conditional questions for the unselected values.
                            val questionToUpdate =
                                questionEntityStateList.find { it.questionId == event.questionEntityState?.questionId && it.showQuestion }
                            val unselectedOption =
                                questionToUpdate?.optionItemEntityState?.filter { it.optionId != event.optionItemEntity.optionId }
                            unselectedOption?.forEach { optionItemEntityState ->
                                optionItemEntityState.optionItemEntity?.conditions?.forEach { conditionsDto ->
                                    val mConditionCheckResult = conditionsDto?.checkCondition(
                                        event.optionItemEntity.display ?: BLANK_STRING
                                    )
                                    updateQuestionStateForCondition(
                                        conditionResult = mConditionCheckResult == true,
                                        conditionsDto
                                    )
                                    if (mConditionCheckResult == false) {
                                        onEvent(
                                            QuestionTypeEvent.RemoveConditionalQuestionValuesForUnselectedOption(
                                                conditionsDto
                                            )
                                        )
                                    }
                                    conditionsDto?.resultList?.forEach { subQuestion ->
                                        subQuestion.options?.forEach { subQuestionOption ->
                                            subQuestionOption?.conditions?.forEach { subConditionDto ->
                                                val mSubConditionCheckResult =
                                                    subConditionDto?.checkCondition(
                                                        event.optionItemEntity.display
                                                            ?: BLANK_STRING
                                                    )
                                                updateQuestionStateForCondition(
                                                    conditionResult = mSubConditionCheckResult == true,
                                                    subConditionDto
                                                )
                                                if (mConditionCheckResult == false) {
                                                    onEvent(
                                                        QuestionTypeEvent.RemoveConditionalQuestionValuesForUnselectedOption(
                                                            subConditionDto!!
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Show conditional question based on selected response
                            val conditionCheckResult = conditionsDto?.checkCondition(event.optionItemEntity.display ?: BLANK_STRING)
                            updateQuestionStateForCondition(conditionResult = conditionCheckResult == true, conditionsDto)
                        }
                        QuestionType.SingleSelectDropdown.name,
                        QuestionType.SingleSelectDropDown.name -> {
                            // Show conditional question based on selected response
                            val conditionCheckResult = conditionsDto?.checkCondition(
                                event.optionItemEntity.values
                                    ?.find { it.id == event.optionItemEntity.selectedValueId }?.value /*event.optionItemEntity.selectedValue*/
                                    ?: BLANK_STRING
                            )
                            updateQuestionStateForCondition(
                                conditionResult = conditionCheckResult == true,
                                conditionsDto
                            )

                            if (conditionCheckResult == false) {
                                onEvent(
                                    QuestionTypeEvent.RemoveConditionalQuestionValuesForUnselectedOption(
                                        conditionsDto
                                    )
                                )
                            }
                        }
                    }
                }
            }

            is QuestionTypeEvent.UpdateConditionQuestionStateForInputNumberOptions -> {

                val mOptionItemList = event.questionEntityState?.optionItemEntityState?.toList()
                val unSelectedOptions = mutableListOf<OptionItemEntityState>()

                if (event.optionItemEntityList.isEmpty()) {
                    unSelectedOptions.addAll(mOptionItemList ?: emptyList())
                } else {
                    mOptionItemList?.filter {
                        !event.optionItemEntityList.map {
                            it
                                .optionId
                        }.contains(
                            it
                                .optionId
                        )
                    }?.let {
                        unSelectedOptions.addAll(it.toList())
                    }
                }

                unSelectedOptions.distinctBy { it.optionId }
                    .forEach { unseletecOptionItemEntityState ->
                        unseletecOptionItemEntityState?.optionItemEntity?.conditions?.let {
                            it.forEach { conditionsDto ->
                                if (event.optionItemEntityList.isEmpty()) {
                                    val conditionCheckResult =
                                        conditionsDto?.checkCondition(BLANK_STRING)
                                    if (conditionsDto?.resultType?.equals(
                                            ResultType.Questions.name,
                                            true
                                        ) == true
                                    ) {
                                        updateQuestionStateForCondition(
                                            conditionResult = conditionCheckResult == true,
                                            conditionsDto
                                        )
                                        if (conditionCheckResult == false) {
                                            onEvent(
                                                QuestionTypeEvent.RemoveConditionalQuestionValuesForUnselectedOption(
                                                    conditionsDto!!
                                                )
                                            )
                                        }
                                    }
                                    if (conditionsDto?.resultType?.equals(
                                            ResultType.NoneMarked.name,
                                            true
                                        ) == true
                                    ) {
                                        updateQuestionOptionStateForNoneCondition(
                                            conditionResult = conditionCheckResult == true,
                                            optionId = unseletecOptionItemEntityState.optionId ?: 0,
                                            conditionsDto = conditionsDto,
                                            questionId = event.questionEntityState?.questionId ?: 0,
                                            noneOptionUnselected = true
                                        )
                                    }
                                }

                                event.optionItemEntityList.forEach { optionItemEntity ->
                                    val conditionCheckResult = conditionsDto?.checkCondition(
                                        optionItemEntity.display ?: BLANK_STRING
                                    )
                                    if (conditionsDto?.resultType?.equals(
                                            ResultType.Questions.name,
                                            true
                                        ) == true
                                    ) {
                                        updateQuestionStateForCondition(
                                            conditionResult = conditionCheckResult == true,
                                            conditionsDto
                                        )
                                        if (conditionCheckResult == false) {
                                            onEvent(
                                                QuestionTypeEvent.RemoveConditionalQuestionValuesForUnselectedOption(
                                                    conditionsDto!!
                                                )
                                            )
                                        }
                                    }
                                    if (conditionsDto?.resultType?.equals(
                                            ResultType.NoneMarked.name,
                                            true
                                        ) == true
                                    )
                                        updateQuestionOptionStateForNoneCondition(
                                            conditionResult = conditionCheckResult == true,
                                            optionItemEntity.optionId ?: 0,
                                            conditionsDto,
                                            event.questionEntityState?.questionId ?: 0, true
                                        )
                                }

                            }
                        }
                    }

                event.optionItemEntityList.forEach { optionItemEntity ->
                    optionItemEntity.conditions?.let {
                        it.forEach { conditionsDto ->
                            if (optionItemEntity.selectedValue != "0") {
                                val conditionCheckResult = conditionsDto?.checkCondition(
                                    optionItemEntity.display ?: BLANK_STRING
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
                }
            }

            is QuestionTypeEvent.UpdateConditionQuestionStateForMultipleOption -> {
                if (event.questionEntityState?.questionEntity?.type != QuestionType.MultiSelect.name && event.questionEntityState?.questionEntity?.type != QuestionType.Grid.name)
                    return

                val mOptionItemList = event.questionEntityState?.optionItemEntityState?.toList()
                val unselectedOptions = mutableListOf<OptionItemEntityState>()
                // When All unselected
                if (event.optionItemEntityList.isEmpty()) {
                    unselectedOptions.addAll(mOptionItemList ?: emptyList())
                } else {
                    mOptionItemList?.filter {
                        event.optionItemEntityList.map { it.optionId }.contains(it.optionId) != true
                    }?.let { unselectedOptions.addAll(it.toList()) }
                }

                unselectedOptions.distinctBy { it.optionId }.forEach { unselectedOptionItemEntityState ->
                    unselectedOptionItemEntityState.optionItemEntity?.conditions?.let {
                        it.forEach { conditionsDto ->
                            // When All unselected
                            if (event.optionItemEntityList.isEmpty()) {
                                val conditionCheckResult =
                                    conditionsDto?.checkCondition(BLANK_STRING)
                                if (conditionsDto?.resultType?.equals(
                                        ResultType.Questions.name,
                                        true
                                    ) == true
                                ) {
                                    updateQuestionStateForCondition(
                                        conditionResult = conditionCheckResult == true,
                                        conditionsDto
                                    )
                                    if (conditionCheckResult == false) {
                                        onEvent(
                                            QuestionTypeEvent.RemoveConditionalQuestionValuesForUnselectedOption(
                                                conditionsDto!!
                                            )
                                        )
                                    }
                                }
                                if (conditionsDto?.resultType?.equals(
                                        ResultType.NoneMarked.name,
                                        true
                                    ) == true
                                )
                                    updateQuestionOptionStateForNoneCondition(
                                        conditionResult = conditionCheckResult == true,
                                        optionId = unselectedOptionItemEntityState.optionId ?: 0,
                                        conditionsDto = conditionsDto,
                                        questionId = event.questionEntityState?.questionId ?: 0,
                                        noneOptionUnselected = true
                                    )
                            }
                            event.optionItemEntityList.forEach { optionItemEntity ->
                                val conditionCheckResult = conditionsDto?.checkCondition(
                                    optionItemEntity.display ?: BLANK_STRING
                                )
                                if (conditionsDto?.resultType?.equals(
                                        ResultType.Questions.name,
                                        true
                                    ) == true
                                ) {
                                    updateQuestionStateForCondition(
                                        conditionResult = conditionCheckResult == true,
                                        conditionsDto
                                    )
                                    if (conditionCheckResult == false) {
                                        onEvent(
                                            QuestionTypeEvent.RemoveConditionalQuestionValuesForUnselectedOption(
                                                conditionsDto!!
                                            )
                                        )
                                    }
                                }
                                if (conditionsDto?.resultType?.equals(
                                        ResultType.NoneMarked.name,
                                        true
                                    ) == true
                                )
                                    updateQuestionOptionStateForNoneCondition(
                                        conditionResult = conditionCheckResult == true,
                                        optionItemEntity.optionId ?: 0,
                                        conditionsDto,
                                        event.questionEntityState?.questionId ?: 0, true
                                    )
                                /*if (conditionsDto?.resultType?.equals(ResultType.Options.name, true) == true)
                                    updateOptionStateForCondition(conditionResult = conditionCheckResult == true, conditionsDto, unselectedOptionItemEntityState.optionItemEntity)*/
                            }
                        }
                    }
                }

                event.optionItemEntityList.forEach { optionItemEntity ->
                    optionItemEntity.conditions?.let {
                        it.forEach { conditionsDto ->
                            val conditionCheckResult =
                                if (event.questionEntityState?.questionEntity?.type == QuestionType.SingleSelectDropdown.name ||
                                    event.questionEntityState?.questionEntity?.type == QuestionType.SingleSelectDropdown.name
                                ) conditionsDto?.checkCondition(
                                    optionItemEntity.selectedValue ?: BLANK_STRING
                                ) else conditionsDto?.checkCondition(
                                    optionItemEntity.display ?: BLANK_STRING
                                )
                            if (conditionsDto?.resultType?.equals(
                                    ResultType.Questions.name,
                                    true
                                ) == true
                            )
                                updateQuestionStateForCondition(
                                    conditionResult = conditionCheckResult == true,
                                    conditionsDto
                                )

                            if (conditionsDto?.resultType?.equals(
                                    ResultType.Options.name,
                                    true
                                ) == true
                            )
                                updateOptionStateForCondition(
                                    conditionResult = conditionCheckResult == true,
                                    conditionsDto,
                                    optionItemEntity
                                )

                            if (conditionsDto?.resultType?.equals(
                                    ResultType.NoneMarked.name,
                                    true
                                ) == true
                            )
                                updateQuestionOptionStateForNoneCondition(
                                    conditionResult = conditionCheckResult == true,
                                    optionItemEntity.optionId ?: 0,
                                    conditionsDto,
                                    event.questionEntityState?.questionId ?: 0
                                )
                        }
                    }
                }
            }

            is QuestionScreenEvents.UpdateAnsweredQuestionCount -> {
                try {
                    val tempList = questionEntityStateList.toList()
//                    viewModelScope.launch(Dispatchers.IO) {
                    if (event.isQuestionResponseUnanswered) {
                        event.question.questionId?.let {
                            if (answeredQuestionCount.contains(it))
                                answeredQuestionCount.remove(it)
                        }
                    } else {
                        event.question.questionId?.let { answeredQuestionCount.add(it) }
                    }
                    totalQuestionCount.intValue =
                        tempList.filter { it.showQuestion && it.questionEntity?.type != QuestionType.AutoCalculation.name }
                            .distinctBy { it.questionId }.size
//                        delay(100)
//                        withContext(Dispatchers.Main) {
                    isSectionCompleted.value =
                        answeredQuestionCount.size == totalQuestionCount.intValue || answeredQuestionCount.size > totalQuestionCount.intValue
//                        }
//                    }
                } catch (ex: Exception) {
                    BaselineLogger.e("TAG", "onEvent: exception; ${ex.message}", ex)
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

            is QuestionTypeEvent.RemoveConditionalQuestionValuesForUnselectedOption -> {
                if (event.questionConditionsDto.resultList.isNotEmpty()) {
                    event.questionConditionsDto.resultList.forEach { questionItem ->
                        val question = questionEntityStateList.toList()
                            .find { it.questionId == questionItem.questionId }

                        val isQuestionAnswered =
                            if (question?.questionEntity?.type == QuestionType.InputNumber.name) inputTypeQuestionAnswerEntityList.value.map { it.questionId }
                                .contains(question.questionId) else
                                sectionDetail.value.questionAnswerMapping.containsKey(question?.questionId)
                        if (isQuestionAnswered) {

                            CoroutineScope(Dispatchers.IO).launch {
                                questionScreenUseCase.saveSectionAnswerUseCase.deleteSectionResponseForQuestion(
                                    question = question,
                                    didiId = didiDetails.value?.didiId ?: 0,
                                    optionList = sectionDetail.value.optionsItemMap[question?.questionId]
                                        ?: listOf()
                                )
                            }
                            if (question?.questionEntity?.type == QuestionType.InputNumber.name) {
                                val updatedList =
                                    inputTypeQuestionAnswerEntityList.value.toMutableList()
                                val index =
                                    updatedList.map { it.questionId }.indexOf(question.questionId)
                                if (index != -1) {
                                    updatedList.removeAt(index)
                                    _inputTypeQuestionAnswerEntityList.value = updatedList
                                }
                            }
                            val questionAnswerMapping =
                                _sectionDetail.value.questionAnswerMapping.toMutableMap()
                            questionAnswerMapping.remove(question?.questionId)
                            _sectionDetail.value = _sectionDetail.value.copy(
                                questionAnswerMapping = questionAnswerMapping
                            )

                            _filterSectionList.value = sectionDetail.value

                            onEvent(
                                QuestionScreenEvents.UpdateAnsweredQuestionCount(
                                    question!!,
                                    true
                                )
                            )

                            onEvent(
                                EventWriterEvents.SaveAnswerEvent(
                                    surveyId = question?.questionEntity?.surveyId ?: 0,
                                    sectionId = question?.questionEntity?.sectionId ?: 0,
                                    didiId = didiDetails.value?.didiId ?: 0,
                                    questionId = question?.questionEntity?.questionId ?: 0,
                                    questionType = question?.questionEntity?.type ?: BLANK_STRING,
                                    questionTag = question?.questionEntity?.tag ?: 0,
                                    questionDesc = question?.questionEntity?.questionDisplay
                                        ?: BLANK_STRING,
                                    showConditionalQuestion = false,
                                    saveAnswerEventOptionItemDtoList = listOf()
                                )
                            )
                        }
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

            is QuestionTypeEvent.SaveCacheFormQuestionResponseToDbEvent -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val finalFormQuestionResponseList = mutableListOf<FormQuestionResponseEntity>()

                    val question = questionEntityStateList.toList()
                        .find { it.questionId == event.questionId }

                    finalFormQuestionResponseList.addAll(event.formQuestionResponseList)

                    finalFormQuestionResponseList.distinctBy { it.optionId }.forEach {
                        val existingFormQuestionResponseEntity =
                            questionScreenUseCase.saveFormQuestionResponseUseCase.getOptionItem(
                                it
                            )
                        if (existingFormQuestionResponseEntity > 0) {
                            questionScreenUseCase.saveFormQuestionResponseUseCase.updateFromListItemIntoDb(
                                it
                            )
                        } else {
                            questionScreenUseCase.saveFormQuestionResponseUseCase.saveFormsListIntoDB(
                                finalFormQuestionResponseList
                            )
                        }
                    }

                    event.formQuestionResponseList.find {
                        it.optionId == question?.optionItemEntityState?.find {
                            it
                                .optionItemEntity?.optionType == QuestionType.FormWithNone.name
                        }?.optionItemEntity?.optionId
                    }?.optionId?.let {
                        formWithNoneOptionMarkedSet.add(
                            it
                        )
                    }

                    val completeOptionListForQuestion =
                        questionScreenUseCase.getFormQuestionResponseUseCase
                            .getFormResponsesForQuestion(
                                surveyId = sectionDetail.value.surveyId,
                                sectionDetail.value.sectionId,
                                event.questionId,
                                didiDetails.value?.didiId ?: 0
                            )
                    onEvent(
                        EventWriterEvents.SaveAnswerEvent(
                            surveyId = finalFormQuestionResponseList.first().surveyId,
                            sectionId = finalFormQuestionResponseList.first().sectionId,
                            didiId = didiDetails.value?.didiId ?: 0,
                            questionId = finalFormQuestionResponseList.first().questionId,
                            questionType = QuestionType.Form.name,
                            questionTag = question?.questionEntity?.tag ?: -1,
                            questionDesc = question?.questionEntity?.questionDisplay
                                ?: BLANK_STRING,
                            saveAnswerEventOptionItemDtoList = completeOptionListForQuestion
                                .convertFormQuestionResponseEntityToSaveAnswerEventOptionItemDto(
                                    QuestionType.Form,
                                    question?.optionItemEntityState!!
                                )
                        )
                    )
                }
            }

            is QuestionTypeEvent.UpdateAutoCalculateTypeQuestionValue -> {
                val mQuestionEntityStateList = questionEntityStateList.distinctBy { it.questionId }
                val autoCalcQuestionList =
                    mQuestionEntityStateList.filter { it.questionEntity?.type == QuestionType.AutoCalculation.name }
                autoCalcQuestionList.forEach { autoCalcQuestion ->
                    autoCalcQuestion.optionItemEntityState.forEach { optionItemEntityState ->
                        optionItemEntityState.optionItemEntity?.conditions?.forEach { conditionsDto ->
                            conditionsDto?.let {
                                if (conditionsDto.resultType.toLowerCase()
                                        .trim() == ResultType.Calculation.name.toLowerCase().trim()
                                ) {

                                    val calculationCondition: Pair<Int, String>? =
                                        getAutoCalculationConditionConditions(conditionsDto.value)

                                    calculationCondition?.let { calcCondition ->

                                        val questionEntityState =
                                            mQuestionEntityStateList.find { it.questionId == calcCondition.first }

                                        questionEntityState?.let { q ->
                                            val options = q.optionItemEntityState.filter {
                                                it.optionItemEntity?.display?.lowercase()?.trim()
                                                    ?.contains(
                                                        calcCondition.second.lowercase().trim()
                                                    ) == true
                                            }

                                            val selectedValues: List<InputTypeQuestionAnswerEntity> =
                                                getSelectedValuesForQuestion(q.questionId, options)
                                            val calculationExpression: String =
                                                getCalculationExpressionForAutoCalculateQuestion(
                                                    selectedValues
                                                )

                                            if (calculationExpression.isNotEmpty()) {
                                                val result =
                                                    CalculatorUtils.calculate(calculationExpression)
                                                        .toString()
                                                val map = calculatedResult.value.toMutableMap()
                                                map[autoCalcQuestion.questionId!!] = result
                                                calculatedResult.value = map
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getCalculationExpressionForAutoCalculateQuestion(selectedValues: List<InputTypeQuestionAnswerEntity>): String {
        if (selectedValues.isEmpty())
            return BLANK_STRING

        return selectedValues.map { it.inputValue }.joinToString("+")
    }

    private fun getSelectedValuesForQuestion(
        questionId: Int?,
        options: List<OptionItemEntityState>
    ): List<InputTypeQuestionAnswerEntity> {
        questionId?.let { qId ->
            if (options.isEmpty())
                return emptyList()

            val questionResponse =
                inputTypeQuestionAnswerEntityList.value.filter { it.questionId == questionId }

            questionResponse.let { qResponse ->

                return qResponse.filter { opt ->
                    options.map { it.optionId }.contains(opt.optionId)
                }

            }

        } ?: return emptyList()

    }

    private suspend fun updateMissionActivityTaskStatus(didiId: Int, sectionStatus: SectionStatus) {
        val activityForSubjectDto = eventsWriterHelperImpl.getActivityFromSubjectId(didiId)
        if (activityForSubjectDto != null) {
            onEvent(
                EventWriterEvents.UpdateMissionActivityTaskStatus(
                    missionId = activityForSubjectDto.missionId,
                    activityId = activityForSubjectDto.activityId,
                    taskId = activityForSubjectDto.taskId,
                    status = sectionStatus
                )
            )
        }
        if (activityForSubjectDto != null) {
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
        var mConditionResult = conditionResult
        conditionsDto?.let { conditions ->
            conditions.resultList.forEach { questionList ->
                val parentQuestionId = questionEntityStateList.toList()
                    .find { it.questionId == questionList.questionId }?.questionEntity?.parentQuestionId
                if (parentQuestionId != 0) {
                    val parentQuestion =
                        questionEntityStateList.toList().find { it.questionId == parentQuestionId }
                    if (parentQuestion?.showQuestion == false) {
                        mConditionResult = false
                    }
                }
                var questionToShow =
                    questionEntityStateList.findQuestionEntityStateById(questionList.questionId)
                val updatedOptionItemEntityStateList = questionToShow?.optionItemEntityState
                    ?.updateOptionItemEntityListStateForQuestionByCondition(mConditionResult)
                questionToShow = questionToShow?.copy(
                    optionItemEntityState = updatedOptionItemEntityStateList!!,
                    showQuestion = mConditionResult
                )
                updateQuestionEntityStateList(questionToShow)
            }
        }
    }

    override fun updateQuestionOptionStateForNoneCondition(
        conditionResult: Boolean,
        optionId: Int,
        conditionsDto: ConditionsDto?,
        questionId: Int,
        noneOptionUnselected: Boolean
    ) {
        var questionToShow =
            questionEntityStateList.findQuestionEntityStateById(questionId)
        val updatedOptionItemEntityStateList = questionToShow?.optionItemEntityState
            ?.updateOptionsForNoneCondition(!conditionResult, optionId, noneOptionUnselected)
        questionToShow = questionToShow?.copy(
            optionItemEntityState = updatedOptionItemEntityStateList!!
        )
        updateQuestionEntityStateList(questionToShow)
    }

    fun updateOptionStateForCondition(
        conditionResult: Boolean,
        conditionsDto: ConditionsDto?,
        optionItemEntity: OptionItemEntity
    ) {
        conditionsDto?.let { conditions ->
            conditions.resultList.forEach { questionList ->
                var questionToShow =
                    questionEntityStateList.findQuestionEntityStateById(optionItemEntity.questionId)
                val optionToUpdate = mutableListOf<OptionItemEntityState>()
                questionList.options?.forEach { opt ->
                    if (questionToShow?.optionItemEntityState?.map { it.optionId }
                            ?.contains(opt?.optionId) == true) {
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
             onEvent(QuestionTypeEvent.UpdateAutoCalculateTypeQuestionValue)
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
        try {
            filterSectionList.value.questionAnswerMapping.forEach {
                answeredQuestionCount.add(it.key)
            }

            inputTypeQuestionAnswerEntityList.value.groupBy { it.questionId }.forEach {
                answeredQuestionCount.add(it.key)
            }
            val qesList = questionEntityStateList.toList()
            totalQuestionCount.intValue =
                qesList.filter { it.showQuestion && it.questionEntity?.type != QuestionType.AutoCalculation.name }
                    .distinctBy { it.questionId }.size
            // Log.d("TAG", "updateSaveUpdateState: questionEntityStateList.filter { it.showQuestion }.size: ${questionEntityStateList.filter { it.showQuestion }.size} answeredQuestionCount: $answeredQuestionCount ::: totalQuestionCount: ${totalQuestionCount.intValue}")
            isSectionCompleted.value =
                answeredQuestionCount.size == totalQuestionCount.intValue || answeredQuestionCount.size > totalQuestionCount.intValue
        } catch (ex: Exception) {
            BaselineLogger.e(
                "QuestionScreenViewModel",
                "updateSaveUpdateState: exception message -> ${ex.message}",
                ex
            )
        }
    }

    fun getOptionItemListWithConditionals(questionId: Int): List<OptionItemEntity> {
        val optionItemList = mutableListOf<OptionItemEntity>()

        optionItemEntityList = sectionDetail.value.optionsItemMap[questionId] ?: emptyList()

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
                            if (option != null) {
                                optionItemList.add(option)
                                option.conditions?.forEach { subConditionsDto ->
                                    if (subConditionsDto?.resultType?.equals(
                                            ResultType.Questions.name,
                                            true
                                        ) == true
                                    ) {
                                        subConditionsDto?.resultList?.forEach { subQuestionItem ->
                                            subQuestionItem.options?.forEach { subSubOption ->
                                                val subOptionEntity =
                                                    subSubOption?.convertToOptionItemEntity(
                                                        optionItemEntity.questionId!!,
                                                        optionItemEntity.sectionId,
                                                        optionItemEntity.surveyId,
                                                        optionItemEntity.languageId!!
                                                    )
                                                if (subOptionEntity != null) {
                                                    optionItemList.add(subOptionEntity)
                                                }
                                            }
                                        }
                                    }

                                }
                            }
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
        val question = questionEntityStateList.find { it.questionId == questionId }
        val formWithNoneOption =
            question?.optionItemEntityState?.find { it.optionItemEntity?.optionType == QuestionType.FormWithNone.name }
        val formResponseItemListForQuestion =
            formResponseEntityToQuestionMap.value[questionId]?.filter { it.optionId != formWithNoneOption?.optionId }
//        val map = formResponseItemListForQuestion?.groupBy { it.referenceId }
//        var size = map?.keys?.size ?: 0
//        map?.forEach {
//            if (if)
//        }
        return formResponseItemListForQuestion?.groupBy { it.referenceId }?.keys?.size ?: 0
    }

    fun isFormQuestionMarkedWithNone(questionId: Int, optionId: Int): Boolean {
        val formResponseItemListForQuestion = formResponseEntityToQuestionMap.value[questionId]
        formResponseItemListForQuestion?.groupBy { it.referenceId }?.forEach {
            val isFormQuestionMarkedWithNone = it.value.find { it.optionId == optionId } != null
            isNoneMarkedForFormQuestion.value[questionId] = isFormQuestionMarkedWithNone
            return isFormQuestionMarkedWithNone
        }
        return false
    }

    fun getTotalIncomeForLivelihoodQuestion(context: Context, questionId: Int): Float {

        val netIncomeValue = ArrayList<String>()

        val optionsWithNetIncome = getOptionItemListWithConditionals(questionId).filter {
            it.display?.contains(
                context.getString(R.string.net_income_comparision),
                true
            )!!
        }
        optionsWithNetIncome.forEach { option ->
            formResponseEntityToQuestionMap.value[questionId]?.forEach {
                if (it.optionId == option.optionId) {
                    netIncomeValue.add(it.selectedValue)
                }
            }
        }

        val expression = netIncomeValue.joinToString("+")
        return if (expression != BLANK_STRING)
            CalculatorUtils.calculate(expression)
        else
            DEFAULT_CALCULATION_RESULT

    }

    fun getReferenceIdForFormWithNoneQuestion(): String {
        if (referenceIdForFormWithNone == BLANK_STRING)
            referenceIdForFormWithNone = UUID.randomUUID().toString()

        return referenceIdForFormWithNone
    }

    fun setReferenceIdForFormWithNoneQuestion(referenceId: String) {
        if (referenceIdForFormWithNone == BLANK_STRING)
            referenceIdForFormWithNone = referenceId
    }

    fun getUserId(): String = questionScreenUseCase.getSectionUseCase.getUniqueUserIdentifier()

    @RequiresApi(Build.VERSION_CODES.N)
    fun saveInputNumberOptionResponse(questionId: Int, optionId: Int, selectedValue: String) {

        val responseList = mutableListOf<InputTypeQuestionAnswerEntity>()

        val question = questionEntityStateList.toList().find { it.questionId == questionId }

        val mOption = InputTypeQuestionAnswerEntity(
            userId = questionScreenUseCase.getSectionUseCase.getUniqueUserIdentifier(),
            didiId = didiDetails.value?.didiId ?: 0,
            surveyId = question?.questionEntity?.surveyId ?: 0,
            sectionId = question?.questionEntity?.sectionId ?: 0,
            questionId = questionId,
            optionId = optionId,
            inputValue = selectedValue
        )

        val oldValues = inputNumberQuestionMap[questionId]

        oldValues?.let {
            responseList.clear()
            responseList.addAll(it)
        }

        if (selectedValue != "0" && selectedValue != BLANK_STRING)
            responseList.add(mOption)
        else {
            if (oldValues?.map { it.optionId }?.contains(mOption.optionId) == true)
                responseList.removeIf { it.optionId == mOption.optionId }
        }

        inputNumberQuestionMap[questionId] = responseList

    }

}
