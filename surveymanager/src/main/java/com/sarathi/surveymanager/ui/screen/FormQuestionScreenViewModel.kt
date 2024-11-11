package com.sarathi.surveymanager.ui.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ID
import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.nudge.core.casteMap
import com.nudge.core.model.response.SurveyValidations
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toSafeInt
import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyConfigEntity
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.GetConditionQuestionMappingsUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSurveyConfigFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSurveyValidationsFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyValidationUseCase
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SubjectAttributes
import com.sarathi.dataloadingmangement.model.uiModel.SurveyCardModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyConfigCardSlots
import com.sarathi.dataloadingmangement.model.uiModel.UiConfigAttributeType
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.surveymanager.utils.conditions.ConditionsUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
open class FormQuestionScreenViewModel @Inject constructor(
    private val fetchDataUseCase: FetchSurveyDataFromDB,
    private val getTaskUseCase: GetTaskUseCase,
    private val getConditionQuestionMappingsUseCase: GetConditionQuestionMappingsUseCase,
    private val surveyAnswerEventWriterUseCase: SurveyAnswerEventWriterUseCase,
    private val saveSurveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val getSurveyConfigFromDbUseCase: GetSurveyConfigFromDbUseCase,
    private val getSurveyValidationsFromDbUseCase: GetSurveyValidationsFromDbUseCase,
    private val validationUseCase: SurveyValidationUseCase,
    private val coreSharedPrefs: CoreSharedPrefs
) : BaseViewModel() {

    private val LOGGING_TAG = FormQuestionScreenViewModel::class.java.simpleName

    var taskId: Int = 0
    var sectionId: Int = 0
    var surveyId: Int = 0
    var formId: Int = 0
    var activityId: Int = 0
    var activityConfigId: Int = 0
    var subjectType: String = BLANK_STRING
    var missionId: Int = 0
    var referenceId: String = UUID.randomUUID().toString()

    var taskEntity: ActivityTaskEntity? = null

    var isActivityNotCompleted = mutableStateOf(true)

    val isButtonEnable = mutableStateOf<Boolean>(false)

    private val _questionUiModel = mutableStateOf<List<QuestionUiModel>>(emptyList())
    val questionUiModel: State<List<QuestionUiModel>> get() = _questionUiModel

    val conditionsUtils = ConditionsUtils()

    var surveyConfig = mutableMapOf<String, SurveyCardModel>()

    val visibilityMap: SnapshotStateMap<Int, Boolean> get() = conditionsUtils.questionVisibilityMap

    var validations: List<SurveyValidations>? = mutableListOf()
    var fieldValidationAndMessageMap = mutableStateMapOf<Int, Pair<Boolean, String>>()

    val formTitle = mutableStateOf(BLANK_STRING)

    override fun <T> onEvent(event: T) {
        when (event) {
            is InitDataEvent.InitFormQuestionScreenState -> {
                loadFormQuestionData()
            }
        }
    }

    private fun loadFormQuestionData() {
        ioViewModelScope {
            taskEntity = getTaskUseCase.getTask(taskId)
            val question = fetchDataUseCase.invoke(
                surveyId = surveyId,
                sectionId = sectionId,
                subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                activityConfigId = activityConfigId,
                referenceId = referenceId,
                grantId = 0
            )
            _questionUiModel.value = question.filter { it.formId == formId }

            val sourceTargetQuestionMapping = getConditionQuestionMappingsUseCase
                .invoke(
                    surveyId = surveyId,
                    sectionId = sectionId,
                    questionIdList = question.map { it.questionId }
                )

            taskEntity?.let {
                getSurveyConfigFromDbUseCase.invoke(it.missionId, it.activityId, surveyId, formId)
                    .also { surveyConfigEntityList ->
                        val taskAttributes = getTaskUseCase.getSubjectAttributes(it.taskId)
                        getSurveyConfig(surveyConfigEntityList, taskAttributes)
                    }
                validations = getSurveyValidationsFromDbUseCase.invoke(surveyId, sectionId)
                questionUiModel.value.forEach {
                    fieldValidationAndMessageMap[it.questionId] = Pair(true, BLANK_STRING)
                }
            }


            conditionsUtils.apply {
                init(questionUiModel.value, sourceTargetQuestionMapping)
                initQuestionVisibilityMap(questionUiModel.value)
                questionUiModel.value.forEach {
                    runConditionCheck(it)
                    runValidationCheck(questionId = it.questionId) { isValid, message ->
                        fieldValidationAndMessageMap[it.questionId] =
                            Pair(isValid, message)
                    }
                }
                val nonFormParentQuestion = sourceTargetQuestionMapping.filter {
                    !questionUiModel.value.map { it.questionId }.contains(it.sourceQuestionId)
                }
                nonFormParentQuestion.forEach {
                    conditionsUtils.questionVisibilityMap[it.targetQuestionId] = true

                }
            }
        }
    }

    private fun getSurveyConfig(
        surveyConfigEntityList: List<SurveyConfigEntity>,
        taskAttributes: List<SubjectAttributes>
    ) {
        val mSurveyConfig = mutableMapOf<String, SurveyCardModel>()
        surveyConfigEntityList.forEach { it ->
            var surveyConfigEntity = it
            if (surveyConfigEntity.type.equals(UiConfigAttributeType.DYNAMIC.name, true)) {
                // TEMP Code remove after moving caste table to code.
                if (surveyConfigEntity.value.equals("casteId", true)) {
                    val casteId =
                        taskAttributes.find { it.key == surveyConfigEntity.value }?.value.value()
                            .toSafeInt()
                    surveyConfigEntity = surveyConfigEntity.copy(
                        value = casteMap.get(coreSharedPrefs.getAppLanguage())?.get(casteId)
                            ?: casteMap.get(DEFAULT_LANGUAGE_CODE)?.get(casteId).value()
                    )
                } else {
                    surveyConfigEntity =
                        surveyConfigEntity.copy(value = taskAttributes.find { it.key == surveyConfigEntity.value }?.value.value())
                }

            }
            mSurveyConfig.put(
                surveyConfigEntity.key,
                SurveyCardModel.getSurveyCarModel(surveyConfigEntity)
            )
        }
        surveyConfig = mSurveyConfig
        formTitle.value =
            surveyConfig[SurveyConfigCardSlots.FORM_QUESTION_CARD_TITLE.name]?.value.value()
    }

    fun setPreviousScreenData(
        taskId: Int,
        sectionId: Int,
        surveyId: Int,
        formId: Int,
        activityId: Int,
        activityConfigId: Int,
        missionId: Int,
        referenceId: String,
        subjectType: String
    ) {
        this.taskId = taskId
        this.sectionId = sectionId
        this.surveyId = surveyId
        this.formId = formId
        this.activityId = activityId
        this.activityConfigId = activityConfigId
        this.missionId = missionId
        if (referenceId != BLANK_STRING) {
            this.referenceId = referenceId
        }
        this.subjectType = subjectType
    }

    private suspend fun saveSingleAnswerIntoDb(currentQuestionUiModel: QuestionUiModel) {
        saveQuestionAnswerIntoDb(currentQuestionUiModel)

        surveyAnswerEventWriterUseCase.saveSurveyAnswerEvent(
            questionUiModel = currentQuestionUiModel,
            subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
            subjectType = subjectType,
            taskLocalId = taskEntity?.localTaskId
                ?: com.sarathi.dataloadingmangement.BLANK_STRING,
            referenceId = referenceId,
            grantId = 0,
            grantType = BLANK_STRING,
            taskId = taskId,
            uriList = ArrayList(),
            activityId = activityId,
            activityReferenceId = 0,
            activityReferenceType = BLANK_STRING,
            isFromRegenerate = false
        )
    }


    protected suspend fun saveQuestionAnswerIntoDb(question: QuestionUiModel) {
        saveSurveyAnswerUseCase.saveSurveyAnswer(
            questionUiModel = question,
            subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
            taskId = taskId,
            referenceId = referenceId,
            grantId = 0,
            grantType = BLANK_STRING
        )
    }

    fun checkButtonValidation(): Boolean {
        val filterQuestionUiModelList =
            questionUiModel.value.filter { it.isMandatory && visibilityMap.get(it.questionId) == true }

        for (questionUiModel in filterQuestionUiModelList) {
            val result = (questionUiModel.options?.filter { it.isSelected == true }?.size ?: 0) > 0
            if (!result) {
                return false
            }
        }
        return true
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

            isButtonEnable.value = isQuestionValidationFromConfig && checkButtonValidation()
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
                }
        }
    }

    fun saveAllAnswers() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            questionUiModel.value.filter { it.isMandatory && visibilityMap.get(it.questionId) == true }
                .forEach { questionUiModel ->
                    saveSingleAnswerIntoDb(questionUiModel)
                }
            surveyAnswerEventWriterUseCase.writeFormResponseEvent(
                questionUiModels = questionUiModel.value.filter {
                    it.isMandatory && visibilityMap.get(
                        it.questionId
                    ) == true
                },
                subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                subjectType = subjectType,
                taskLocalId = taskEntity?.localTaskId
                    ?: com.sarathi.dataloadingmangement.BLANK_STRING,
                referenceId = referenceId,
                grantId = 0,
                grantType = BLANK_STRING,
                taskId = taskId,
                isFromRegenerate = false
            )
        }
    }

    fun getPrefixFileName(question: QuestionUiModel): String {
        return "${coreSharedPrefs.getMobileNo()}_Question_Answer_Image_${question.questionId}_${question.surveyId}_"
    }

}