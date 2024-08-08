package com.sarathi.missionactivitytask.ui.activities.select

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.BLANK_STRING
import com.nudge.core.CoreDispatchers
import com.nudge.core.DEFAULT_ID
import com.nudge.core.json
import com.sarathi.contentmodule.ui.content_screen.domain.usecase.FetchContentUseCase
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.domain.use_case.FetchAllDataUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GrantConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.GrantConfigUiModel
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel
import com.sarathi.missionactivitytask.ui.grantTask.domain.usecases.GetActivityConfigUseCase
import com.sarathi.missionactivitytask.ui.grantTask.viewmodel.TaskScreenViewModel
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private val grantConfigUseCase: GrantConfigUseCase
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

    private var taskEntity: ActivityTaskEntity? = null
    var taskUiList = mutableStateOf<List<TaskUiModel>>(emptyList())
    val questionList = ArrayList<QuestionUiModel>()
    var grantConfigUi = mutableStateOf(GrantConfigUiModel(null, "", 0))
    private val _questionUiModel = mutableStateOf<List<QuestionUiModel>>(emptyList())
    val questionUiModel: State<List<QuestionUiModel>> get() = _questionUiModel
    override fun <T> onEvent(event: T) {
        super.onEvent(event)
        when (event) {
            is InitDataEvent.InitActivitySelectTaskScreenState -> {
                initActivitySelectTaskScreen(event.missionId, event.activityId)
            }
        }
    }

    private fun initActivitySelectTaskScreen(missionId: Int, activityId: Int) {

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            taskUiList.value =
                getTaskUseCase.getActiveTasks(missionId = missionId, activityId = activityId)
            Log.d(
                "TAG",
                "initActivitySelectTaskScreen: activityConfigUiModel ${activityConfigUiModel?.json()} "
            )
//            setGrantComponentDTO(
//                surveyId = activityConfigUiModel?.surveyId ?: 0,
//                activityConfigId = activityConfigUiModel?.activityConfigId ?: 0
//            )
            taskUiList.value.forEach { task ->
                intiQuestions(
                    taskId = task.taskId,
                    surveyId = activityConfigUiModel?.surveyId ?: 0,
                    activityConfigId = activityConfigUiModel?.activityConfigId ?: 0,
                    sectionId = activityConfigUiModel?.sectionId ?: 0,
                    grantID = 0,
                    referenceId = BLANK_STRING
                )
            }
            Log.d("TAG", "initActivitySelectTaskScreen: ${taskUiList.value.json()} ")
        }
    }


    suspend fun setGrantComponentDTO(surveyId: Int, activityConfigId: Int) {
        grantConfigUi.value = grantConfigUseCase.getGrantComponentDTO(
            surveyId = surveyId,
            activityConfigId = activityConfigId
        )
    }

    private fun intiQuestions(
        taskId: Int,
        surveyId: Int,
        sectionId: Int,
        activityConfigId: Int,
        referenceId: String,
        grantID: Int
    ) {
        CoroutineScope(CoreDispatchers.ioDispatcher + exceptionHandler).launch {
            taskEntity = getTaskUseCase.getTask(taskId)
            Log.d("TAG", "intiQuestions: ${taskEntity?.json()}")
            Log.d(
                "TAG",
                "intiQuestions Values: $surveyId :: $sectionId :: $activityConfigId :: ${taskEntity?.subjectId ?: DEFAULT_ID} :: $referenceId :: $grantID "
            )
            val questions = fetchDataUseCase.invoke(
                surveyId = surveyId,
                sectionId = sectionId,
                subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                activityConfigId = activityConfigId,
                referenceId = referenceId,
                grantId = grantID
            )
            Log.d("TAG", "intiQuestions 1: ${questions?.json()}")

            questionList.addAll(questions)

            Log.d("TAG", "intiQuestions List: ${questionList?.json()}")

        }

    }

}