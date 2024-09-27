package com.sarathi.surveymanager.ui.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ID
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.GetConditionQuestionMappingsUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.surveymanager.utils.conditions.ConditionsUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FormQuestionScreenViewModel @Inject constructor(
    private val fetchDataUseCase: FetchSurveyDataFromDB,
    private val getTaskUseCase: GetTaskUseCase,
    private val getConditionQuestionMappingsUseCase: GetConditionQuestionMappingsUseCase
) : BaseViewModel() {

    var taskId: Int = 0
    var sectionId: Int = 0
    var surveyId: Int = 0
    var formId: Int = 0
    var activityId: Int = 0
    var activityConfigId: Int = 0
    var missionId: Int = 0
    var referenceId: String = BLANK_STRING

    var taskEntity: ActivityTaskEntity? = null

    var isActivityNotCompleted = mutableStateOf(true)

    private val _questionUiModel = mutableStateOf<List<QuestionUiModel>>(emptyList())
    val questionUiModel: State<List<QuestionUiModel>> get() = _questionUiModel

    val questionVisibilityMap: SnapshotStateMap<Int, Boolean> = mutableStateMapOf()

    val conditionsUtils = ConditionsUtils.getInstance()

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

            questionUiModel.value.forEach {
                questionVisibilityMap.put(it.questionId, !it.isConditional)
                if (it.options?.any { optionsUiModel -> optionsUiModel.isSelected == true } == true) {
                    questionVisibilityMap.put(it.questionId, true)
                }
            }
            val sourceTargetQuestionMapping = getConditionQuestionMappingsUseCase
                .invoke(
                    surveyId = surveyId,
                    sectionId = sectionId,
                    questionIdList = questionUiModel.value.map { it.questionId }
                )

            conditionsUtils.apply {
                setSourceTargetMap(sourceTargetQuestionMapping)
                setQuestionConditionMap(sourceTargetQuestionMapping)
                setConditionsUiModelList(sourceTargetQuestionMapping)
                setResponseMap(questionUiModel.value)
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
        referenceId: String
    ) {
        this.taskId = taskId
        this.sectionId = sectionId
        this.surveyId = surveyId
        this.formId = formId
        this.activityId = activityId
        this.activityConfigId = activityConfigId
        this.missionId = missionId
        this.referenceId = referenceId
    }
}