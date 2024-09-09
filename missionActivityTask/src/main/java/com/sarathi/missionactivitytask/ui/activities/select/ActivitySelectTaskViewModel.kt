package com.sarathi.missionactivitytask.ui.activities.select

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.nudge.core.BLANK_STRING
import com.nudge.core.CoreDispatchers
import com.nudge.core.DEFAULT_ID
import com.nudge.core.enums.ActivityTypeEnum
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
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityConfigUseCase
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.TaskScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
                ).firstOrNull()
                list?.let {
                    it.subjectId = task.subjectId
                    _questionUiModel.value[task.taskId ?: -1] = it
                }
            }
            if (!isActivityCompleted.value) {

                expandedIds.addAll(taskUiList.value.map { it.taskId })
            }
            withContext(CoreDispatchers.mainDispatcher) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
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
            grantId = grantID
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
                grantType = ActivityTypeEnum.getActivityTypeFromId(grantID).name,
                taskId = taskId,
                uriList = ArrayList(),
                activityReferenceId = activityConfigUiModelWithoutSurvey?.referenceId,
                activityReferenceType = activityConfigUiModelWithoutSurvey?.referenceType
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


}