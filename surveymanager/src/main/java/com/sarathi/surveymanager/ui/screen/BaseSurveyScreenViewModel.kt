package com.sarathi.surveymanager.ui.screen

import android.text.TextUtils
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.nudge.core.DEFAULT_ID
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.DISBURSED_AMOUNT_TAG
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.domain.use_case.FetchSurveyDataFromDB
import com.sarathi.dataloadingmangement.domain.use_case.FormEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.FormUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUiConfigUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetActivityUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetSectionListUseCase
import com.sarathi.dataloadingmangement.domain.use_case.GetTaskUseCase
import com.sarathi.dataloadingmangement.domain.use_case.MATStatusEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SaveSurveyAnswerUseCase
import com.sarathi.dataloadingmangement.domain.use_case.SurveyAnswerEventWriterUseCase
import com.sarathi.dataloadingmangement.domain.use_case.UpdateMissionActivityTaskStatusUseCase
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
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


            isTaskStatusCompleted()
            withContext(Dispatchers.Main) {
                onEvent(LoaderEvent.UpdateLoaderState(false))
            }
        }
    }

    fun saveAllAnswerIntoDB() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            questionUiModel.value.forEach { question ->
                saveQuestionAnswerIntoDb(question)

            }
            if (sanctionAmount != 0) {
                val formEntity = fromEUseCase.saveFormEData(
                    subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                    taskId = taskId,
                    surveyId = surveyId,
                    missionId = taskEntity?.missionId ?: -1,
                    activityId = taskEntity?.activityId ?: -1,
                    subjectType = subjectType,
                    referenceId = referenceId
                )
                formEventWriterUseCase.writeFormEvent(
                    surveyName = questionUiModel.value.firstOrNull()?.surveyName ?: BLANK_STRING,
                    formEntity = formEntity,

                    )
            }
            if (taskEntity?.status == SurveyStatusEnum.NOT_STARTED.name || taskEntity?.status == SurveyStatusEnum.NOT_AVAILABLE.name || taskEntity?.status == SurveyStatusEnum.COMPLETED.name) {
                taskStatusUseCase.markTaskInProgress(
                    taskId = taskId
                )
                taskStatusUseCase.markActivityInProgress(
                    missionId = taskEntity?.missionId ?: DEFAULT_ID,
                    activityId = taskEntity?.activityId ?: DEFAULT_ID,
                )
                taskStatusUseCase.markMissionInProgress(
                    missionId = taskEntity?.missionId ?: DEFAULT_ID,
                )
                taskEntity = getTaskUseCase.getTask(taskId)
                taskEntity?.let {
                    matStatusEventWriterUseCase.markMATStatus(
                        surveyName = questionUiModel.value.firstOrNull()?.surveyName
                            ?: BLANK_STRING,
                        subjectType = subjectType,
                        missionId = taskEntity?.missionId ?: DEFAULT_ID,
                        activityId = taskEntity?.activityId ?: DEFAULT_ID,
                        taskId = taskEntity?.taskId ?: DEFAULT_ID,
                        isFromRegenerate = false

                    )
                }

            }
            surveyAnswerEventWriterUseCase.invoke(
                questionUiModels = questionUiModel.value,
                subjectId = taskEntity?.subjectId ?: DEFAULT_ID,
                subjectType = subjectType,
                taskLocalId = taskEntity?.localTaskId ?: BLANK_STRING,
                referenceId = referenceId,
                grantId = grantID,
                grantType = granType,
                taskId = taskId,
                isFromRegenerate = false,
                activityId = activityConfig?.activityId.value(),
                activityReferenceId = activityConfig?.referenceId,
                activityReferenceType = activityConfig?.referenceType
            )

        }

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


    fun checkButtonValidation() {
        questionUiModel.value.filter { it.isMandatory }.forEach { questionUiModel ->
            if (questionUiModel.tagId.contains(DISBURSED_AMOUNT_TAG)) {
                val disbursedAmount =
                    if (TextUtils.isEmpty(questionUiModel.options?.firstOrNull()?.selectedValue)) 0 else questionUiModel.options?.firstOrNull()?.selectedValue?.toInt()
                if (sanctionAmount != 0 && (disbursedAmount
                        ?: 0) + totalRemainingAmount > sanctionAmount
                ) {
                    isButtonEnable.value = false
                    return
                }
            }
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
            saveAllAnswerIntoDB()
        }
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

    private fun isTaskStatusCompleted() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            isActivityNotCompleted.value = !getActivityUseCase.isAllActivityCompleted(
                missionId = taskEntity?.missionId ?: 0,
                activityId = taskEntity?.activityId ?: 0
            )
        }
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
}