package com.sarathi.surveymanager.ui.screen

import android.text.TextUtils
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.nudge.core.DEFAULT_FORM_ID
import com.nudge.core.DEFAULT_ID
import com.nudge.core.model.response.SurveyValidations
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toSafeInt
import com.nudge.core.usecase.FetchAppConfigFromCacheOrDbUsecase
import com.nudge.core.value
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.DISBURSED_AMOUNT_TAG
import com.sarathi.dataloadingmangement.NUMBER_ZERO
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyConfigEntity
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.FormEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetConditionQuestionMappingsUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSectionListUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSurveyConfigFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSurveyValidationsFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyValidationUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SubjectAttributes
import com.sarathi.dataloadingmangement.model.uiModel.SurveyCardModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyConfigCardSlots
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigAttributeType
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.surveymanager.utils.conditions.ConditionsUtils
import com.sarathi.surveymanager.utils.events.EventWriterEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class BaseSurveyScreenViewModel @Inject constructor(
    private val fetchDataUseCase: FetchSurveyDataFromDB,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val saveSurveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val surveyAnswerEventWriterUseCase: SurveyAnswerEventWriterUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val fromEUseCase: FormUseCase,
    private val formEventWriterUseCase: FormEventWriterUseCase,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
    private val getSectionListUseCase: GetSectionListUseCase,
    private val getConditionQuestionMappingsUseCase: GetConditionQuestionMappingsUseCase,
    private val getSurveyConfigFromDbUseCase: GetSurveyConfigFromDbUseCase,
    private val getSurveyValidationsFromDbUseCase: GetSurveyValidationsFromDbUseCase,
    private val validationUseCase: SurveyValidationUseCase,
    private val fetchContentUseCase: FetchContentUseCase,
    private val fetchAppConfigFromCacheOrDbUsecase: FetchAppConfigFromCacheOrDbUsecase
) : BaseViewModel() {

    val LOGGER_TAG = BaseSurveyScreenViewModel::class.java.simpleName

    var surveyId: Int = 0
    var sectionId: Int = 0
    var taskId: Int = 0
    var activityConfigId: Int = 0
    var grantID: Int = 0
    var sanctionAmount: Int = 0
    var totalSubmittedAmount: Int = 0
    var totalRemainingAmount: Int = 0
    var granType: String = BLANK_STRING
    var subjectType: String = BLANK_STRING
    var referenceId: String = BLANK_STRING
    var taskEntity: ActivityTaskEntity? = null

    val isButtonEnable = mutableStateOf<Boolean>(false)
    val isActivityCompleted = mutableStateOf<Boolean>(false)
    private val _questionUiModel = mutableStateOf<List<QuestionUiModel>>(emptyList())
    val questionUiModel: State<List<QuestionUiModel>> get() = _questionUiModel

    var activityConfig: ActivityConfigEntity? = null

    var isNoSection = mutableStateOf(false)

    var surveyConfig = mapOf<Int, MutableMap<String, List<SurveyCardModel>>>()

    val conditionsUtils = ConditionsUtils()

    val visibilityMap: SnapshotStateMap<Int, Boolean> get() = conditionsUtils.questionVisibilityMap

    val showSummaryView = mutableMapOf<Int, Int>()

    var validations: List<SurveyValidations>? = mutableListOf()
    var fieldValidationAndMessageMap = mutableStateMapOf<Int, Pair<Boolean, String>>()

    private var formResponseMap = mapOf<Int, List<SurveyAnswerEntity>>()

    var autoCalculateQuestionResultMap: SnapshotStateMap<Int, String> =
        mutableStateMapOf<Int, String>()

    val optionStateMap: SnapshotStateMap<Pair<Int, Int>, Boolean> get() = conditionsUtils.optionStateMap

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                CoroutineScope(ioDispatcher + exceptionHandler).launch {
                    intiQuestions()
                }
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(
                    isLoaderVisible = event.showLoader
                )
            }


            is EventWriterEvents.SaveAnswerEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                }
            }

        }
    }

    open suspend fun intiQuestions() {
            taskEntity = getTaskUseCase.getTask(taskId)
            if (_questionUiModel.value.isEmpty()) {
                _questionUiModel.value = fetchDataUseCase.invoke(
                    surveyId = surveyId,
                    sectionId = sectionId,
                    subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                    activityConfigId = activityConfigId,
                    referenceId = referenceId,
                    grantId = grantID,
                    missionId = taskEntity?.missionId.value(DEFAULT_ID),
                    activityId = taskEntity?.activityId.value(DEFAULT_ID)
                )
            }

            taskEntity?.let { task ->
                activityConfig =
                    getActivityUiConfigUseCase.getActivityConfig(task.activityId, task.missionId)
            }

            val sectionList = getSectionListUseCase.invoke(surveyId = surveyId)

            isNoSection.value = sectionList.size == 1

            questionUiModel.value
                .filter { it.formId != 0 }
                .groupBy { it.formId }
                .also { it ->
                    val formQuestionMap = mutableMapOf<Int, List<Int>>()
                    it.forEach { mapEntry ->
                        val questionIds = mapEntry.value.map { it.questionId }
                        formQuestionMap.put(mapEntry.key, questionIds)
                    }
                    val totalSavedFormResponseCount =
                        saveSurveyAnswerUseCase.getTotalSavedFormResponsesCount(
                            surveyId = surveyId,
                            sectionId = sectionId,
                            taskId = taskId,
                            formQuestionMap = formQuestionMap
                        )
                    totalSavedFormResponseCount.forEach { mapEntry ->
                        showSummaryView[mapEntry.key] = mapEntry.value
                    }

                    formResponseMap = saveSurveyAnswerUseCase.getFormResponseMap(
                        surveyId = surveyId,
                        sectionId = sectionId,
                        taskId = taskId,
                        formQuestionMap = formQuestionMap
                    )
                }


            activityConfig?.let {
                getSurveyConfigFromDbUseCase.invoke(
                    missionId = it.missionId,
                    it.activityId,
                    surveyId
                )?.also { surveyConfigMap ->
                    val taskAttributes = getTaskUseCase.getSubjectAttributes(taskId)
                    surveyConfig = getSurveyConfig(surveyConfigMap, taskAttributes)
                }
                validations = getSurveyValidationsFromDbUseCase.invoke(surveyId, sectionId)
            }

            val sourceTargetQuestionMapping = getConditionQuestionMappingsUseCase
                .invoke(
                    surveyId = surveyId,
                    sectionId = sectionId,
                    questionIdList = questionUiModel.value.map { it.questionId }
                )

            conditionsUtils.apply {
                init(questionUiModel.value, sourceTargetQuestionMapping)
                initQuestionVisibilityMap(questionUiModel.value)
                initOptionsStateMap(questionUiModel.value)
                questionUiModel.value.forEach {
                    runConditionCheck(it)
                }
                updateAutoCalculateQuestionValue(
                    questionUiModel.value,
                    surveyConfig[DEFAULT_FORM_ID],
                    autoCalculateQuestionResultMap
                )
            }

            isTaskStatusCompleted()


            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }

    }

    private fun getSurveyConfig(
        surveyConfigMap: Map<Int, List<SurveyConfigEntity>>,
        taskAttributes: List<SubjectAttributes>
    ): MutableMap<Int, MutableMap<String, List<SurveyCardModel>>> {
        return surveyConfigMap.mapValues { (_, configEntities) ->
            configEntities
                .groupBy { it.key }
                .mapValues { (_, entities) ->
                    entities.map { entity ->
                        val updatedEntity = if (entity.type.equals(
                                UiConfigAttributeType.DYNAMIC.name,
                                ignoreCase = true
                            )
                        ) {
                            entity.copy(value = taskAttributes.find { it.key == entity.value }?.value.value())
                        } else {
                            entity
                        }
                        SurveyCardModel.getSurveyCarModel(updatedEntity)
                    }
                }.toMutableMap()
        }.toMutableMap()
    }


    protected suspend fun saveQuestionAnswerIntoDb(question: QuestionUiModel) {
        saveSurveyAnswerUseCase.saveSurveyAnswer(
            question,
            taskEntity?.subjectId ?: DEFAULT_ID,
            taskId = taskId,
            referenceId = referenceId,
            grantId = grantID,
            grantType = granType
        )
    }

    open fun runValidationCheck(questionId: Int, onValidationComplete: (Boolean, String) -> Unit) {

        validationUseCase.validateExpressionEvaluator(
            validations = validations,
            questionUiModel = questionUiModel.value.find { it.questionId == questionId }
        ) { isValid, message ->
            var isQuestionValidationFromConfig = true

            onValidationComplete(isValid, message)
            fieldValidationAndMessageMap.forEach {
                if (!it.value.first) {
                    isQuestionValidationFromConfig = false
                }
            }

            isButtonEnable.value = isButtonEnabled(isQuestionValidationFromConfig)
        }

    }

    fun isButtonEnabled(isQuestionValidationFromConfig: Boolean): Boolean {
        // Start with the base result based on question validation
        var result = isQuestionValidationFromConfig && checkButtonValidation()

        // Proceed only if there is at least one non-zero formId in questionUiModel
        if (questionUiModel.value.any { it.formId != NUMBER_ZERO }) {

            // Get the list of valid formIds from showSummaryView
            val formIdsInSummary = showSummaryView.keys.toList()

            // If there is only one item in showSummaryView, handle the specific logic
            if (showSummaryView.size == 1) {
                val firstFormQuestion = questionUiModel.value.firstOrNull {
                    formIdsInSummary.contains(it.formId)
                }

                // If there's a corresponding question and its visibility is true, check the values
                firstFormQuestion?.let {
                    if (visibilityMap[it.questionId] == true) {
                        result = result && showSummaryView.all { it.value != 0 }
                    }
                }

            } else {
                // For cases where showSummaryView has multiple items
                result = showSummaryView.isNotEmpty() && showSummaryView.all { it.value != 0 }
            }
        }

        return result
    }

    open fun checkButtonValidation(): Boolean {

        questionUiModel.value.filterForValidations(visibilityMap)
            .forEach { questionUiModel ->
                if (questionUiModel.tagId.contains(DISBURSED_AMOUNT_TAG)) {
                    val disbursedAmount =
                        if (TextUtils.isEmpty(questionUiModel.options?.firstOrNull()?.selectedValue)) 0 else questionUiModel.options?.firstOrNull()?.selectedValue?.toInt()
                    if (sanctionAmount != 0 && (disbursedAmount
                            ?: 0) + totalRemainingAmount > sanctionAmount
                    ) {
                        return false
                    }
                }
                val result =
                    (questionUiModel.options?.filter { it.isSelected == true }?.size ?: 0) > 0
                if (!result) {
                    return false
                }

            }
        return true

    }


    fun setPreviousScreenData(
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        subjectType: String,
        referenceId: String,
        activityConfigId: Int,
        grantId: Int,
        grantType: String,
        sanctionedAmount: Int,
        totalSubmittedAmount: Int,
    ) {
        this.surveyId = surveyId
        this.sectionId = sectionId
        this.taskId = taskId
        this.subjectType = subjectType
        this.referenceId = referenceId
        this.activityConfigId = activityConfigId
        this.grantID = grantId
        this.granType = grantType
        this.sanctionAmount = sanctionedAmount
        this.totalSubmittedAmount = totalSubmittedAmount
    }

    private suspend fun isTaskStatusCompleted() {
        isActivityCompleted.value = getActivityUseCase.isAllActivityCompleted(
            missionId = taskEntity?.missionId ?: 0,
            activityId = taskEntity?.activityId ?: 0
        )

        checkButtonValidation()

    }


    fun getPrefixFileName(question: QuestionUiModel): String {
        return "${coreSharedPrefs.getMobileNo()}_Question_Answer_Image_${question.questionId}_${question.surveyId}_"
    }

    open fun saveSingleAnswerIntoDb(question: QuestionUiModel) {

    }

    open fun updateTaskStatus(taskId: Int, isTaskCompleted: Boolean = false) {
        ioViewModelScope {
            val oldTaskStatus = getTaskUseCase.getTask(taskId).status ?: BLANK_STRING
            val newTaskStatus =
                if (isTaskCompleted) SurveyStatusEnum.COMPLETED.name else SurveyStatusEnum.INPROGRESS.name

            val surveyEntity = getSectionListUseCase.getSurveyEntity(surveyId)
            surveyEntity?.let { survey ->
                if (isTaskCompleted) {
                    taskEntity = taskEntity?.copy(status = SurveyStatusEnum.COMPLETED.name)
                    taskStatusUseCase.markTaskCompleted(taskId)
                } else {
                    taskEntity = taskEntity?.copy(status = SurveyStatusEnum.INPROGRESS.name)
                    taskStatusUseCase.markTaskInProgress(taskId)
                }
                if (oldTaskStatus != newTaskStatus)
                taskEntity?.let { task ->
                    matStatusEventWriterUseCase.updateTaskStatus(
                        task,
                        survey.surveyName,
                        subjectType
                    )
                }
            }
        }
    }

    fun updateQuestionResponseMap(question: QuestionUiModel) {
        conditionsUtils.updateQuestionResponseMap(question)
    }

    fun runNoneOptionCheck(sourceQuestion: QuestionUiModel): Boolean {
        return conditionsUtils.runNoneOptionCheck(sourceQuestion)
    }

    fun runConditionCheck(sourceQuestion: QuestionUiModel) {
        conditionsUtils.runConditionCheck(sourceQuestion)
        conditionsUtils.updateAutoCalculateQuestionValue(
            questionUiModel.value,
            surveyConfig[DEFAULT_FORM_ID],
            autoCalculateQuestionResultMap
        )
        ioViewModelScope {
            updateNonVisibleQuestionsResponse()
        }
    }

    private suspend fun updateNonVisibleQuestionsResponse() {
        val notVisibleQuestion = visibilityMap.filter { !it.value }
        questionUiModel.value.filter { notVisibleQuestion.containsKey(it.questionId) }
            .forEach { it ->
                it.options = it.options?.map {
                    it.copy(
                        isSelected = false,
                        selectedValue = BLANK_STRING
                    )
                }
                if (saveSurveyAnswerUseCase.isAnswerAvailableInDb(
                        it,
                        taskEntity?.subjectId ?: DEFAULT_ID,
                        taskId = taskId,
                        referenceId = referenceId,
                        grantId = grantID,
                        grantType = granType
                    ) && it.formId == NUMBER_ZERO
                ) {
                    saveQuestionAnswerIntoDb(it)
                }
            }
    }

    fun isFormEntryAllowed(formId: Int): Boolean {
        var isFormEntryAllowed = true
        val formConfig = surveyConfig[formId]
        if (formConfig?.containsKey(SurveyConfigCardSlots.FORM_MAX_RESPONSE_COUNT.name) == true) {
            if (formConfig[SurveyConfigCardSlots.FORM_MAX_RESPONSE_COUNT.name]?.firstOrNull()?.value.toSafeInt() == showSummaryView[formId]) {
                isFormEntryAllowed = false
            }
        }

        return !isActivityCompleted.value && isFormEntryAllowed
    }

    fun getSurveyModelWithValue(
        entry: Map.Entry<String, SurveyCardModel>,
        question: QuestionUiModel,
        surveyConfigForForm: MutableMap<String, List<SurveyCardModel>>
    ): SurveyCardModel {
        var updatedModel = entry.value
        val formResponses = formResponseMap[question.formId]

        when (entry.key.uppercase()) {
            SurveyConfigCardSlots.FORM_QUESTION_CARD_TOTAL_COUNT.name -> {
                val updatedTotalCountText =
                    entry.value.value + "${showSummaryView[question.formId].value()}"
                updatedModel = entry.value.copy(value = updatedTotalCountText)
            }

            SurveyConfigCardSlots.FORM_QUESTION_CARD_SUBTITLE_LABLE.name -> {
                val sum =
                    surveyConfigForForm[SurveyConfigCardSlots.FORM_QUESTION_CARD_SUBTITLE_VALUE.name]?.sumOf { surveyCardModel ->
                        val quest =
                            questionUiModel.value.find { it.tagId.contains(surveyCardModel.tagId) }
                        formResponses?.filter { it.questionId == quest?.questionId.value() && it.formId == quest?.formId.value() }
                            ?.flatMap { it.optionItems }
                            ?.filter { it.isSelected == true }
                            ?.sumOf { it.selectedValue.toSafeInt() } ?: 0
                    } ?: 0
                updatedModel = entry.value.copy(value = sum.toString())
            }
        }

        return updatedModel

    }

    fun isFilePathExists(filePath: String): Boolean {
        return fetchContentUseCase.isFilePathExists(filePath)
    }

}

fun List<QuestionUiModel>.filterForValidations(visibilityMap: Map<Int, Boolean>): List<QuestionUiModel> {
    return this.filter { it.isMandatory && it.formId == NUMBER_ZERO && visibilityMap.get(it.questionId) == true }
}