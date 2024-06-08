package com.sarathi.surveymanager.ui.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.DEFAULT_ID
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.SurveyStatusEnum
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import com.sarathi.surveymanager.utils.events.EventWriterEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SurveyScreenViewModel @Inject constructor(
    private val fetchDataUseCase: FetchSurveyDataFromDB,
    private val taskStatusUseCase: UpdateTaskStatusUseCase,
    private val saveSurveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val surveyAnswerEventWriterUseCase: SurveyAnswerEventWriterUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val getTaskUseCase: GetTaskUseCase,
) : BaseViewModel() {
    private var surveyId: Int = 0
    private var sectionId: Int = 0
    private var taskId: Int = 0
    private var activityConfigId: Int = 0
    private var subjectType: String = BLANK_STRING
    private var referenceId: String = BLANK_STRING
    private var taskEntity: ActivityTaskEntity? = null

    val isButtonEnable = mutableStateOf<Boolean>(false)
    val isTaskCompleted = mutableStateOf<Boolean>(false)
    private val _questionUiModel = mutableStateOf<List<QuestionUiModel>>(emptyList())
    val questionUiModel: State<List<QuestionUiModel>> get() = _questionUiModel
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
            _questionUiModel.value = fetchDataUseCase.invoke(
                surveyId = surveyId,
                sectionId = sectionId,
                subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                activityConfigId = activityConfigId
                referenceId = referenceId
            )
            checkButtonValidation()
            isTaskStatusCompleted()
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    fun saveAnswerIntoDB() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            questionUiModel.value.forEach { question ->
                saveSurveyAnswerUseCase.saveSurveyAnswer(
                    question,
                    taskEntity?.subjectId ?: DEFAULT_ID,
                    taskId = taskId,
                    referenceId = referenceId
                )
                if (taskEntity?.status == SurveyStatusEnum.NOT_STARTED.name) {
                    taskStatusUseCase.markTaskInProgress(
                        subjectId = taskEntity?.subjectId ?: DEFAULT_ID, taskId = taskId
                    )
                    taskEntity = getTaskUseCase.getTask(taskId)
                    taskEntity?.let {
                        matStatusEventWriterUseCase.updateTaskStatus(
                            taskEntity = it,
                            referenceId.toString(),
                            subjectType
                        )
                    }

                }
                surveyAnswerEventWriterUseCase.invoke(
                    questionUiModel = questionUiModel.value,
                    subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                    subjectType = subjectType,
                    taskLocalId = taskEntity?.localTaskId ?: BLANK_STRING,
                    referenceId = referenceId,
                    uriList = listOf()
                )
            }

        }

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

    fun saveButtonClicked() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            saveAnswerIntoDB()
//            taskStatusUseCase.markTaskCompleted(
//                subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
//                taskId = taskEntity?.taskId ?: DEFAULT_ID
//            )
//            taskEntity?.let {
//                matStatusEventWriterUseCase.updateTaskStatus(
//                    taskEntity = it,
//                    referenceId.toString(),
//                    subjectType
//                )
//            }
        }
    }

    fun setPreviousScreenData(
        surveyId: Int,
        sectionId: Int,
        taskId: Int,
        subjectType: String,
        referenceId: String,
        activityConfigId: Int
    ) {
        this.surveyId = surveyId
        this.sectionId = sectionId
        this.taskId = taskId
        this.subjectType = subjectType
        this.referenceId = referenceId
        this.activityConfigId = activityConfigId
    }

    private fun isTaskStatusCompleted() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            isTaskCompleted.value = !taskStatusUseCase.getTaskStatus(
                userId = saveSurveyAnswerUseCase.getUserIdentifier(),
                taskId = taskId,
                subjectId = taskEntity?.subjectId ?: DEFAULT_ID
            ).equals(SurveyStatusEnum.COMPLETED.name)
        }


    }
}