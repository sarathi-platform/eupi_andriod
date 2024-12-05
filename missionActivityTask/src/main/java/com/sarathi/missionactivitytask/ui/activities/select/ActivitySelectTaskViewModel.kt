package com.sarathi.missionactivitytask.ui.activities.select

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.BLANK_STRING
import com.nudge.core.CoreDispatchers
import com.nudge.core.DEFAULT_ID
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.helper.TranslationEnum
import com.nudge.core.value
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardModel
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardSlots
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityConfigUseCase
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.TaskScreenViewModel
import com.sarathi.missionactivitytask.utils.StatusEnum
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class ActivitySelectTaskViewModel @Inject constructor(
    private val getActivityTaskUseCase: GetTaskUseCase,
    private val surveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
    private val getActivityConfigUseCase: GetActivityConfigUseCase,
    private val fetchContentUseCase: FetchContentUseCase,
    private val eventWriterUseCase: MATStatusEventWriterUseCase,
    private val fetchAllDataUseCase: FetchAllDataUseCase,
    private val fetchDataUseCase: FetchSurveyDataFromDB,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val surveyAnswerEventWriterUseCase: SurveyAnswerEventWriterUseCase,
    private val saveSurveyAnswerUseCase: SaveSurveyAnswerUseCase
) : TaskScreenViewModel(
    getActivityTaskUseCase,
    surveyAnswerUseCase,
    getActivityUiConfigUseCase,
    getActivityConfigUseCase,
    fetchContentUseCase,
    taskStatusUseCase,
    eventWriterUseCase,
    getActivityUseCase,
    fetchAllDataUseCase
) {

    var referenceId: String = BLANK_STRING
    var grantID: Int = 0
    var grantType: String = BLANK_STRING
    var taskUiList = mutableStateOf<List<TaskUiModel>>(emptyList())
    val questionList = arrayListOf<QuestionUiModel>()


    val expandedIds = mutableStateListOf<Int>()
    override fun <T> onEvent(event: T) {
        super.onEvent(event)
        when (event) {
            is InitDataEvent.InitActivitySelectTaskScreenState -> {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                initActivitySelectTaskScreen(event.missionId, event.activityId)
            }
        }
    }

    private fun initActivitySelectTaskScreen(missionId: Int, activityId: Int) {

        CoroutineScope(Dispatchers.IO).launch {
            delay(100)
            taskUiList.value =
                getTaskUseCase.getActiveTasks(missionId = missionId, activityId = activityId)
            expandedIds.clear()
            taskUiList.value.forEach { task ->
                val list = intiQuestions(
                    taskId = task.taskId,
                    surveyId = activityConfigUiModel?.surveyId ?: 0,
                    activityConfigId = activityConfigUiModel?.activityConfigId ?: 0,
                    sectionId = activityConfigUiModel?.sectionId ?: 0,
                    grantID = 0,
                    referenceId = BLANK_STRING
                )
                list.firstOrNull()?.let {
                    it.subjectId = task.subjectId
                    _questionUiModel.value[task.taskId ?: -1] = it
                }
            }

            withContext(CoreDispatchers.mainDispatcher) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    fun onExpandClicked(
        task: MutableMap.MutableEntry<Int, HashMap<String, TaskCardModel>>
    ) {
        if (expandedIds.contains(task.key)) {
            expandedIds.remove(task.key)
        } else {
            expandedIds.add(task.key)
        }
    }

    fun expandNextItem(currentIndex: Int, groupKey: String? = null) {
        val nextIndex = currentIndex + 1
        var taskIdList =
            if (groupKey != null)
                filterTaskMap[groupKey].value()
            else
                filterList.value.entries.toList().distinct()

        expandedIds.clear()

        if (nextIndex < taskIdList.size) {
            if (taskIdList[nextIndex].value[TaskCardSlots.TASK_STATUS.name]?.value == StatusEnum.NOT_STARTED.name) {
                expandedIds.add(taskIdList[nextIndex].key)
            } else {
                expandNextItem(nextIndex, groupKey)
            }
            return
        }

        if (nextIndex == taskIdList.size && groupKey != null) {

            val groupKeySet = filterTaskMap.keys
            val currentGroupIndex = groupKeySet.indexOf(groupKey)
            val nextGroupIndex = currentGroupIndex + 1

            if (nextGroupIndex < groupKeySet.size) {
                val nextGroupKey = groupKeySet.toList().get(nextGroupIndex)
                expandNextItem(-1, nextGroupKey)
            }
        }

    }

    private fun getFirstGroupWithNotStatedTask(): String? {
        if (filterTaskMap.isEmpty())
            return null

        val groupKeySet = filterTaskMap.keys
        var groupKey = groupKeySet.first()
        groupKeySet.forEach { key ->
            val groupContainsNotStatedTask = filterTaskMap[key]?.value()?.any { task ->
                task.value[TaskCardSlots.TASK_STATUS.name]?.value == StatusEnum.NOT_STARTED.name
            }
            if (groupContainsNotStatedTask == true) {
                groupKey = key
                return groupKey
            }
        }
        return groupKey
    }

    override fun expandFirstNotStartedItem() {
        if (!isActivityCompleted.value) {

            val firstGroupWithNotStatedTask = if (isGroupingApplied.value) {
                getFirstGroupWithNotStatedTask()
            } else
                null

            val firstNotStartedTaskIndex = filterList.value.entries.toList()
                .indexOfFirst { it.value[TaskCardSlots.TASK_STATUS.name]?.value == StatusEnum.NOT_STARTED.name }
            expandNextItem(firstNotStartedTaskIndex - 1, firstGroupWithNotStatedTask)

        }
    }

    private suspend fun intiQuestions(
        taskId: Int,
        surveyId: Int,
        sectionId: Int,
        activityConfigId: Int,
        referenceId: String,
        grantID: Int
    ): List<QuestionUiModel> {
        val taskEntity = getTaskUseCase.getTask(taskId)
        val questions = fetchDataUseCase.invoke(
            surveyId = surveyId,
            sectionId = sectionId,
            subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
            activityConfigId = activityConfigId,
            referenceId = referenceId,
            grantId = grantID,
            missionId = taskEntity?.missionId.value(DEFAULT_ID),
            activityId = taskEntity?.activityId.value(DEFAULT_ID)
        )
        return questions
    }

    fun saveSingleAnswerIntoDb(
        currentQuestionUiModel: QuestionUiModel,
        subjectType: String,
        taskId: Int
    ) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val taskEntity = getTaskUseCase.getTask(taskId)
            saveQuestionAnswerIntoDb(currentQuestionUiModel, taskEntity)
            surveyAnswerEventWriterUseCase.saveSurveyAnswerEvent(
                questionUiModel = currentQuestionUiModel,
                subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                subjectType = subjectType,
                taskLocalId = taskEntity?.localTaskId ?: BLANK_STRING,
                referenceId = referenceId,
                grantId = grantID,
                grantType = ActivityTypeEnum.SELECT.name,
                taskId = taskId,
                uriList = ArrayList(),
                activityId = activityConfigUiModelWithoutSurvey?.activityId.value(),
                activityReferenceId = activityConfigUiModelWithoutSurvey?.referenceId,
                activityReferenceType = activityConfigUiModelWithoutSurvey?.referenceType,
                isFromRegenerate = false
            )
        }
    }

    suspend fun saveQuestionAnswerIntoDb(
        question: QuestionUiModel,
        taskEntity: ActivityTaskEntity
    ) {
        saveSurveyAnswerUseCase.saveSurveyAnswer(
            questionUiModel = question,
            subjectId = taskEntity.subjectId ?: DEFAULT_ID,
            taskId = taskEntity.taskId,
            referenceId = referenceId,
            grantId = grantID,
            grantType = grantType
        )
    }

    fun updateTasStatus(
        taskId: Int,
        status: String,
    ) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {

            taskStatusUseCase.markActivityInProgress(missionId, activityId)
            taskStatusUseCase.markMissionInProgress(missionId)
            eventWriterUseCase.markMATStatus(
                missionId = missionId,
                activityId = activityId,
                taskId = taskId,
                subjectType = activityConfigUiModel?.subject ?: BLANK_STRING,
                surveyName = ActivityTypeEnum.SELECT.name
            )
            getTaskUseCase.updateTaskStatus(
                taskId = taskId,
                status = status
            )
            checkButtonValidation()
            updateProgress()
        }
    }

    override fun getScreenName(): TranslationEnum {
        return TranslationEnum.ActivitySelectTaskScreen
    }
}