package com.sarathi.surveymanager.ui.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.nudge.core.BLANK_STRING
import com.nudge.core.DEFAULT_ID
import com.nudge.core.value
import com.sarathi.dataloadingmangement.NUMBER_ZERO
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.SurveyConfigEntity
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSurveyConfigFromDbUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyAnswerFormSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyCardModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import com.sarathi.dataloadingmangement.util.event.LoaderEvent
import com.sarathi.dataloadingmangement.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FormResponseSummaryViewModel @Inject constructor(
    private val fetchDataUseCase: FetchSurveyDataFromDB,
    private val taskStatusUseCase: UpdateMissionActivityTaskStatusUseCase,
    private val saveSurveyAnswerUseCase: SaveSurveyAnswerUseCase,
    private val surveyAnswerEventWriterUseCase: SurveyAnswerEventWriterUseCase,
    private val matStatusEventWriterUseCase: MATStatusEventWriterUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val getActivityUseCase: GetActivityUseCase,
    private val getActivityUiConfigUseCase: GetActivityUiConfigUseCase,
    private val getSurveyConfigFromDbUseCase: GetSurveyConfigFromDbUseCase
) : BaseViewModel() {

    var surveyId: Int = 0
    var sectionId: Int = 0
    var taskId: Int = 0
    var formId: Int = 0
    var activityConfigId: Int = 0
    var taskEntity: ActivityTaskEntity? = null
    var activityConfig: ActivityConfigEntity? = null
    var isActivityCompleted = false

    private val _questionUiModel = mutableStateOf<List<QuestionUiModel>>(emptyList())
    val questionUiModel: State<List<QuestionUiModel>> get() = _questionUiModel

    private val _formQuestionResponseMap =
        mutableStateMapOf<String, List<SurveyAnswerFormSummaryUiModel>>()
    val formQuestionResponseMap: SnapshotStateMap<String, List<SurveyAnswerFormSummaryUiModel>> get() = _formQuestionResponseMap

    val referenceIdsList = mutableStateListOf<String>()

    var surveyConfig = mutableMapOf<String, SurveyCardModel>()


    fun init(
        taskId: Int,
        surveyId: Int,
        sectionId: Int,
        formId: Int,
        activityConfigId: Int
    ) {
        this.taskId = taskId
        this.surveyId = surveyId
        this.sectionId = sectionId
        this.formId = formId
        this.activityConfigId = activityConfigId
    }

    override fun <T> onEvent(event: T) {
        when (event) {

            is InitDataEvent.InitDataState -> {
                onEvent(LoaderEvent.UpdateLoaderState(true))
                intiQuestions()
            }

            is LoaderEvent.UpdateLoaderState -> {
                _loaderState.value = _loaderState.value.copy(isLoaderVisible = event.showLoader)
            }
        }
    }

    private fun intiQuestions() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            taskEntity = getTaskUseCase.getTask(taskId)
            if (_questionUiModel.value.isEmpty()) {
                _questionUiModel.value = fetchDataUseCase.invokeFormQuestions(
                    surveyId = surveyId,
                    sectionId = sectionId,
                    subjectId = taskEntity?.subjectId.value(DEFAULT_ID),
                    activityConfigId = activityConfigId,
                    referenceId = BLANK_STRING,
                    grantId = NUMBER_ZERO,
                    formId = formId
                )
            }

            taskEntity?.let {
                activityConfig =
                    getActivityUiConfigUseCase.getActivityConfig(it.activityId, it.missionId)
                val savedAnswers = saveSurveyAnswerUseCase.getAllSaveAnswer(
                    activityConfigId = activityConfigId,
                    surveyId = surveyId,
                    sectionId = sectionId,
                    taskId = it.taskId,
                    grantId = NUMBER_ZERO
                )

                _formQuestionResponseMap.clear()
                _formQuestionResponseMap.putAll(savedAnswers.groupBy { it.referenceId })

                referenceIdsList.clear()
                referenceIdsList.addAll(formQuestionResponseMap.keys.toList())

                getSurveyConfigFromDbUseCase.invoke(it.missionId, it.activityId, surveyId, formId)
                    .also { surveyConfigEntityList ->
                        getSurveyConfig(surveyConfigEntityList)
                    }

                isActivityCompleted = getActivityUseCase.isAllActivityCompleted(
                    missionId = it.missionId.value(NUMBER_ZERO),
                    activityId = it.activityId.value(NUMBER_ZERO)
                )
            }

            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    private fun getSurveyConfig(surveyConfigEntityList: List<SurveyConfigEntity>) {
        val mSurveyConfig = mutableMapOf<String, SurveyCardModel>()
        surveyConfigEntityList.forEach { surveyConfigEntity ->
            mSurveyConfig.put(
                surveyConfigEntity.key,
                SurveyCardModel.getSurveyCarModel(surveyConfigEntity)
            )
        }
        surveyConfig = mSurveyConfig
    }

    fun deleteAnswer(referenceId: String?) {
        ioViewModelScope {
            referenceIdsList.remove(referenceId)

            val deleteCount = saveSurveyAnswerUseCase.deleteSurveyAnswer(
                surveyId = surveyId,
                sectionId = sectionId,
                taskId = taskId,
                referenceId = referenceId.value(),
            )

            if (deleteCount > 0) {
                surveyAnswerEventWriterUseCase.deleteSavedAnswerEvent(
                    surveyID = surveyId,
                    sectionId = sectionId,
                    surveyName = BLANK_STRING,
                    grantId = NUMBER_ZERO,
                    grantType = BLANK_STRING,
                    referenceId = referenceId.value(),
                    taskId = taskEntity?.taskId ?: DEFAULT_ID,
                    uriList = emptyList(),
                    taskLocalId = taskEntity?.localTaskId ?: BLANK_STRING,
                    subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                    subjectType = activityConfig?.subject.value(),
                    isFromRegenerate = false
                )
            }

        }
    }

}