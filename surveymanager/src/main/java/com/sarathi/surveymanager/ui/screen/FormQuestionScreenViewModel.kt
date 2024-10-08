package com.sarathi.surveymanager.ui.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ID
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.GetConditionQuestionMappingsUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.surveymanager.utils.conditions.ConditionsUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class FormQuestionScreenViewModel @Inject constructor(
    private val fetchDataUseCase: FetchSurveyDataFromDB,
    private val getTaskUseCase: GetTaskUseCase,
    private val getConditionQuestionMappingsUseCase: GetConditionQuestionMappingsUseCase,
    private val surveyAnswerEventWriterUseCase: SurveyAnswerEventWriterUseCase,
    private val saveSurveyAnswerUseCase: SaveSurveyAnswerUseCase,
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
    var referenceId: String = BLANK_STRING

    var taskEntity: ActivityTaskEntity? = null

    var isActivityNotCompleted = mutableStateOf(true)

    val isButtonEnable = mutableStateOf<Boolean>(false)

//    private val _questionUiModel = mutableStateListOf<QuestionUiModel>()
//    val questionUiModel: SnapshotStateList<QuestionUiModel> get() = _questionUiModel

    private val _questionUiModel = mutableStateOf<List<QuestionUiModel>>(emptyList())
    val questionUiModel: State<List<QuestionUiModel>> get() = _questionUiModel

    val conditionsUtils = ConditionsUtils()

    val visibilityMap: SnapshotStateMap<Int, Boolean> get() = conditionsUtils.questionVisibilityMap

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
            _questionUiModel.value = fetchDataUseCase.invokeFormQuestions(
                surveyId = surveyId,
                sectionId = sectionId,
                subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                activityConfigId = activityConfigId,
                referenceId = referenceId,
                grantId = 0,
                formId = formId
            )

            val sourceTargetQuestionMapping = getConditionQuestionMappingsUseCase
                .invoke(
                    surveyId = surveyId,
                    sectionId = sectionId,
                    questionIdList = questionUiModel.value.map { it.questionId }
                )

            conditionsUtils.apply {
                init(questionUiModel.value, sourceTargetQuestionMapping)
                initQuestionVisibilityMap(questionUiModel.value)
            }
        }
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
        this.referenceId = referenceId
        this.subjectType = subjectType
    }

    fun saveSingleAnswerIntoDb(currentQuestionUiModel: QuestionUiModel) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
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
                activityReferenceType = BLANK_STRING
            )
        }
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

    fun checkButtonValidation() {
        questionUiModel.value.filter { it.isMandatory }.forEach { questionUiModel ->
            val result = (questionUiModel.options?.filter { it.isSelected == true }?.size ?: 0) > 0
            if (!result) {
                isButtonEnable.value = false
                return
            }

        }
        isButtonEnable.value = true
    }

    fun updateQuestionResponseMap(question: QuestionUiModel) {
        conditionsUtils.updateQuestionResponseMap(question)
    }

    fun runConditionCheck(sourceQuestion: QuestionUiModel) {
        conditionsUtils.runConditionCheck(sourceQuestion)
    }

}