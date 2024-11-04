package com.sarathi.surveymanager.ui.screen

import android.text.TextUtils
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.nudge.core.DEFAULT_ID
import com.nudge.core.model.response.SurveyValidations
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toSafeInt
import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.DISBURSED_AMOUNT_TAG
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
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
    private val validationUseCase: SurveyValidationUseCase
) : BaseViewModel() {
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
    val isActivityNotCompleted = mutableStateOf<Boolean>(false)
    private val _questionUiModel = mutableStateOf<List<QuestionUiModel>>(emptyList())
    val questionUiModel: State<List<QuestionUiModel>> get() = _questionUiModel

    var activityConfig: ActivityConfigEntity? = null

    var isNoSection = mutableStateOf(false)

    var surveyConfig = mapOf<Int, MutableMap<String, SurveyCardModel>>()

    val conditionsUtils = ConditionsUtils()

    val visibilityMap: SnapshotStateMap<Int, Boolean> get() = conditionsUtils.questionVisibilityMap

    val showSummaryView = mutableMapOf<Int, Int>()

    var validations: List<SurveyValidations>? = mutableListOf()
    var fieldValidationAndMessageMap = mutableStateMapOf<Int, Pair<Boolean, String>>()

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitDataState -> {
                intiQuestions()
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

    private fun intiQuestions() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            taskEntity = getTaskUseCase.getTask(taskId)
            if (_questionUiModel.value.isEmpty()) {
                _questionUiModel.value = fetchDataUseCase.invoke(
                    surveyId = surveyId,
                    sectionId = sectionId,
                    subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                    activityConfigId = activityConfigId,
                    referenceId = referenceId,
                    grantId = grantID
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
                questionUiModel.value.forEach {
                    runConditionCheck(it)
                }
            }

            isTaskStatusCompleted()
            questionUiModel.value.filter { visibilityMap[it.questionId].value() }.apply {
                this.forEach {
                    runValidationCheck(it.questionId) { isValid, message ->
                        fieldValidationAndMessageMap[it.questionId] =
                            Pair(isValid, message)

                    }
                }

            }

            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private fun getSurveyConfig(
        surveyConfigMap: Map<Int, List<SurveyConfigEntity>>,
        taskAttributes: List<SubjectAttributes>
    ): MutableMap<Int, MutableMap<String, SurveyCardModel>> {
        val mSurveyConfig = mutableMapOf<Int, MutableMap<String, SurveyCardModel>>()
        surveyConfigMap.forEach { surveyConfigMapEntry ->
            val surveyConfigForForm = mutableMapOf<String, SurveyCardModel>()
            surveyConfigMapEntry.value.forEach { it ->
                var surveyConfigEntity = it
                if (surveyConfigEntity.type.equals(UiConfigAttributeType.DYNAMIC.name, true)) {
                    surveyConfigEntity =
                        surveyConfigEntity.copy(value = taskAttributes.find { it.key == surveyConfigEntity.value }?.value.value())
                }
                val model = SurveyCardModel.getSurveyCarModel(surveyConfigEntity)
                surveyConfigForForm[surveyConfigEntity.key] = model
            }
            mSurveyConfig[surveyConfigMapEntry.key] = surveyConfigForForm
        }

        return mSurveyConfig

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

    fun runValidationCheck(questionId: Int, onValidationComplete: (Boolean, String) -> Unit) {

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

            isButtonEnable.value =
                isQuestionValidationFromConfig && checkButtonValidation() || (showSummaryView.isNotEmpty() && showSummaryView.all { it.value != 0 })
        }

    }

    fun checkButtonValidation(): Boolean {


        questionUiModel.value.filter { it.isMandatory && visibilityMap.get(it.questionId) == true }
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
        isActivityNotCompleted.value = !getActivityUseCase.isAllActivityCompleted(
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
            val surveyEntity = getSectionListUseCase.getSurveyEntity(surveyId)
            surveyEntity?.let { survey ->
                if (isTaskCompleted) {
                    taskEntity = taskEntity?.copy(status = SurveyStatusEnum.COMPLETED.name)
                    taskStatusUseCase.markTaskCompleted(taskId)
                } else {
                    taskEntity = taskEntity?.copy(status = SurveyStatusEnum.INPROGRESS.name)
                    taskStatusUseCase.markTaskInProgress(taskId)
                }
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

    fun runConditionCheck(sourceQuestion: QuestionUiModel) {
        conditionsUtils.runConditionCheck(sourceQuestion)
        ioViewModelScope {
            val notVisibleQuestion = visibilityMap.filter { !it.value }
            questionUiModel.value.filter { notVisibleQuestion.containsKey(it.questionId) }
                .forEach { it ->
                    it.options = it.options?.map {
                        it.copy(
                            isSelected = false,
                            selectedValue = BLANK_STRING
                        )
                    }
                    saveQuestionAnswerIntoDb(it)
                }
        }
    }

    fun isFormEntryAllowed(formId: Int): Boolean {
        var isFormEntryAllowed = true
        val formConfig = surveyConfig[formId]
        if (formConfig?.containsKey(SurveyConfigCardSlots.FORM_MAX_RESPONSE_COUNT.name) == true) {
            if (formConfig[SurveyConfigCardSlots.FORM_MAX_RESPONSE_COUNT.name]?.value?.toSafeInt() == showSummaryView[formId]) {
                isFormEntryAllowed = false
            }
        }

        return isActivityNotCompleted.value && isFormEntryAllowed
    }

}